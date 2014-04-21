package net.psammead.linky.config.source;

import net.psammead.linky.config.ConfigSource;

public final class BranchSource implements ConfigSource {
	private final ConfigSource	source;
	private final String		prefix;

	public BranchSource(ConfigSource source, String prefix) {
		this.source = source;
		this.prefix = prefix;
	}

	public boolean has(String key) {
		return source.has(prefix + key);
	}
	
	public String get(String key) {
		return source.get(prefix + key);
	}
}
