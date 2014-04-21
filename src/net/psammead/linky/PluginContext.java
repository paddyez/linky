package net.psammead.linky;

import java.util.Set;

import net.psammead.linky.config.Config;
import net.psammead.linky.irc.Routing;
import net.psammead.linky.persistence.Persistent;
import net.psammead.linky.security.Role;

/** everything a Plugin knows about the world */
public interface PluginContext {
	//### this should only be allowed for owners
	void jump();
	void die();
	
	Set<Role> userRoles(Routing routing);
	PersonalityHelp help(Set<Role> roles);
	PersonalityStatus status(Set<Role> roles);
	
	Config config();
	String message(String key, Object... args);
	
	//### better just let the plugin provide its persistent data on request? then afterLoad and beforeUnload could go away
	void registerPersistent(Persistent persistent);
	void unregisterPersistent(Persistent persistent);
	
	/** print a log line via the logger */
	void log(String line);
	
	/** print a log line via the logger */
	void error(Throwable t);
}
