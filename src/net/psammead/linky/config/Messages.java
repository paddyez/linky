package net.psammead.linky.config;

import java.text.DateFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.TimeZone;


public final class Messages {
	private final Config	config;
	private final Locale	locale;
	private final TimeZone	timeZone;

	public Messages(Config config, Locale locale, TimeZone timeZone) {
		this.config		= config;
		this.locale		= locale;
		this.timeZone	= timeZone;
	}

	public String get(String key, Object... args) {
		// create a MessageFormat
		String			pattern		= config.getString(key);
		MessageFormat	format		= new MessageFormat(pattern, locale);

		// update the TimeZone of all DateFormats
		Format[]	formats	= format.getFormats();
		for (int i = 0; i<formats.length; i++) {
			Format	sub	= formats[i];
			if (sub instanceof DateFormat)	((DateFormat)sub).setTimeZone(timeZone);
		}

		// and finally - format
		return format.format(args);
	}
}
