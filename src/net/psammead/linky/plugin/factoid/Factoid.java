package net.psammead.linky.plugin.factoid;

import java.util.*;

public final class Factoid {
	public final String	channel;
	public final String	author;
	public final Date	added;
	public final String	topic;
	public final String	text;
	public final int	type;

	/** all value constructor */
	public Factoid(
		String	channel,
		String	author,
		Date	added,
		String	topic,
		String	text,
		int	type
	) {
		this.channel	= channel;
		this.author	= author;
		this.added	= added;
		this.topic	= topic;
		this.text	= text;
		this.type	= type;
	}
	
	/** compares with another object */
	@Override
	public boolean equals(Object o) {
		if (o == this)	return true;
		if (o == null)	return false;
		if (o.getClass() != Factoid.class)	return false;
		final Factoid oo = (Factoid)o;
		return	(channel	== null ? oo.channel == null	: channel.equals(oo.channel))
			&&	(author	== null ? oo.author == null	: author.equals(oo.author))
			&&	(added	== null ? oo.added == null	: added.equals(oo.added))
			&&	(topic	== null ? oo.topic == null	: topic.equals(oo.topic))
			&&	(text	== null ? oo.text == null	: text.equals(oo.text))
			&&	type	== oo.type;
	}

	/** hashCode for Maps and Sets */
	@Override
	public int hashCode() {
		int out = 0; out += channel	== null ? 0 : channel.hashCode();
		out *= 7919; out += author	== null ? 0 : author.hashCode();
		out *= 7919; out += added	== null ? 0 : added.hashCode();
		out *= 7919; out += topic	== null ? 0 : topic.hashCode();
		out *= 7919; out += text	== null ? 0 : text.hashCode();
		out *= 7919; out += type;
		return out;
	}
	
	/** for debugging purposes only */
	@Override
	public String toString() {
		final StringBuffer out = new StringBuffer("Factoid{ ");
		out.append("channel=").append(channel).append(", ");
		out.append("author=").append(author).append(", ");
		out.append("added=").append(added).append(", ");
		out.append("topic=").append(topic).append(", ");
		out.append("text=").append(text).append(", ");
		out.append("type=").append(type).append(" }");
		return out.toString();
	}
}
