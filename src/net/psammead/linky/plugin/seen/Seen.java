 // VOGEN: seen.model.Seen% channel:String, nick:String, lastPost:Date

 package net.psammead.linky.plugin.seen;

import java.util.*;

/** generated class */
public final class Seen {
	//------------------------------------------------------------------------------
	//## fields

	private final String	channel;
	private final String	nick;
	private final Date	lastPost;

	//------------------------------------------------------------------------------
	//## constructors
	
	/** all value constructor */
	public Seen(
		String	channel,
		String	nick,
		Date	lastPost
	) {
		this.channel	= channel;
		this.nick	= nick;
		this.lastPost	= lastPost;
	}

	//------------------------------------------------------------------------------
	//## getter methods

	public String	getChannel()	{ return channel; }
	public String	getNick()	{ return nick; }
	public Date	getLastPost()	{ return lastPost; }


	//------------------------------------------------------------------------------
	//## default methods
	
	/** compares with another object */
	@Override
	public boolean equals(Object o) {
		if (o == this)	return true;
		if (o == null)	return false;
		if (o.getClass() != Seen.class)	return false;
		Seen oo = (Seen)o;
		return	(channel	== null ? oo.channel == null	: channel.equals(oo.channel))
			&&	(nick	== null ? oo.nick == null	: nick.equals(oo.nick))
			&&	(lastPost	== null ? oo.lastPost == null	: lastPost.equals(oo.lastPost));
	}

	/** hashCode for Maps and Sets */
	@Override
	public int hashCode() {
		int out = 0; out += channel	== null ? 0 : channel.hashCode();
		out *= 7919; out += nick	== null ? 0 : nick.hashCode();
		out *= 7919; out += lastPost	== null ? 0 : lastPost.hashCode();
		return out;
	}
	
	/** for debugging purposes only */
	@Override
	public String toString() {
		StringBuffer out = new StringBuffer("Seen{ ");
		out.append("channel=").append(channel).append(", ");
		out.append("nick=").append(nick).append(", ");
		out.append("lastPost=").append(lastPost).append(" }");
		return out.toString();
	}
}
