package net.psammead.linky;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.psammead.linky.irc.Connection;
import net.psammead.linky.irc.ConnectionHandlerAdapter;
import net.psammead.linky.irc.ConnectionHandlerCaster;
import net.psammead.linky.irc.Routing;
import net.psammead.linky.persistence.Persistence;
import net.psammead.linky.security.Role;
import net.psammead.linky.security.SecuredUtil;
import net.psammead.linky.settings.PersonalitySettings;
import net.psammead.linky.settings.PluginSettings;
import net.psammead.linky.util.NamedLogger;
import net.psammead.util.Disposable;
import net.psammead.util.reflect.ReflectException;
import net.psammead.util.reflect.ReflectUtil;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

/** a single linky instance configured from its own set of properties */
public final class Personality implements Disposable {
	private static final int	RECONNECT_DELAY	= 15;	// seconds
	
	private final PersonalitySettings		settings;
	
	private final NamedLogger	logger;
	private final Connection	connection;
	private final Persistence	persistence;
	
	private final ConnectionHandlerCaster		caster;
	private final LinkedList<PluginInstance>	plugins;
	
	public Personality(String name, PersonalitySettings settings) throws IOException, NickAlreadyInUseException, IrcException {
		this.settings = settings;
		
		logger	= new NamedLogger(name);
	
		logger.message("loading");
		
		// persistence engine
		persistence	= new Persistence(logger, settings.persistenceSettings);
		
		// setup event infrastructure
		caster	= new ConnectionHandlerCaster();
		caster.addConnectionHandler(new ConnectionHandlerImpl());
		
		// load plugins
		plugins			= new LinkedList<PluginInstance>();
		for (String pluginName : settings.pluginNames) {
			loadPlugin(pluginName);
		}
		
		// load persistent models
		persistence.start();
		
		// start the bot
		connection	= new Connection(logger, settings.ircSettings, caster);
		connection.init();
		
		logger.message("loaded");
	}
	
	/** kills this Personality */
	public void dispose() {
		logger.message("unloading");
		
		connection.exit();
		persistence.stop();
		
		while (!plugins.isEmpty()) {
			PluginInstance	plugin	= plugins.get(0);
			unloadPlugin(plugin);
		}
		
		logger.message("unloaded");
	}
	
	/** make the ConnectionHandlerImpl re-connect */ 
	public void disconnect() {
		// TODO should not wait 15 seconds!
		logger.message("disconnecting");
		connection.disconnect();
	}
	
	//------------------------------------------------------------------------------
	//## plugin manager
	
	/** load a plugin */
	private synchronized void loadPlugin(String pluginName) throws IOException {
		logger.message("loading plugin: " + pluginName);
		
		PluginSettings	pluginSettings	= settings.pluginSettings(pluginName);
		
		Plugin plugin;
		try { plugin = (Plugin)ReflectUtil.object(pluginSettings.className, new Object[] {}); }
		catch (ReflectException e) { throw new RuntimeException(e); }
		
		PluginInstance	pluginInstance	= new PluginInstance(this, logger, persistence, pluginSettings, plugin);
		loadPlugin(pluginInstance);
	}
	
	/** load a plugin */
	private synchronized void loadPlugin(PluginInstance plugin) {
		plugins.add(plugin);
		plugin.afterLoad();
		caster.addConnectionHandler(plugin.getConnectionHandler());
	}
	
	/** unload a plugin */
	private synchronized void unloadPlugin(PluginInstance plugin) {
		caster.removeConnectionHandler(plugin.getConnectionHandler());
		plugin.beforeUnload();
		plugins.remove(plugin);
	}
	
	/** gets all plugins */
	public synchronized List<PluginInstance> getLoadedPlugins() {
		return Collections.unmodifiableList(plugins);
	}
	//------------------------------------------------------------------------------
	//## event manager
	
	/** takes events from the Bot and dispatches them to Plugins */
	private class ConnectionHandlerImpl extends ConnectionHandlerAdapter {
		@Override
		public void onRawLine(Connection connection, String line) {
			logger.message(line);
		}
		
		@Override
		public void onError(Connection connection, Throwable t) {
			logger.error(t);
		}

		@Override
		public void onPrivMsg(Routing routing, String message) {
			dispatchCommand(routing, message);
		}
		
		@Override
		public void onDisconnect(Connection connection) {
			// TODO doesn't belong here at all
			if (!connection.isAlive()) {
				logger.message("disconnected: done");
				return;
			}
			
			// auto-reconnect
			logger.message("reconnecting");
			while (!connection.isConnected()) {
				try {
					connection.connect();
				}
				catch (Exception e) {
					logger.error(e);
					logger.message("failed. retrying in " + RECONNECT_DELAY + " seconds");
					try {
						Thread.sleep(RECONNECT_DELAY*1000L);
					}
					catch (InterruptedException e1) {
						logger.error(e1);
					}
				}
			}
			logger.message("reconnected");
		}
	}
	
	//------------------------------------------------------------------------------
	//## dispatcher implementation
	
	/** dispatches a command or returns false when nothing was found */
	private void dispatchCommand(Routing routing, String message) {
		// commands must be directly addressed to us
		String addressed	= routing.directlyAddressed(message);
		if (addressed == null)	return;
		
		Set<Role>	roles = userRoles(routing);
		
		// find candidate CommandExecution objects
		List<CommandExecution>	candidates		= new ArrayList<CommandExecution>();
		List<PluginInstance>	allowedPlugins	= SecuredUtil.allowed(plugins, roles);
		for (PluginInstance plugin : allowedPlugins) {
			// TODO liefert kein einziges komando für Owner zurück. warum?
			List<CommandInstance>	allowedCommands	= SecuredUtil.allowed(plugin.getCommands(), roles);
			for (CommandInstance command : allowedCommands) {
				CommandExecution	execution	= command.parse(addressed);
				if (execution == null)	continue;
				candidates.add(execution);
			}
		}
		
		// if not even the Fallback plugin was found we just exit
		if (candidates.isEmpty())	return;
		
		// find the best weight
		int	maxWeight	= Integer.MIN_VALUE;
		for (CommandExecution execution : candidates) {
			int	weight	= execution.weight();
			if (weight > maxWeight)	maxWeight = weight;
		}
		
		// execute the first command with maxWeight
		for (CommandExecution execution : candidates) {
//			System.err.println("### trying command: " + execution);
			int	weight	= execution.weight();
			if (weight < maxWeight)	continue;
			execution.execute(routing);
			break;
		}
	}
	
	//------------------------------------------------------------------------------
	//## security stuff
	
	/** fetch all roles of the source */
	public Set<Role> userRoles(Routing routing) {
		// find out roles of the user
		Set<Role>	roles	= new HashSet<Role>();
		for (PluginInstance plugin : plugins) {
			roles.addAll(plugin.provideRoles(routing));
		}
		return roles;
	}
	
	//------------------------------------------------------------------------------
	//## help
	
	public PersonalityHelp help(Set<Role> roles) {
		List<PluginInstance>	instances	= SecuredUtil.visible(plugins, roles);
		List<PluginHelp>		helps		= new ArrayList<PluginHelp>();
		for (PluginInstance instance : instances) {
			PluginHelp help = instance.help(roles);
			if (help.empty())	continue;
			helps.add(help);
		}
		return new PersonalityHelp("###name", helps);
	}
	
	public PersonalityStatus status(Set<Role> roles) {
		List<PluginInstance>	instances	= SecuredUtil.visible(plugins, roles); 
		List<String> lines	= new ArrayList<String>();
		for (PluginInstance instance : instances) {
			String	status	= instance.status();
			if (status == null)	continue;
			lines.add(status);
		}
		return new PersonalityStatus(lines);
	}
}
