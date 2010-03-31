/* ============================================================
 * Rrd4j : Pure java implementation of RRDTool's functionality
 * ============================================================
 *
 * Project Info:  http://www.rrd4j.org
 * Project Lead:  Mathias Bogaert (m.bogaert@memenco.com)
 *
 * (C) Copyright 2003-2007, by Sasa Markovic.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * Developers:    Sasa Markovic
 *
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package org.rrd4j.cmd;

import org.rrd4j.core.*;
import org.rrd4j.DsType;

import java.io.IOException;

class RrdTuneCmd extends RrdToolCmd {
	String getCmdType() {
		return "tune";
	}

	Object execute() throws IOException {
		String[] heartbeats = getMultipleOptionValues("h", "heartbeat");
		String[] minimums = getMultipleOptionValues("i", "minimum");
		String[] maximums = getMultipleOptionValues("a", "maximum");
		String[] dsTypes = getMultipleOptionValues("d", "data-source-type");
		String[] dsNames = getMultipleOptionValues("r", "data-source-rename");
		String[] words = getRemainingWords();
		if(words.length < 2) {
			throw new IllegalArgumentException("File name not specified");
		}
		if(words.length > 2) {
			throw new IllegalArgumentException("Unexpected token encountered: " + words[2]);
		}
		String path = words[1];
		RrdDb rrd = getRrdDbReference(path);
		try {
			// heartbeat
            for (String heartbeat : heartbeats) {
                tuneHeartbeat(rrd, heartbeat);
            }
			// minimum
            for (String minimum : minimums) {
                tuneMinimum(rrd, minimum);
            }
			// maximum
            for (String maximum : maximums) {
                tuneMaximum(rrd, maximum);
            }
			// rename
            for (String dsName : dsNames) {
                tuneName(rrd, dsName);
            }
			// type
            for (String dsType : dsTypes) {
                tuneType(rrd, dsType);
            }
			// post festum
			if(heartbeats.length == 0 && minimums.length == 0 && maximums.length == 0 &&
				dsTypes.length == 0 && dsNames.length == 0) {
				dump(rrd);
			}
		}
		finally {
			releaseRrdDbReference(rrd);
		}
		return path;
	}

	private void tuneHeartbeat(RrdDb rrd, String heartbeatStr) throws IOException {
		String[] tokens = new ColonSplitter(heartbeatStr).split();
		if(tokens.length != 2) {
			throw new IllegalArgumentException("Invalid suntax in: " + heartbeatStr);
		}
		String dsName = tokens[0];
		long heartbeat = Long.parseLong(tokens[1]);
		Datasource ds = rrd.getDatasource(dsName);
		ds.setHeartbeat(heartbeat);
	}

	private void tuneMinimum(RrdDb rrd, String minimumStr) throws IOException {
		String[] tokens = new ColonSplitter(minimumStr).split();
		if(tokens.length != 2) {
			throw new IllegalArgumentException("Invalid suntax in: " + minimumStr);
		}
		String dsName = tokens[0];
		double minValue = Util.parseDouble(tokens[1]);
		Datasource ds = rrd.getDatasource(dsName);
		ds.setMinValue(minValue, false);
	}

	private void tuneMaximum(RrdDb rrd, String maximumStr) throws IOException {
		String[] tokens = new ColonSplitter(maximumStr).split();
		if(tokens.length != 2) {
			throw new IllegalArgumentException("Invalid suntax in: " + maximumStr);
		}
		String dsName = tokens[0];
		double maxValue = Util.parseDouble(tokens[1]);
		Datasource ds = rrd.getDatasource(dsName);
		ds.setMaxValue(maxValue, false);
	}

	private void tuneName(RrdDb rrd, String nameStr) throws IOException {
		String[] tokens = new ColonSplitter(nameStr).split();
		if(tokens.length != 2) {
			throw new IllegalArgumentException("Invalid suntax in: " + nameStr);
		}
		String oldName = tokens[0], newName = tokens[1];
		Datasource ds = rrd.getDatasource(oldName);
		ds.setDsName(newName);
	}

	private void tuneType(RrdDb rrd, String typeStr) throws IOException {
		String[] tokens = new ColonSplitter(typeStr).split();
		if(tokens.length != 2) {
			throw new IllegalArgumentException("Invalid suntax in: " + typeStr);
		}
		String dsName = tokens[0];
		DsType dsType = DsType.valueOf(tokens[1]);
		Datasource ds = rrd.getDatasource(dsName);
		ds.setDsType(dsType);
	}

	private void dump(RrdDb rrd) throws IOException {
		StringBuilder line = new StringBuilder();
		int n = rrd.getDsCount();
		for(int i = 0; i < n; i++) {
			Datasource ds = rrd.getDatasource(i);
			line.append("DS[");
			line.append(ds.getDsName());
			line.append("] typ: ");
			line.append(ds.getDsType());
			while(line.length() < 24) {
				line.append(' ');
			}
			line.append("hbt: ");
			line.append(ds.getHeartbeat());
			while(line.length() < 40) {
				line.append(' ');
			}
			line.append("min: ");
			line.append(String.format("%-11f", ds.getMinValue()));
			line.append("max: ");
			line.append(String.format("%-11f", ds.getMaxValue()));
			println(line.toString());
			line.setLength(0);
		}
	}
}
