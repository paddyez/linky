package net.psammead.linky;

import java.util.List;

import net.psammead.linky.irc.Routing;
import net.psammead.util.ToString;

/** a parsed Command ready for execution */
public final class CommandExecution {
	private final Command		command;
	private final List<Object>	arguments;
	private final int			weight;

	public CommandExecution(Command command, List<Object> arguments, int weight) {
		this.command	= command;
		this.arguments	= arguments;
		this.weight		= weight;
	}
	
	/** the weight the CommandParser returned */
	public int weight() {
		return weight;
	}
	
	/** execute the code of the command */
	public void execute(Routing routing) {
		command.execute(routing, arguments.toArray());
	}
	
	/** for debugging purposes only */
	@Override
	public String toString() {
		return new ToString(this)
				.append("command-name",		command.name())
				.append("arguments",		arguments)
				.append("weight",			weight)
				.toString();
	}
}
