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

package org.rrd4j.core.timespec;

import org.rrd4j.core.Util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Simple class to represent time obtained by parsing at-style date specification (described
 * in detail on the rrdfetch man page. See javadoc for {@link org.rrd4j.core.timespec.TimeParser}
 * for more information.
 */
public class TimeSpec {
	static final int TYPE_ABSOLUTE = 0;
	static final int TYPE_START = 1;
	static final int TYPE_END = 2;

	int type = TYPE_ABSOLUTE;
	int year, month, day, hour, min, sec;
	int wday;
	int dyear, dmonth, dday, dhour, dmin, dsec;

	String dateString;

	TimeSpec context;

	TimeSpec(String dateString) {
		this.dateString = dateString;
	}

	void localtime(long timestamp) {
		GregorianCalendar date = new GregorianCalendar();
		date.setTime(new Date(timestamp * 1000L));
		year = date.get(Calendar.YEAR) - 1900;
		month = date.get(Calendar.MONTH);
		day = date.get(Calendar.DAY_OF_MONTH);
		hour = date.get(Calendar.HOUR_OF_DAY);
		min = date.get(Calendar.MINUTE);
		sec = date.get(Calendar.SECOND);
		wday = date.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
	}

    GregorianCalendar getTime() {
        GregorianCalendar gc;
        // absoulte time, this is easy
        if (type == TYPE_ABSOLUTE) {
            gc = new GregorianCalendar(year + 1900, month, day, hour, min, sec);
        }
        // relative time, we need a context to evaluate it
        else if (context != null && context.type == TYPE_ABSOLUTE) {
            gc = context.getTime();
        }
        // how would I guess what time it was?
        else {
            throw new IllegalStateException("Relative times like '" +
                    dateString + "' require proper absolute context to be evaluated");
        }
        gc.add(Calendar.YEAR, dyear);
        gc.add(Calendar.MONTH, dmonth);
        gc.add(Calendar.DAY_OF_MONTH, dday);
        gc.add(Calendar.HOUR_OF_DAY, dhour);
        gc.add(Calendar.MINUTE, dmin);
        gc.add(Calendar.SECOND, dsec);
        return gc;
    }

	/**
	 * Returns the corresponding timestamp (seconds since Epoch). Example:<p>
	 * <pre>
	 * TimeParser p = new TimeParser("now-1day");
	 * TimeSpec ts = p.parse();
	 * System.out.println("Timestamp was: " + ts.getTimestamp();
	 * </pre>
	 * @return Timestamp (in seconds, no milliseconds)
	 */
	public long getTimestamp() {
		return Util.getTimestamp(getTime());
	}

	String dump() {
		return (type == TYPE_ABSOLUTE? "ABSTIME": type == TYPE_START? "START": "END") +
				": " + year + "/" + month + "/" + day +
				"/" + hour + "/" + min + "/" + sec + " (" +
				dyear + "/" + dmonth + "/" + dday +
				"/" + dhour + "/" + dmin + "/" + dsec + ")";
	}

	/**
	 * Use this static method to resolve relative time references and obtain the corresponding
	 * Calendar objects. Example:<p>
	 * <pre>
	 * TimeParser pStart = new TimeParser("now-1month"); // starting time
	 * TimeParser pEnd = new TimeParser("start+1week");  // ending time
	 * TimeSpec specStart = pStart.parse();
	 * TimeSpec specEnd = pEnd.parse();
	 * GregorianCalendar[] gc = TimeSpec.getTimes(specStart, specEnd);
	 * </pre>
	 * @param spec1 Starting time specification
	 * @param spec2 Ending time specification
	 * @return Two element array containing Calendar objects
	 */
	public static Calendar[] getTimes(TimeSpec spec1, TimeSpec spec2) {
		if(spec1.type == TYPE_START || spec2.type == TYPE_END) {
			throw new IllegalArgumentException("Recursive time specifications not allowed");
		}
		spec1.context = spec2;
		spec2.context = spec1;
		return new Calendar[] {
			spec1.getTime(),
			spec2.getTime()
		};
	}

	/**
	 * Use this static method to resolve relative time references and obtain the corresponding
	 * timestamps (seconds since epoch). Example:<p>
	 * <pre>
	 * TimeParser pStart = new TimeParser("now-1month"); // starting time
	 * TimeParser pEnd = new TimeParser("start+1week");  // ending time
	 * TimeSpec specStart = pStart.parse();
	 * TimeSpec specEnd = pEnd.parse();
	 * long[] ts = TimeSpec.getTimestamps(specStart, specEnd);
	 * </pre>
	 * @param spec1 Starting time specification
	 * @param spec2 Ending time specification
	 * @return array containing two timestamps (in seconds since epoch)
	 */
	public static long[] getTimestamps(TimeSpec spec1, TimeSpec spec2) {
		Calendar[] gcs = getTimes(spec1, spec2);
		return new long[] {
			Util.getTimestamp(gcs[0]), Util.getTimestamp(gcs[1])
		};
	}
}

