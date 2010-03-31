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
import java.util.concurrent.ConcurrentHashMap;

/**
 * Factory class which creates actual {@link RrdMemoryBackend} objects. Rrd4j's support
 * for in-memory RRDs is still experimental. You should know that all active RrdMemoryBackend
 * objects are held in memory, each backend object stores RRD data in one big byte array. This
 * implementation is therefore quite basic and memory hungry but runs very fast.<p>
 *
 * <p>Calling {@link RrdDb#close() close()} on RrdDb objects does not release any memory at all
 * (RRD data must be available for the next <code>new RrdDb(path)</code> call. To release allocated
 * memory, you'll have to call {@link #delete(java.lang.String) delete(path)} method of this class.</p>
 */
public class RrdMemoryBackendFactory extends RrdBackendFactory {
    /**
     * factory name, "MEMORY"
     */
    public static final String NAME = "MEMORY";

    private Map<String, RrdMemoryBackend> backends = new ConcurrentHashMap<String, RrdMemoryBackend>();

    /**
     * Creates RrdMemoryBackend object.
     *
     * @param id       Since this backend holds all data in memory, this argument is interpreted
     *                 as an ID for this memory-based storage.
     * @param readOnly This parameter is ignored
     *
     * @return RrdMemoryBackend object which handles all I/O operations
     *
     * @throws IOException Thrown in case of I/O error.
     */
    protected RrdBackend open(String id, boolean readOnly) throws IOException {
        RrdMemoryBackend backend;
        if (backends.containsKey(id)) {
            backend = backends.get(id);
        }
        else {
            backend = new RrdMemoryBackend(id);
            backends.put(id, backend);
        }
        return backend;
    }

    /**
     * Method to determine if a memory storage with the given ID already exists.
     *
     * @param id Memory storage ID.
     *
     * @return True, if such storage exists, false otherwise.
     */
    protected boolean exists(String id) {
        return backends.containsKey(id);
    }

    protected boolean shouldValidateHeader(String path) throws IOException {
        return false;
    }

    /**
     * Removes the storage with the given ID from the memory.
     *
     * @param id Storage ID
     *
     * @return True, if the storage with the given ID is deleted, false otherwise.
     */
    public boolean delete(String id) {
        if (backends.containsKey(id)) {
            backends.remove(id);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Returns the name of this factory.
     * @return Factory name (equals to "MEMORY").
     */
    public String getFactoryName() {
        return NAME;
    }
}
