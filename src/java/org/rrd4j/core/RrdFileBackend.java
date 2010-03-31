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
import java.io.RandomAccessFile;

/**
 * Backend which is used to store RRD data to ordinary files on the disk. This was the
 * default factory before 1.4.0 version. This backend is based on the
 * RandomAccessFile class (java.io.* package).
 */
public class RrdFileBackend extends RrdBackend {
	/** read/write file status */
	protected boolean readOnly;
	/** radnom access file handle */
	protected RandomAccessFile file;

	/**
	 * Creates RrdFileBackend object for the given file path, backed by RandomAccessFile object.
	 * @param path Path to a file
	 * @param readOnly True, if file should be open in a read-only mode. False otherwise
	 * @throws IOException Thrown in case of I/O error
	 */
	protected RrdFileBackend(String path, boolean readOnly) throws IOException {
		super(path);
		this.readOnly = readOnly;
		this.file = new RandomAccessFile(path, readOnly ? "r" : "rw");
	}

	/**
	 * Closes the underlying RRD file.
	 *
	 * @throws IOException Thrown in case of I/O error
	 */
	public void close() throws IOException {
		file.close();
	}

	/**
	 * Returns canonical path to the file on the disk.
	 *
	 * @param path File path
	 * @return Canonical file path
	 * @throws IOException Thrown in case of I/O error
	 */
	public static String getCanonicalPath(String path) throws IOException {
		return Util.getCanonicalPath(path);
	}

	/**
	 * Returns canonical path to the file on the disk.
	 *
	 * @return Canonical file path
	 * @throws IOException Thrown in case of I/O error
	 */
	public String getCanonicalPath() throws IOException {
		return RrdFileBackend.getCanonicalPath(getPath());
	}

	/**
	 * Writes bytes to the underlying RRD file on the disk
	 *
	 * @param offset Starting file offset
	 * @param b      Bytes to be written.
	 * @throws IOException Thrown in case of I/O error
	 */
	protected void write(long offset, byte[] b) throws IOException {
		file.seek(offset);
		file.write(b);
	}

	/**
	 * Reads a number of bytes from the RRD file on the disk
	 *
	 * @param offset Starting file offset
	 * @param b      Buffer which receives bytes read from the file.
	 * @throws IOException Thrown in case of I/O error.
	 */
	protected void read(long offset, byte[] b) throws IOException {
		file.seek(offset);
		if (file.read(b) != b.length) {
			throw new IOException("Not enough bytes available in file " + getPath());
		}
	}

	/**
	 * Returns RRD file length.
	 *
	 * @return File length.
	 * @throws IOException Thrown in case of I/O error.
	 */
	public long getLength() throws IOException {
		return file.length();
	}

	/**
	 * Sets length of the underlying RRD file. This method is called only once, immediately
	 * after a new RRD file gets created.
	 *
	 * @param length Length of the RRD file
	 * @throws IOException Thrown in case of I/O error.
	 */
	protected void setLength(long length) throws IOException {
		file.setLength(length);
	}
}
