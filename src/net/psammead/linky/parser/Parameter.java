package net.psammead.linky.parser;


public final class Parameter {
	public final String			name;
	public final ParameterType	type;

	/** a single Parameter parsed from the syntax */
	public Parameter(String name, ParameterType type) {
		this.name = name;
		this.type = type;
	}
}
