package net.psammead.linky.irc;

import java.util.regex.Pattern;

import net.psammead.util.ToString;

/** the target of an Event */
public final class Target {
	private static final int		MAX_NICKNAME_LENGTH		= 32;	// TODO should't the length be restricted to 9 characters?
	private static final Pattern	VALID_NICKNAME_REGEXP	= Pattern.compile("[a-zA-Z][A-Za-z\\[\\\\\\]^_`{|}][a-zA-Z][A-Za-z0-9\\[\\\\\\]^_`{|}-]*");
	
	private static final int		MAX_CHANNELIDENTIFIER_LENGTH	= 200;	
	private static final Pattern	VALID_CHANNELIDENTIFIER_REGEXP	= Pattern.compile("[#&+!][^ ,\\r\\n\\007]+");
	
	public final String		identifier;
	
	/** this target represents a nickname */
	public final boolean	nickFlag;
	
	/** this target represents a whole channel */
	public final boolean	channelFlag;
	
	public Target(String identifier) {
		this.identifier = identifier;
		nickFlag	= identifier.length() <= MAX_NICKNAME_LENGTH 
					&& VALID_NICKNAME_REGEXP.matcher(identifier).matches();
		channelFlag	= identifier.length() <= MAX_CHANNELIDENTIFIER_LENGTH 
					&& VALID_CHANNELIDENTIFIER_REGEXP.matcher(identifier).matches();
	}
	
	//### boolean isHostMask() {}
	
	/** for debugging only */
	@Override
	public String toString() {
		return new ToString(this)
				.append("identifier",	identifier)
				.toString();
	}
}


