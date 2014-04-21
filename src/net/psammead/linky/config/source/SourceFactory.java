package net.psammead.linky.config.source;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

import net.psammead.linky.config.ConfigSource;
import net.psammead.util.IOUtil;

public final class SourceFactory {
	private SourceFactory() {}
	
	public static ConfigSource systemProperties() {
		return new PropertiesSource(System.getProperties());
	}

	public static ConfigSource propertiesFile(File file) throws IOException {
		InputStream	input	= null;
		try { 
			input	= new FileInputStream(file); 
			Properties	properties	= new Properties();
			properties.load(input);
			return new PropertiesSource(properties);
		}
		finally {
			IOUtil.closeSilent(input);
		}
	}
	
	public static ConfigSource localizedPropertiesFile(File baseDir, String baseName, Locale locale) throws IOException {
		InputStream	in	= null;
		try {
			File		file		= localizedFile(baseDir, baseName, locale);
			InputStream	input		= new FileInputStream(file);
			Properties	properties	= new Properties();
			properties.load(input);
			return new PropertiesSource(properties);
		}
		finally {
			IOUtil.closeSilent(in);
		}
	}
	
	/** pluginDir / baseName _ language _ country _ variant */
	private static File localizedFile(File baseDir, String baseName, Locale locale) throws IOException {
		String	language	= locale.getLanguage();
		String	country		= locale.getCountry();
		String	variant		= locale.getVariant();
		
		File	file;
		if (variant.length() != 0) {
			file	= new File(baseDir, baseName + "_" + language + "_" + country + "_" + language + ".properties");
			if (file.exists())	return file;
		}
		if (country.length() != 0) {
			file	= new File(baseDir, baseName + "_" + language + "_" + country + ".properties");
			if (file.exists())	return file;
		}
		if (language.length() != 0) {
			file	= new File(baseDir, baseName + "_" + language + ".properties");
			if (file.exists())	return file;
		}
		file	= new File(baseDir, baseName + ".properties");
		if (file.exists())	return file;
		
		throw new IOException("cannot locate resource named " + baseName + " in directory " + baseDir + " for locale " + locale);
	}
}
