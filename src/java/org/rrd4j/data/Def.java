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

package org.rrd4j.data;

import org.rrd4j.ConsolFun;
import org.rrd4j.core.FetchData;
import org.rrd4j.core.Util;

import java.io.IOException;

class Def extends Source {
    private String path, dsName, backend;
    private ConsolFun consolFun;
    private FetchData fetchData;

    Def(String name, FetchData fetchData) {
        this(name, null, name, null, null);
        setFetchData(fetchData);
    }

    Def(String name, String path, String dsName, ConsolFun consolFunc) {
        this(name, path, dsName, consolFunc, null);
    }

    Def(String name, String path, String dsName, ConsolFun consolFunc, String backend) {
        super(name);
        this.path = path;
        this.dsName = dsName;
        this.consolFun = consolFunc;
        this.backend = backend;
    }

    String getPath() {
        return path;
    }

    String getCanonicalPath() throws IOException {
        return Util.getCanonicalPath(path);
    }

    String getDsName() {
        return dsName;
    }

    ConsolFun getConsolFun() {
        return consolFun;
    }

    String getBackend() {
        return backend;
    }

    boolean isCompatibleWith(Def def) throws IOException {
        return getCanonicalPath().equals(def.getCanonicalPath()) &&
                getConsolFun() == def.consolFun &&
                ((backend == null && def.backend == null) ||
                        (backend != null && def.backend != null && backend.equals(def.backend)));
    }

    void setFetchData(FetchData fetchData) {
        this.fetchData = fetchData;
    }

    long[] getRrdTimestamps() {
        return fetchData.getTimestamps();
    }

    double[] getRrdValues() {
        return fetchData.getValues(dsName);
    }

    long getArchiveEndTime() {
        return fetchData.getArcEndTime();
    }

    long getFetchStep() {
        return fetchData.getStep();
    }

    Aggregates getAggregates(long tStart, long tEnd) {
        long[] t = getRrdTimestamps();
        double[] v = getRrdValues();
        return new Aggregator(t, v).getAggregates(tStart, tEnd);
    }

    double getPercentile(long tStart, long tEnd, double percentile) {
        long[] t = getRrdTimestamps();
        double[] v = getRrdValues();
        return new Aggregator(t, v).getPercentile(tStart, tEnd, percentile);
    }

    boolean isLoaded() {
        return fetchData != null;
    }
}
