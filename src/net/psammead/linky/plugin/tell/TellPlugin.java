package net.psammead.linky.plugin.tell;

import net.psammead.linky.Command;
import net.psammead.linky.CommandBase;
import net.psammead.linky.PluginBase;
import net.psammead.linky.irc.Routing;

public final class TellPlugin extends PluginBase {
	public TellPlugin() {}

	//------------------------------------------------------------------------------
	//## command handler
	
	@Override
	public Command[] commands() { 
		return new Command[] {
			new CommandBase("tell") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdTell(routing, (String)args[0]);
				}
			}
		};
	}
	
	/** resolve an ip or hostname into each other */
	private void cmdTell(Routing routing, String what) {
		// "<" + routing.source.nick + "> " + 
		routing.replyMulti(what);	
	}
}
