package net.psammead.linky.parser.type;

import net.psammead.linky.parser.ParameterType;


/** a String with abitrary length */ 
public final class TextType implements ParameterType {
	//### should know string in ""
	//### (?:X)  	X, as a non-capturing group
	
	public String coerce(String value) { return value; }
	public String regexp() { return "(.+?)"; }
}
