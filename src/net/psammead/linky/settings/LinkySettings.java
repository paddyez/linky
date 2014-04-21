package net.psammead.linky.settings;

import java.io.IOException;

import net.psammead.linky.config.Config;
import net.psammead.linky.util.Data;

public final class LinkySettings {
	public final String[]	personalityNames;
	
	private final Config	config;
	
	public LinkySettings() throws IOException {
		config	= new Config()
					.propertiesFile(Data.appFile("data/linky.properties"))
					.expandOver(new Config().systemProperties());
		
		personalityNames	= config.getStringArray("personalities");
	}

	public PersonalitySettings personalitySettings(String personalityName) throws IOException {
		//Config	personalityConfig	= config.branch(personalityName + ".");
		Config	personalityConfig	= new Config()
										.propertiesFile(Data.appFile("data/personality/" + personalityName + "/personality.properties"))
										.expandOver(new Config().systemProperties());
		return new PersonalitySettings(personalityConfig);
	}
}
