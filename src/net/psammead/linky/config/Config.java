package net.psammead.linky.config;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import net.psammead.linky.config.source.BranchSource;
import net.psammead.linky.config.source.DefaultsSource;
import net.psammead.linky.config.source.EmptySource;
import net.psammead.linky.config.source.ExpandSource;
import net.psammead.linky.config.source.SourceFactory;

public final class Config {
	private final ConfigSource	source;
	
	public Config() {
		source	= new EmptySource();
	}

	private Config(ConfigSource source) {
		this.source	= source;
	}
	
	//------------------------------------------------------------------------------
	
	/** creates a Config for the system properties, defaulting to this Config */
	public Config systemProperties() {
		return new Config(new DefaultsSource(SourceFactory.systemProperties(), this.source));
	}
	
	/** creates a Config for a properties file, defaulting to this Config */
	public Config propertiesFile(File file) throws IOException {
		return new Config(new DefaultsSource(SourceFactory.propertiesFile(file), this.source));
	}
	
	/** creates a Config for a localized properties file, defaulting to this Config */
	public Config localizedPropertiesFile(File baseDir, String baseName, Locale locale) throws IOException {
		return new Config(new DefaultsSource(SourceFactory.localizedPropertiesFile(baseDir, baseName, locale), this.source));
	}
	
	/** create a Config that sees only a subtree with given prefix */
	public Config branch(String prefix) {
		return new Config(new BranchSource(source, prefix));
	}
	
	/** create a Config that replaces ${foo} with expansion.getProperty("foo")  */
	public Config expandOver(Config expansion) {
		return new Config(new ExpandSource(source, expansion.source));
	}
	
	/** create a Config that tries to find undefined properties in another Config */
	public Config defaultsTo(Config defaults) {
		return new Config(new DefaultsSource(source, defaults.source));
	}
	
	//------------------------------------------------------------------------------

	public boolean hasElement(String key) {
		return source.has(key);
	}
	
	public String getString(String key) {
		return source.get(key);
	}

	public int getInt(String key) {
		String value = source.get(key);
		try { return Integer.parseInt(value); }
		catch (Exception e) { throw new IllegalArgumentException("not an int: \"" + value + "\""); }
	}
	
	public long getLong(String key) {
		String value = source.get(key);
		try { return Long.parseLong(value); }
		catch (Exception e) { throw new IllegalArgumentException("not a long: \"" + value + "\""); }
	}

	public String[] getStringArray(String key) {
		return source.get(key).split(",");
	}

	public DateFormat getDateFormat(String key) {
		String value = source.get(key);
		try { return new SimpleDateFormat(value); }
		catch (Exception e) { throw new IllegalArgumentException("not a SimpleDateFormat: \"" + value + "\""); }
	}
	
	public Locale getLocale(String key) {
		String value = source.get(key);
		String[]	args	= value.split("_");
		switch (args.length) {
			case 1:	return new Locale(args[0]);
			case 2:	return new Locale(args[0], args[1]);
			case 3:	return new Locale(args[0], args[1], args[2]);
		}
		throw new IllegalArgumentException("not a Locale: \"" + value + "\"");
	}

	public TimeZone getTimeZone(String key) {
		String value = source.get(key);
		TimeZone	tz	= TimeZone.getTimeZone(value);
		if (tz == null)	throw new IllegalArgumentException("not a TimeZone: \"" + value + "\"");
		return tz;
	}

	//------------------------------------------------------------------------------

//	private byte asByte(String value) {
//	try { return Byte.parseByte(value); }
//	catch (Exception e) { throw new IllegalArgumentException("not a byte: \"" + value + "\""); }
//}
//
//private short asShort(String value) {
//	try { return Short.parseShort(value); }
//	catch (Exception e) { throw new IllegalArgumentException("not a short: \"" + value + "\""); }
//}
//
//private long asLong(String value) {
//	try { return Long.parseLong(value); }
//	catch (Exception e) { throw new IllegalArgumentException("not a long: \"" + value + "\""); }
//}
//
//private float asFloat(String value) {
//	try { return Float.parseFloat(value); }
//	catch (Exception e) { throw new IllegalArgumentException("not a float: \"" + value + "\""); }
//}
//
//private double asDouble(String value) {
//	try { return Double.parseDouble(value); }
//	catch (Exception e) { throw new IllegalArgumentException("not a double: \"" + value + "\""); }
//}
//
//private boolean asBoolean(String value) {
//	if ("true".equals(value)  || "yes".equals(value))	return true;
//	if ("false".equals(value) || "no".equals(value))	return false;
//	throw new IllegalArgumentException("not a boolean: \"" + value + "\"");
//}
//
//private char asChar(String value) {
//	if (value.length() != 1)	throw new IllegalArgumentException("not a char: \"" + value + "\"");
//	return value.charAt(0);
//}
}
