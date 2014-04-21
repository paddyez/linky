package net.psammead.linky;

import java.io.IOException;

import net.psammead.linky.settings.LinkySettings;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

public final class Linky {
	public static void main(String[] args) throws NickAlreadyInUseException, IOException, IrcException {
		new Linky();
	}
	
	public Linky() throws IOException, NickAlreadyInUseException, IrcException {
		LinkySettings	settings	= new LinkySettings();
		for (String personalityName : settings.personalityNames) {
			new Personality(personalityName, settings.personalitySettings(personalityName));
		}
	}
}
