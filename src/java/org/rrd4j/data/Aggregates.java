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
import org.rrd4j.core.Util;

/**
 * Simple class which holds aggregated values (MIN, MAX, FIRST, LAST, AVERAGE and TOTAL). You
 * don't need to create objects of this class directly. Objects of this class are returned from
 * <code>getAggregates()</code> method in
 * {@link org.rrd4j.core.FetchData#getAggregates(String) FetchData} and
 * {@link DataProcessor#getAggregates(String)} DataProcessor} classes.
 */
public class Aggregates {
    double min = Double.NaN, max = Double.NaN;
    double first = Double.NaN, last = Double.NaN;
    double average = Double.NaN, total = Double.NaN;

    Aggregates() {
        // NOP;
    }

    /**
     * Returns the minimal value
     *
     * @return Minimal value
     */
    public double getMin() {
        return min;
    }

    /**
     * Returns the maximum value
     *
     * @return Maximum value
     */
    public double getMax() {
        return max;
    }

    /**
     * Returns the first falue
     *
     * @return First value
     */
    public double getFirst() {
        return first;
    }

    /**
     * Returns the last value
     *
     * @return Last value
     */
    public double getLast() {
        return last;
    }

    /**
     * Returns average
     *
     * @return Average value
     */
    public double getAverage() {
        return average;
    }

    /**
     * Returns total value
     *
     * @return Total value
     */
    public double getTotal() {
        return total;
    }

    /**
     * Returns single aggregated value for the give consolidation function
     *
     * @param consolFun Consolidation function: MIN, MAX, FIRST, LAST, AVERAGE, TOTAL. These constanst
     *                  are conveniently defined in the {@link org.rrd4j.ConsolFun ConsolFun} interface.
     *
     * @return Aggregated value
     *
     * @throws IllegalArgumentException Thrown if unsupported consolidation function is supplied
     */
    public double getAggregate(ConsolFun consolFun) {
        switch (consolFun) {
            case AVERAGE:
                return average;
            case FIRST:
                return first;
            case LAST:
                return last;
            case MAX:
                return max;
            case MIN:
                return min;
            case TOTAL:
                return total;
        }
        throw new IllegalArgumentException("Unknown consolidation function: " + consolFun);
    }

    /**
     * Returns String representing all aggregated values. Just for debugging purposes.
     * @return String containing all aggregated values
     */
    public String dump() {
        return "MIN=" + Util.formatDouble(min) + ", MAX=" + Util.formatDouble(max) + "\n" +
                "FIRST=" + Util.formatDouble(first) + ", LAST=" + Util.formatDouble(last) + "\n" +
                "AVERAGE=" + Util.formatDouble(average) + ", TOTAL=" + Util.formatDouble(total);
	}
}
