package net.psammead.linky.parser.type;

import net.psammead.linky.parser.ParameterType;


/** a integral Number */
public final class NumberType implements ParameterType {
	public Object coerce(String value) { return Integer.parseInt(value); }
	public String regexp() { return "([0-9]+)"; }
}
