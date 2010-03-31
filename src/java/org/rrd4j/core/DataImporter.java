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

import org.rrd4j.ConsolFun;

import java.io.IOException;

abstract class DataImporter {

	// header
	abstract String getVersion() throws IOException;
	abstract long getLastUpdateTime() throws IOException;
	abstract long getStep() throws IOException;
	abstract int getDsCount() throws IOException;
	abstract int getArcCount() throws IOException;

	// datasource
	abstract String getDsName(int dsIndex) throws IOException;
	abstract String getDsType(int dsIndex) throws IOException;
	abstract long getHeartbeat(int dsIndex) throws IOException;
	abstract double getMinValue(int dsIndex) throws IOException;
	abstract double getMaxValue(int dsIndex) throws IOException;

	// datasource state
	abstract double getLastValue(int dsIndex) throws IOException;
	abstract double getAccumValue(int dsIndex) throws IOException;
	abstract long getNanSeconds(int dsIndex) throws IOException;

    // archive
	abstract ConsolFun getConsolFun(int arcIndex) throws IOException;
	abstract double getXff(int arcIndex) throws IOException;
	abstract int getSteps(int arcIndex) throws IOException;
	abstract int getRows(int arcIndex) throws IOException;

	// archive state
	abstract double getStateAccumValue(int arcIndex, int dsIndex) throws IOException;
	abstract int getStateNanSteps(int arcIndex, int dsIndex) throws IOException;
	abstract double[] getValues(int arcIndex, int dsIndex) throws IOException;

	long getEstimatedSize() throws IOException {
		int dsCount = getDsCount();
		int arcCount = getArcCount();
		int rowCount = 0;
		for(int i = 0; i < arcCount; i++) {
			rowCount += getRows(i);
		}
		return RrdDef.calculateSize(dsCount, arcCount, rowCount);
	}

	void release() throws IOException {
		// NOP
	}

}