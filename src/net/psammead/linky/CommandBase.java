package net.psammead.linky;

import net.psammead.linky.irc.Routing;
import net.psammead.util.ToString;

public abstract class CommandBase implements Command {
	private final String name;

	public CommandBase(String name) {
		this.name	= name;
	}

	public final String name() {
		return name;
	}
	
	public abstract void execute(Routing routing, Object[] args);
	
	@Override
	public String toString() { return new ToString(this).toString(); }
}
