package net.psammead.linky.settings;

import net.psammead.linky.config.Config;

public final class IrcServerSetting {
	public final String	host;
	public final int	port;
	public final String	password;
	
	public IrcServerSetting(Config config) {
		host		= config.getString("host");
		port		= config.getInt("port");
		password	= config.getString("password");
	}
	
	public static boolean exists(Config config) {
		return config.hasElement("host")
			|| config.hasElement("port")
			|| config.hasElement("password");
	}
}
