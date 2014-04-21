package net.psammead.linky.settings;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import net.psammead.linky.CommandInstance;
import net.psammead.linky.config.Config;
import net.psammead.linky.config.Messages;
import net.psammead.linky.security.Role;
import net.psammead.linky.security.SecuredUtil;
import net.psammead.linky.util.Data;

public final class PluginSettings {
	public final Config	config;
	
	public final Config		localized;
	public final Messages	messages;
	public final String		className;
	public final Set<Role>	visibleFor;
	public final Set<Role>	allowedFor;

	private final Set<Role>	commandsVisibleFor;
	private final Set<Role>	commandsAllowedFor;
	
	public PluginSettings(Config baseConfig, String name, Locale locale, TimeZone timeZone) throws IOException {
		File	baseDir	= Data.appFile("data/plugin");
		File	dir		= new File(baseDir, name);
		
		Config	defaults	= new Config().propertiesFile(new File(dir, "plugin.properties"));
		config	= baseConfig.defaultsTo(defaults);
		
		localized		= new Config().localizedPropertiesFile(dir, "locale", locale);
		messages		= new Messages(localized.branch("message."), locale, timeZone);
		
		className		= config.getString("class");
		
		visibleFor		= SecuredUtil.parseRoles(config.getString("visible"));
		allowedFor		= SecuredUtil.parseRoles(config.getString("allowed"));
		
		// TODO use some defaults mechanism here
		Config	commandsConfig	= config.branch("commands.");
		commandsVisibleFor	= commandsConfig.hasElement("visible")
							? SecuredUtil.parseRoles(commandsConfig.getString("visible"))
							: visibleFor;
		commandsAllowedFor	= commandsConfig.hasElement("allowed")
							? SecuredUtil.parseRoles(commandsConfig.getString("allowed"))
							: allowedFor;
	}
	
	public CommandSettings commandSettings(String name) {
		Config	commandLocalized	= localized.branch("command." + name + ".");
		String	syntax		= commandLocalized.getString("syntax");
		String	description	= commandLocalized.getString("help");
		
		// TODO use some defaults mechanism here
		Config	commandConfig	= config.branch("command." + name + ".");
		Set<Role>	visibleFor	= commandConfig.hasElement("visible")
								? SecuredUtil.parseRoles(commandConfig.getString("visible"))
								: commandsVisibleFor;
		Set<Role>	allowedFor	= commandConfig.hasElement("allowed")
								? SecuredUtil.parseRoles(commandConfig.getString("allowed"))
								: commandsAllowedFor;
		// NOTE: has to be localized
		int			weight		= commandLocalized.hasElement("weight")
								? commandLocalized.getInt("weight")
								: CommandInstance.DEFAULT_WEIGHT;
		
		return new CommandSettings(syntax, description, visibleFor, allowedFor, weight);
	}
}
