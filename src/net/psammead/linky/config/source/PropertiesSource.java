package net.psammead.linky.config.source;

import java.util.Properties;

import net.psammead.linky.config.ConfigSource;

public final class PropertiesSource implements ConfigSource {
	private final Properties	properties;

	public PropertiesSource(Properties properties) {
		this.properties	= properties;
	}

	public boolean has(String key) {
		return properties.getProperty(key) != null;
	}
	
	public String get(String key) {
		return properties.getProperty(key);
	}
}
