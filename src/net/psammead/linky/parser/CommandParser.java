package net.psammead.linky.parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.psammead.linky.parser.type.ChoiceType;
import net.psammead.linky.parser.type.MultiType;
import net.psammead.linky.parser.type.NumberType;
import net.psammead.linky.parser.type.TextType;
import net.psammead.linky.parser.type.WordType;

/** 
 * parses a single Command that a Plugin understands 
 *
 * syntax works like this:
 *
 *		' '				any whitespace
 *		text			literal word
 *		<name~word>		single word
 *		<any~string>	any string
 *		<many~number>	an integral number
 *		<on/off~choice>	a choice between two alternatives
 */ 
public final class CommandParser {
	// TODO get type words from the ParameterType
	// 1 2                3          4    5            6
	// ( ( whitespace ) | ( word ) | ("<" ( name ) "~" ( type ) ">" ) )
	private	final Pattern	SYNTAX_PATTERN	= Pattern.compile("((\\s+)|([^<][^\\s]*)|(<([^<>\\s]+)~(choice|number|word|text|multi)>))");

	/** syntax definition */
	public final String	syntax;
	
	/** syntax without parameter types */
	public final String	shortSyntax;
	
//	/** match-weight, the higher the better */
//	public final int weight;
	
	private	final Pattern			pattern;
	private final List<Parameter> 	parameters;

	public CommandParser(String syntax) {
		this.syntax			= syntax;
		
		parameters	= new ArrayList<Parameter>();
		
		StringBuffer	regexp	= new StringBuffer();
		Matcher			matcher	= SYNTAX_PATTERN.matcher(syntax);
		while (matcher.find()) {
			String	whitespace	= matcher.group(2);
			String	plainWord	= matcher.group(3);
			String	paramName	= matcher.group(5);
			String	paramType	= matcher.group(6);
			if (whitespace != null) {
				regexp.append("\\s+");
				continue;
			}
			if (plainWord != null) {
				regexp.append(quoteMeta(plainWord));
				continue;
			}
			ParameterType	type;
				 if ("number".equals(paramType))	type	= new NumberType();
			else if ("word".equals(paramType))		type	= new WordType();
			else if ("text".equals(paramType))		type	= new TextType();
			else if ("multi".equals(paramType))		type	= new MultiType();
			else if ("choice".equals(paramType)) {
				String[]	choices	= paramName.split("/");
				if (choices.length != 2)	throw new IllegalArgumentException("choice types need two words separated with a '/'"); 
				type	= new ChoiceType(choices[0], choices[1]);	
			}
			else throw new RuntimeException("### unknown ParameterType: " + paramType);
			Parameter	parameter	= new Parameter(paramName, type);
			parameters.add(parameter);
			String	typeRegexp	 = type.regexp();
			regexp.append(typeRegexp);
		}
		// case insensitive?
		//pattern = Pattern.compile(regexp.toString(), Pattern.CASE_INSENSITIVE);
		pattern	= Pattern.compile(regexp.toString());
		
		shortSyntax	= syntax
								.replaceAll("\t", " ")
								.replaceAll("<([^<>\\s~]+)~([^<>\\s~]+)>",	"<$1>");	// $1 name, $2 type
								
//		weight = parameters.size() * 1024 +				// number of parameters counts 1024 times more than
//							syntax						// the syntax without parameters does
//								.replaceAll("\\s+", " ")
//								.replaceAll("<.*?>", "")
//								.length();
	}
	
	/** returns the arguments parsed or null when the message does not match */
	public List<Object> parse(String message) {
		Matcher	matcher	= pattern.matcher(message);
		if (!matcher.matches())	return null;
		
		List<Object>	args	= new LinkedList<Object>();
		for (int i=0; i<matcher.groupCount(); i++) {
			String			value		= matcher.group(i+1);
			Parameter		parameter	= parameters.get(i);
			ParameterType	type		= parameter.type;
			Object			argument	= type.coerce(value);
			args.add(argument);
		}
		return args;
	}

	/** prefix everything except a-z, A-Z, 0-9 and whitespace with a backslash */
	public static String quoteMeta(String word) {
		return word.replaceAll("([^a-zA-Z0-9\\s])", "\\\\$1");	
	}
}
