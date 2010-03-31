/* ============================================================
 * Rrd4j : Pure java implementation of RRDTool's functionality
 * ============================================================
 *
 * Project Info:  http://www.rrd4j.org
 * Project Lead:  Mathias Bogaert (m.bogaert@memenco.com)
 *
 * (C) Copyright 2003-2007, by Sasa Markovic.
 *
 * Developers:    Sasa Markovic
 *
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package org.rrd4j.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This class should be used to synchronize access to RRD files
 * in a multithreaded environment. This class should be also used to prevent opening of
 * too many RRD files at the same time (thus avoiding operating system limits).
 */
public class RrdDbPool {
	/**
	 * Initial capacity of the pool i.e. maximum number of simultaneously open RRD files. The pool will
	 * never open too many RRD files at the same time.
	 */
	public static final int INITIAL_CAPACITY = 200;

    private static class RrdDbPoolSingletonHolder {
        static RrdDbPool instance = new RrdDbPool();
    }

	private int capacity = INITIAL_CAPACITY;
	private Map<String, RrdEntry> rrdMap = new HashMap<String, RrdEntry>(INITIAL_CAPACITY);

	/**
	 * Creates a single instance of the class on the first call,
     * or returns already existing one. Uses Initialization On Demand Holder idiom.
	 *
	 * @return Single instance of this class
	 * @throws RuntimeException Thrown if the default RRD backend is not derived from the {@link RrdFileBackendFactory}
	 */
	public static RrdDbPool getInstance() {
		return RrdDbPoolSingletonHolder.instance;
	}

	private RrdDbPool() {
		RrdBackendFactory factory = RrdBackendFactory.getDefaultFactory();

        if (!(factory instanceof RrdFileBackendFactory)) {
			throw new RuntimeException("Cannot create instance of " + getClass().getName() + " with " +
					"a default backend factory not derived from RrdFileBackendFactory");
		}
	}

	/**
	 * Requests a RrdDb reference for the given RRD file path.<p>
	 * <ul>
	 * <li>If the file is already open, previously returned RrdDb reference will be returned. Its usage count
	 * will be incremented by one.
	 * <li>If the file is not already open and the number of already open RRD files is less than
	 * {@link #INITIAL_CAPACITY}, the file will be open and a new RrdDb reference will be returned.
	 * If the file is not already open and the number of already open RRD files is equal to
	 * {@link #INITIAL_CAPACITY}, the method blocks until some RRD file is closed.
	 * </ul>
	 *
	 * @param path Path to existing RRD file
	 * @return reference for the give RRD file
	 * @throws IOException  Thrown in case of I/O error
	 */
	public synchronized RrdDb requestRrdDb(String path) throws IOException {
		String canonicalPath = Util.getCanonicalPath(path);
		while (!rrdMap.containsKey(canonicalPath) && rrdMap.size() >= capacity) {
			try {
				wait();
			}
			catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		if (rrdMap.containsKey(canonicalPath)) {
			// already open, just increase usage count
			RrdEntry entry = rrdMap.get(canonicalPath);
			entry.count++;
			return entry.rrdDb;
		}
		else {
			// not open, open it now and add to the map
			RrdDb rrdDb = new RrdDb(canonicalPath);
			rrdMap.put(canonicalPath, new RrdEntry(rrdDb));
			return rrdDb;
		}
	}

	/**
	 * Requests a RrdDb reference for the given RRD file definition object.<p>
	 * <ul>
	 * <li>If the file with the path specified in the RrdDef object is already open,
	 * the method blocks until the file is closed.
	 * <li>If the file is not already open and the number of already open RRD files is less than
	 * {@link #INITIAL_CAPACITY}, a new RRD file will be created and a its RrdDb reference will be returned.
	 * If the file is not already open and the number of already open RRD files is equal to
	 * {@link #INITIAL_CAPACITY}, the method blocks until some RRD file is closed.
	 * </ul>
	 *
	 * @param rrdDef Definition of the RRD file to be created
	 * @return Reference to the newly created RRD file
	 * @throws IOException  Thrown in case of I/O error
	 */
	public synchronized RrdDb requestRrdDb(RrdDef rrdDef) throws IOException {
		String canonicalPath = Util.getCanonicalPath(rrdDef.getPath());
		while (rrdMap.containsKey(canonicalPath) || rrdMap.size() >= capacity) {
			try {
				wait();
			}
			catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		RrdDb rrdDb = new RrdDb(rrdDef);
		rrdMap.put(canonicalPath, new RrdEntry(rrdDb));
		return rrdDb;
	}

	/**
	 * Requests a RrdDb reference for the given path. The file will be created from
	 * external data (from XML dump, RRD file or RRDTool's binary RRD file).<p>
	 * <ul>
	 * <li>If the file with the path specified is already open,
	 * the method blocks until the file is closed.
	 * <li>If the file is not already open and the number of already open RRD files is less than
	 * {@link #INITIAL_CAPACITY}, a new RRD file will be created and a its RrdDb reference will be returned.
	 * If the file is not already open and the number of already open RRD files is equal to
	 * {@link #INITIAL_CAPACITY}, the method blocks until some RRD file is closed.
	 * </ul>
	 *
	 * @param path Path to RRD file which should be created
	 * @param sourcePath Path to external data which is to be converted to Rrd4j's native RRD file format
	 * @return Reference to the newly created RRD file
	 * @throws IOException  Thrown in case of I/O error
	 */
	public synchronized RrdDb requestRrdDb(String path, String sourcePath) throws IOException {
		String canonicalPath = Util.getCanonicalPath(path);
		while (rrdMap.containsKey(canonicalPath) || rrdMap.size() >= capacity) {
			try {
				wait();
			}
			catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		RrdDb rrdDb = new RrdDb(canonicalPath, sourcePath);
		rrdMap.put(canonicalPath, new RrdEntry(rrdDb));
		return rrdDb;
	}

	/**
	 * Releases RrdDb reference previously obtained from the pool. When a reference is released, its usage
	 * count is decremented by one. If usage count drops to zero, the underlying RRD file will be closed.
	 *
	 * @param rrdDb RrdDb reference to be returned to the pool
	 * @throws IOException  Thrown in case of I/O error
	 */
	public synchronized void release(RrdDb rrdDb) throws IOException {
		// null pointer should not kill the thread, just ignore it
		if (rrdDb == null) {
			return;
		}
		String canonicalPath = Util.getCanonicalPath(rrdDb.getPath());
		if (!rrdMap.containsKey(canonicalPath)) {
			throw new IllegalStateException("Could not release [" + canonicalPath + "], the file was never requested");
		}
		RrdEntry entry = rrdMap.get(canonicalPath);
		if (--entry.count <= 0) {
			// no longer used
			rrdMap.remove(canonicalPath);
			notifyAll();
			entry.rrdDb.close();
		}
	}

	/**
	 * Returns the maximum number of simultaneously open RRD files.
	 *
	 * @return maximum number of simultaneously open RRD files
	 */
	public synchronized int getCapacity() {
		return capacity;
	}

	/**
	 * Sets the maximum number of simultaneously open RRD files.
	 *
	 * @param capacity Maximum number of simultaneously open RRD files.
	 */
	public synchronized void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	/**
	 * Returns an array of open file names.
	 *
	 * @return Array with canonical paths to open RRD files held in the pool.
	 */
	public synchronized String[] getOpenFiles() {
		return rrdMap.keySet().toArray(new String[0]);
	}

	/**
	 * Returns the number of open RRD files.
	 *
	 * @return Number of currently open RRD files held in the pool.
	 */
	public synchronized int getOpenFileCount() {
		return rrdMap.size();
	}

	class RrdEntry {
		RrdDb rrdDb;
		int count;

		RrdEntry(RrdDb rrdDb) {
			this.rrdDb = rrdDb;
			this.count = 1;
		}
	}
}