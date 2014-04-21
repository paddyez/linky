package net.psammead.linky.settings;

import java.util.ArrayList;
import java.util.List;

import net.psammead.linky.config.Config;

public final class IrcSettings {
	private static final int				MAX_SERVER_SETTINGS	= 100;
	private static final IrcServerSetting[]	NO_SERVER_SETTINGS	= new IrcServerSetting[0];
	
	public final String		nick;
	public final String		login;
	public final String		version;
	public final String		finger;
	public final String		encoding;
	public final long		delay;
	
	public final IrcServerSetting[]	servers;
	
	public IrcSettings(Config config) {
		servers		= parseServers(config);
		encoding	= config.getString("encoding");
		nick		= config.getString("nick");
		login		= config.getString("login");
		version		= config.getString("version");
		finger		= config.getString("finger");
		delay		= config.getLong("delay");	
	}

	private IrcServerSetting[] parseServers(Config config) {
		final List<IrcServerSetting>	serverList	= new ArrayList<IrcServerSetting>();
		for (int i=0; i<MAX_SERVER_SETTINGS; i++) {
			Config	branch	= config.branch("server." + i + ".");
			if (!IrcServerSetting.exists(branch))	continue;
			serverList.add(new IrcServerSetting(branch));
		}
		if (serverList.isEmpty())	throw new RuntimeException("no irc server configured, at least one is necessary.");
		return serverList.toArray(NO_SERVER_SETTINGS);
	}
}
