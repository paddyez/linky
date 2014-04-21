package net.psammead.linky.plugin.fallback;

import net.psammead.linky.Command;
import net.psammead.linky.CommandBase;
import net.psammead.linky.PluginBase;
import net.psammead.linky.irc.Routing;

public final class FallbackPlugin extends PluginBase {
	public FallbackPlugin() {}
	
	//------------------------------------------------------------------------------
	//## command handler
	
	@Override
	public Command[] commands() { 
		return new Command[] {
			new CommandBase("doesNotUnderstand") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdDoesNotUnderstand(routing);
				}
			}
		};
	}
	
	/** tell the user the command could not be understood */
	private void cmdDoesNotUnderstand(Routing routing) {
		routing.reply(context.message("commandNotUnderstood"));
	}
}
