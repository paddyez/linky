package net.psammead.linky.settings;

import java.io.IOException;
import java.util.Locale;
import java.util.TimeZone;

import net.psammead.linky.config.Config;

public final class PersonalitySettings {
	private final Config	config;

	public final Locale		locale;
	public final TimeZone	timeZone;
	public final String[]	pluginNames;
	
	public final PersistenceSettings	persistenceSettings;
	public final IrcSettings			ircSettings;
	
	public PersonalitySettings(Config config) {
		this.config = config;
		// config
		locale		= config.getLocale("locale");
		timeZone	= config.getTimeZone("timeZone");
//		Localized localized	= new Localized(Linky.appFile("data/messages"), "locale", locale, timeZone);
//		messages	= localized.messages("message.");
		
		pluginNames		= config.getStringArray("plugins");
		
		persistenceSettings	= new PersistenceSettings(config.branch("persistence."));
		ircSettings			= new IrcSettings(config.branch("irc."));
	}

	public PluginSettings pluginSettings(String pluginName) throws IOException {
		Config	pluginConfig	= config.branch("plugin." + pluginName + ".");
		return new PluginSettings(pluginConfig, pluginName, locale, timeZone);
	}
}
