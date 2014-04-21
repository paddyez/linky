package net.psammead.linky.config;

public interface ConfigSource {
	boolean has(String key);
	String get(String key);
}
