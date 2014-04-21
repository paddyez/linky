package net.psammead.linky.parser.type;

import net.psammead.linky.parser.CommandParser;
import net.psammead.linky.parser.ParameterType;


/** a boolean represented as on/off, yes/no or true/false */
public final class ChoiceType implements ParameterType {
	private final String	yes;
	private final String	no;
	
	public ChoiceType(String yes, String no) {
		this.yes	= yes;
		this.no		= no;
	}
	
	public Object coerce(String value) { return yes.equals(value); }
	public String regexp() { return "(" + CommandParser.quoteMeta(yes) + "|" + CommandParser.quoteMeta(no) + ")"; }
}
