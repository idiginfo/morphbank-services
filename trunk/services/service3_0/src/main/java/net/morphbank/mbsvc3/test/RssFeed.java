/*******************************************************************************
 * Copyright (c) 2011 Greg Riccardi, Guillaume Jimenez.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the GNU Public License v2.0
 *  which accompanies this distribution, and is available at
 *  http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *  
 *  Contributors:
 *  	Greg Riccardi - initial API and implementation
 * 	Guillaume Jimenez - initial API and implementation
 ******************************************************************************/
package net.morphbank.mbsvc3.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RssFeed {
	private RssHeader header;
	private List<RssEntry> entries;

	public void setHeader(RssHeader header) {
		this.header = header;
	}

	public void setEntries(List<RssEntry> entries) {
		this.entries = entries;
	}

	public RssHeader getHeader() {
		return header;
	}

	public List<RssEntry> getEntries() {
		return entries;
	}

	public static String formatDate(Calendar cal) {
		SimpleDateFormat sdf = new SimpleDateFormat(
				"EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
		return sdf.format(cal.getTime());
	}
}
