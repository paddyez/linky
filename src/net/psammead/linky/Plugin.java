package net.psammead.linky;

import java.util.Set;

import net.psammead.linky.irc.ConnectionHandler;
import net.psammead.linky.irc.Routing;
import net.psammead.linky.security.Role;

/** to be implemented by the plugin writer */
public interface Plugin {
	/** called after the constructor and before init */
	void setPluginContext(PluginContext pluginContext);
	
	/** initialization after the PluginContext has been set */
	void init();
	
	/** is called before the Plugin receives any messages */
	void afterLoad();

	/** is called before the Plugin is removed and after any messages */
	void beforeUnload();
	
	/** called to determine roles of a user before executing any Commands */
	Set<Role> provideRoles(Routing routing);
	
	/** returns all Command implementations this Plugin provides */
	Command[] commands();
	
	/** retrne a ConnectionHandler for this Plugin */
	ConnectionHandler handler();
	
	/** status information about this Plugin */
	String status();
}

