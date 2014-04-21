package net.psammead.linky.parser.type;

import net.psammead.linky.parser.ParameterType;


/** a single word */
public final class WordType implements ParameterType {
	public String coerce(String value) { return value; }
	public String regexp() { return "(\\S+)"; }
}
