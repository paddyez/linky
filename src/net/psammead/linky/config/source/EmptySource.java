package net.psammead.linky.config.source;

import net.psammead.linky.config.ConfigSource;

public final class EmptySource implements ConfigSource {
	public EmptySource() {}

	public boolean has(String key) {
		return false;
	}
	
	// TODO how about the complete path in the error message?
	public String get(String key) {
		throw new RuntimeException("missing property: " + key);
	}
}
