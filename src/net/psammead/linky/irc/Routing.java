package net.psammead.linky.irc;

import net.psammead.util.ToString;

/** where an Event comes from and where it goes to */
public final class Routing {
	public final Connection	connection;
	public final Source		source;
	public final Target		target;

	public Routing(Connection connection, Source source, Target target) {
		this.connection	= connection;
		this.source		= source;
		this.target		= target;
	}
	
	//------------------------------------------------------------------------------
	//## output
	
	/** reply to a message without any prefix */
	public void reply(String message) {
		String replyTarget	= target.channelFlag ? target.identifier  : source.nick;
		connection.sendMessage(replyTarget, message);
	}
	
	/** reply to a message with a suitable prefix */
	public void replyPrefixed(String message) {
		String replyPrefix	= target.channelFlag ? source.nick + ": " : "";
		String replyTarget	= target.channelFlag ? target.identifier  : source.nick;
		connection.sendMessage(replyTarget, replyPrefix + message);
	}
	
	/** reply to the sender without prefix, but in multiple lines */
	public void replyMulti(String message) {
		String replyTarget	= target.channelFlag ? target.identifier : source.nick;
		message	= message.replaceAll("\n+$", "");	
		for (String line : message.split("\n")) {
			connection.sendMessage(replyTarget, line);
		}
	}
	
	/** reply to the sender personally without prefix, but in multiple lines */
	public void replyPrivate(String message) {
		message	= message.replaceAll("\n+$", "");	
		for (String line : message.split("\n")) {
			connection.sendMessage(source.nick, line);
		}
	}
	
	/** reply with an action */
	public void replyAction(String action) {
		connection.sendAction(target.identifier, action);
	}

	/** send a Message to an arbitrary channel or user */
	public void sendMessage(String targetIdentifier, String text) {
		connection.sendMessage(targetIdentifier, text);
	}
	
	//------------------------------------------------------------------------------
	//## input
	
	/** returns true if the routing designates a private Message to us */
	public boolean isPrivate() {
		String	connectionNick	= connection.getNick();
		
		return target.nickFlag
			&& target.identifier.equals(connectionNick);
	}

	/** returns the text after a directly addressed Message or null */
	public String directlyAddressed(String message) {
		String	connectionNick	= connection.getNick();
		
		// the bot cannot address itself directly
		if (source.nick.equals(connectionNick))	return null;	
		
		// test for suitable prefixes and remove it when found
		String[]	prefixes	= new String[] { 
			connectionNick + ": ", 
			connectionNick + ", ", 
			"!" 
		};
		boolean		hasPrefix	= false;
		for (String prefix : prefixes) {
			if (message.startsWith(prefix)) {
				hasPrefix	= true;
				message	= message.substring(prefix.length());
				break;
			}
		} 
		
		// we must have found a prefix or be spoken to in private
		if (!hasPrefix && !isPrivate())	return null;
		
		// trim the message and check whether it still contains anything
		message	= message.trim();
		if (message.length() == 0)	return null;
		
		return message;
	}
	
	//------------------------------------------------------------------------------

	/** for debugging only */
	@Override
	public String toString() {
		return new ToString(this)
				.append("bot",		connection)
				.append("source",	source)
				.append("target",	target)
				.toString();
	}
}
