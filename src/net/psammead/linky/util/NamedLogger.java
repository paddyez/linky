package net.psammead.linky.util;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public final class NamedLogger {
	private static final String	DATE_FORMAT	= "HH:mm dd.MM.yy";
	
	private final String	name;
	
	public NamedLogger(String name) {
		this.name	= name;
	}
	
	/** print a log line */
	public void message(String s) {
		DateFormat	dateFormat	= new SimpleDateFormat(DATE_FORMAT);
		String		dateString	= dateFormat.format(new Date());
		System.out.println(dateString + "\t" + name + "\t" + s);
	}

	/** log an exception */
	public void error(Throwable t) {
		t.printStackTrace();
	}
}
