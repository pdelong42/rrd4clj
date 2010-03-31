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

class RrdLong extends RrdPrimitive {
	private long cache;
	private boolean cached = false;

	RrdLong(RrdUpdater updater, boolean isConstant) throws IOException {
		super(updater, RrdPrimitive.RRD_LONG, isConstant);
	}

	RrdLong(RrdUpdater updater) throws IOException {
		this(updater, false);
	}

	void set(long value) throws IOException {
		if(!isCachingAllowed()) {
			writeLong(value);
		}
		// caching allowed
		else if(!cached || cache != value) {
			// update cache
			writeLong(cache = value);
			cached = true;
		}
	}

	long get() throws IOException {
		return cached? cache: readLong();
	}
}
