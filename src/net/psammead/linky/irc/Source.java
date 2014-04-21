package net.psammead.linky.irc;

import net.psammead.util.ToString;

/** the Source of an Event */
public final class Source {
	public final String	nick;
	public final String	login;
	public final String	host;

	public Source(String nick, String login, String host) {
		this.nick	= nick;
		this.login	= login;
		this.host	= host;
	}
	
	/** for debugging only */
	@Override
	public String toString() {
		return new ToString(this)
				.append("nick",		nick)
				.append("login",	login)
				.append("host",		host)
				.toString();
	}
}
