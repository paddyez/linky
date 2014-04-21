package net.psammead.linky;

import java.util.List;
import java.util.Set;

import net.psammead.linky.parser.CommandParser;
import net.psammead.linky.security.Role;
import net.psammead.linky.security.Secured;
import net.psammead.linky.settings.CommandSettings;

/** a single Command that a Plugin understands */
public final class CommandInstance  implements Secured {
	public static final int	DEFAULT_WEIGHT	= 0;
	
	private final Command			command;
	private final CommandSettings	settings;
	private final CommandParser		parser;
	
	public CommandInstance(Command command, CommandSettings settings) {
		this.command	= command;
		this.settings	= settings;
		this.parser		= new CommandParser(settings.syntax);
	}

	public String shortSyntax() {
		return parser.shortSyntax;
	}
	
	public String description() {
		return settings.description;
	}
	
	/** roles allowed to use this object */
	public Set<Role> allowedFor() {
		return settings.allowedFor;
	}
	
	/** roles allowed to see this object */
	public Set<Role>  visibleFor() {
		return settings.visibleFor;
	}
	
	public CommandExecution parse(String message) {
		List<Object> args = parser.parse(message);
		if (args == null)	return  null;
		return new CommandExecution(command, args, settings.weight);
	}
	
	//------------------------------------------------------------------------------
	//## help
	
	public CommandHelp help(@SuppressWarnings("unused") Set<Role> roles) {
		return new CommandHelp(shortSyntax(), description());
	}
}
