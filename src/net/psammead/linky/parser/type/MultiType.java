package net.psammead.linky.parser.type;

import net.psammead.linky.parser.ParameterType;


/** a String[] with abitrary length composed of whitespace-separated words */ 
public final class MultiType implements ParameterType {
	//### should know string in ""
	//### (?:X)  	X, as a non-capturing group
	
	public String[] coerce(String value) { return value.trim().split("\\s+"); }
	public String regexp() { return "(.+?)"; }
}
