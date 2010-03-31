/* ============================================================
 * Rrd4j : Pure java implementation of RRDTool's functionality
 * ============================================================
 *
 * Project Info:  http://www.rrd4j.org
 * Project Lead:  Mathias Bogaert (m.bogaert@memenco.com)
 *
 * Developers:    Sasa Markovic
 *
 *
 * (C) Copyright 2003-2007, by Sasa Markovic.
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
package org.rrd4j.graph;

import org.rrd4j.data.DataProcessor;
import org.rrd4j.ConsolFun;

class SDef extends Source {
    private String defName;
    private ConsolFun consolFun;

    SDef(String name, String defName, ConsolFun consolFun) {
        super(name);
        this.defName = defName;
        this.consolFun = consolFun;
    }

    void requestData(DataProcessor dproc) {
        dproc.addDatasource(name, defName, consolFun);
    }
}
