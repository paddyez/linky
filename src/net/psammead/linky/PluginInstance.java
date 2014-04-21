package net.psammead.linky;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import net.psammead.linky.config.Config;
import net.psammead.linky.irc.ConnectionHandler;
import net.psammead.linky.irc.Routing;
import net.psammead.linky.persistence.Persistence;
import net.psammead.linky.persistence.Persistent;
import net.psammead.linky.security.Role;
import net.psammead.linky.security.Secured;
import net.psammead.linky.security.SecuredUtil;
import net.psammead.linky.settings.CommandSettings;
import net.psammead.linky.settings.PluginSettings;
import net.psammead.linky.util.NamedLogger;

/**  a plugin instance */
public final class PluginInstance implements Secured {
	private final Personality			personality;
	
	private final NamedLogger			logger;
	private final Persistence			persistence;
	
	private final PluginSettings		settings;
	
	private final ConnectionHandler		handler;
	private final List<CommandInstance>	commands;
	
	private final Plugin				plugin;

	public PluginInstance(
			Personality personality, 
			NamedLogger logger, 
			Persistence persistence, 
			PluginSettings settings,
			Plugin plugin) {
		this.personality	= personality;
		this.logger			= logger;
		this.persistence	= persistence;
		this.settings		= settings;
		this.plugin			= plugin;
		
		// TODO think about order of calls
		
		plugin.setPluginContext(new PluginContextImpl());
		plugin.init();
		
		commands	= commandInstances(plugin.commands());
		handler		= plugin.handler();
	}
	
	/** create CommandInstances for the plugin's Commands */
	private List<CommandInstance> commandInstances(Command[] commands) {
		List<CommandInstance> out		= new ArrayList<CommandInstance>();
		for (Command command : commands) {
			CommandSettings commandSettings = settings.commandSettings(command.name());
			CommandInstance commandInstance = new CommandInstance(command, commandSettings);
			out.add(commandInstance);
		}
		return out;
	}

	/** roles allowed to use this object */
	public Set<Role> allowedFor() {
		return settings.allowedFor;
	}
	
	/** roles allowed to see this object */
	public Set<Role> visibleFor() {
		return settings.visibleFor;
	}
	
	/** returns the Description from the localized */
	public final String getDescription() { 
		return settings.localized.getString("description"); 
	} 
	
	/** returns the commands this Plugin understands */
	public List<CommandInstance> getCommands() {
		return Collections.unmodifiableList(commands);
	}
	
	/** returns the ConnectionHandler this Plugin provided */
	public ConnectionHandler getConnectionHandler() {
		return handler;
	}
	
	/** called to determine roles of a user before executing any commands */
	public Set<Role> provideRoles(Routing routing) {
		return plugin.provideRoles(routing);
	}
	
	public void afterLoad() {
		plugin.afterLoad();
	}
	
	public void beforeUnload() {
		plugin.beforeUnload();
	}
	
	public String status() {
		return plugin.status();
	}
	
	/** to be used by the plugin for callbacks */
	private class PluginContextImpl implements PluginContext {
		public void jump() {
			// reconnects automatically
			personality.disconnect();
		}
		public void die() {
			personality.dispose();
		}

		public Set<Role> userRoles(Routing routing) {
			return personality.userRoles(routing);
		}
		public PersonalityHelp help(Set<Role> roles) {
			return personality.help(roles);
		}
		public PersonalityStatus status(Set<Role> roles) {
			return personality.status(roles);
		}
		
		public Config config() {
			return settings.config;
		}
		public String message(String key, Object... args) {
			return settings.messages.get(key, args);
		}
//		public String localized(String key) {
//			return settings.localized.getString(key);
//		}

		public void registerPersistent(Persistent persistent) {
			persistence.register(persistent);
		}
		public void unregisterPersistent(Persistent persistent) {
			persistence.unregister(persistent);
		}
		
		public void log(String line) {
			logger.message(line);
		}
		public void error(Throwable t) {
			logger.error(t);
		}
	}
	
	//------------------------------------------------------------------------------
	//## help
	
	public PluginHelp help(Set<Role> roles) {
		List<CommandInstance>	instances	= SecuredUtil.visible(commands, roles);
		List<CommandHelp>		helps		= new ArrayList<CommandHelp>();
		for (CommandInstance instance : instances) {
			CommandHelp help = instance.help(roles);
			helps.add(help);
		}
		return new PluginHelp("###name", getDescription(), helps);
	}
}
