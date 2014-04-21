package net.psammead.linky;

import java.util.Collections;
import java.util.List;


public final class PersonalityHelp {
	public final String	name;
	public final List<PluginHelp>	plugins;
	
	public PersonalityHelp(String name, List<PluginHelp> plugins) {
		this.name 		= name;
		this.plugins	= Collections.unmodifiableList(plugins);
	}
	
	public boolean empty() {
		return plugins.isEmpty();
	}
}
