package net.psammead.linky;

import net.psammead.linky.irc.Routing;

/** to be implemented by Plugin writers */
public interface Command {
	/** this name is used to find the properties of the Command */
	String name();
	
	/** execute the code of the command */
	void execute(Routing routing, Object[] args);
}
