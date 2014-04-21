package net.psammead.linky;

import java.util.Collections;
import java.util.Set;

import net.psammead.linky.irc.ConnectionHandler;
import net.psammead.linky.irc.ConnectionHandlerAdapter;
import net.psammead.linky.irc.Routing;
import net.psammead.linky.security.Role;
import net.psammead.util.ToString;

/** base class for Plugins with default implementations */
public abstract class PluginBase implements Plugin {
	protected PluginContext	context;
	
	/** called after the constructor and before init */
	public void setPluginContext(PluginContext pluginContext) {
		this.context	= pluginContext;
	}
	
	/** initialization after the PluginContext has been set */
	public void init() {}

	public Command[] commands()			{ return new Command[0]; }
	public ConnectionHandler handler()	{ return new ConnectionHandlerAdapter(); }
		
	public void afterLoad() {}
	public void beforeUnload() {}

	public Set<Role> provideRoles(Routing routing) { return Collections.emptySet(); }
	public String status() { return null; }
	
	@Override
	public String toString() { return new ToString(this).toString(); }
}
