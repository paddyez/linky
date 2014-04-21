package net.psammead.linky.parser;

public interface ParameterType {
	Object coerce(String value);
	String regexp();
}
