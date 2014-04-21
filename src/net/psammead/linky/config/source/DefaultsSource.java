package net.psammead.linky.config.source;

import net.psammead.linky.config.ConfigSource;

public final class DefaultsSource implements ConfigSource {
	private final ConfigSource	source;
	private final ConfigSource	fallback;

	public DefaultsSource(ConfigSource source, ConfigSource fallback) {
		this.source		= source;
		this.fallback	= fallback;
	}

	public boolean has(String key) {
		return source.has(key) || fallback.has(key);
	}
	
	public String get(String key) {
		if (source.has(key))	return source.get(key);
		else					return fallback.get(key);
	}
}
