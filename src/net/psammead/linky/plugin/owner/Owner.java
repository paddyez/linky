 // VOGEN: owner.model.Owner% nick:String, password:String, wantsNotice:boolean, loggedIn:boolean, lastAction:Date = new Date()

package net.psammead.linky.plugin.owner;

import java.util.*;

/** generated class */
public class Owner {
	//------------------------------------------------------------------------------
	//## fields

	private String	nick;
	private String	password;
	private boolean	wantsNotice;
	private boolean	loggedIn;
	private Date	lastAction;

	//------------------------------------------------------------------------------
	//## constructors
	
	/** default constructor */
	public Owner() {
		nick	= null;
		password	= null;
		wantsNotice	= false;
		loggedIn	= false;
		lastAction	= new Date();
	}

	/** all value constructor */
	public Owner(
		String	nick,
		String	password,
		boolean	wantsNotice,
		boolean	loggedIn,
		Date	lastAction
	) {
		this.nick	= nick;
		this.password	= password;
		this.wantsNotice	= wantsNotice;
		this.loggedIn	= loggedIn;
		this.lastAction	= lastAction;
	}

	//------------------------------------------------------------------------------
	//## getter methods

	public String	getNick()	{ return nick; }
	public String	getPassword()	{ return password; }
	public boolean	isWantsNotice()	{ return wantsNotice; }
	public boolean	isLoggedIn()	{ return loggedIn; }
	public Date	getLastAction()	{ return lastAction; }

	//------------------------------------------------------------------------------
	//## setter methods

	public void setNick(String nick)	{ this.nick = nick; }
	public void setPassword(String password)	{ this.password = password; }
	public void setWantsNotice(boolean wantsNotice)	{ this.wantsNotice = wantsNotice; }
	public void setLoggedIn(boolean loggedIn)	{ this.loggedIn = loggedIn; }
	public void setLastAction(Date lastAction)	{ this.lastAction = lastAction; }

	//------------------------------------------------------------------------------
	//## default methods
	
	/** compares with another object */
	@Override
	public boolean equals(Object o) {
		if (o == this)	return true;
		if (o == null)	return false;
		if (o.getClass() != Owner.class)	return false;
		Owner oo = (Owner)o;
		return	(nick	== null ? oo.nick == null	: nick.equals(oo.nick))
			&&	(password	== null ? oo.password == null	: password.equals(oo.password))
			&&	wantsNotice	== oo.wantsNotice
			&&	loggedIn	== oo.loggedIn
			&&	(lastAction	== null ? oo.lastAction == null	: lastAction.equals(oo.lastAction));
	}

	/** hashCode for Maps and Sets */
	@Override
	public int hashCode() {
		int out = 0; out += nick	== null ? 0 : nick.hashCode();
		out *= 7919; out += password	== null ? 0 : password.hashCode();
		out *= 7919; out += wantsNotice ? 1 : 0;
		out *= 7919; out += loggedIn ? 1 : 0;
		out *= 7919; out += lastAction	== null ? 0 : lastAction.hashCode();
		return out;
	}
	
	/** for debugging purposes only */
	@Override
	public String toString() {
		StringBuffer out = new StringBuffer("Owner{ ");
		out.append("nick=").append(nick).append(", ");
		out.append("password=").append(password).append(", ");
		out.append("wantsNotice=").append(wantsNotice).append(", ");
		out.append("loggedIn=").append(loggedIn).append(", ");
		out.append("lastAction=").append(lastAction).append(" }");
		return out.toString();
	}
}
