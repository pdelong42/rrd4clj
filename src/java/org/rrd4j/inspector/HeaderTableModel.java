/* ============================================================
 * Rrd4j : Pure java implementation of RRDTool's functionality
 * ============================================================
 *
 * Project Info:  http://www.rrd4j.org
 * Project Lead:  Mathias Bogaert (m.bogaert@memenco.com)
 *
 * (C) Copyright 2003, by Sasa Markovic.
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

package org.rrd4j.inspector;

import org.rrd4j.core.Header;
import org.rrd4j.core.RrdDb;

import javax.swing.table.AbstractTableModel;
import java.io.File;
import java.util.Date;

class HeaderTableModel extends AbstractTableModel {
	private static final Object[] DESCRIPTIONS = {
		"path", "signature", "step", "last timestamp",
		"datasources", "archives", "size"
	};
	private static final String[] COLUMN_NAMES = {"description", "value"};

	private File file;
	private Object[] values;

	public int getRowCount() {
		return DESCRIPTIONS.length;
	}

	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return DESCRIPTIONS[rowIndex];
		}
		else if (columnIndex == 1) {
			if (values != null) {
				return values[rowIndex];
			}
			else {
				return "--";
			}
		}
		return null;
	}

	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}


	void setFile(File newFile) {
		try {
			file = newFile;
			values = null;
			String path = file.getAbsolutePath();
			RrdDb rrd = new RrdDb(path, true);
			try {
				Header header = rrd.getHeader();
				String signature = header.getSignature();
				String step = "" + header.getStep();
				String lastTimestamp = header.getLastUpdateTime() + " [" +
						new Date(header.getLastUpdateTime() * 1000L) + "]";
				String datasources = "" + header.getDsCount();
				String archives = "" + header.getArcCount();
				String size = rrd.getRrdBackend().getLength() + " bytes";
				values = new Object[]{
					path, signature, step, lastTimestamp, datasources, archives, size
				};
			}
			finally {
				rrd.close();
			}
			fireTableDataChanged();
		}
		catch (Exception e) {
			Util.error(null, e);
		}
	}
}