package net.psammead.linky;

import java.util.Collections;
import java.util.List;


public final class PluginHelp {
	public final String	name;
	public final String	description;
	
	public final List<CommandHelp>	commands;
	
	public PluginHelp(String name, String description, List<CommandHelp> commands) {
		this.name			= name;
		this.description	= description;
		this.commands		= Collections.unmodifiableList(commands);
	}

	public boolean empty() {
		return commands.isEmpty();
	}
}
