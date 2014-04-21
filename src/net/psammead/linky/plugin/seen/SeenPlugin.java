package net.psammead.linky.plugin.seen;

import net.psammead.linky.Command;
import net.psammead.linky.CommandBase;
import net.psammead.linky.PluginBase;
import net.psammead.linky.irc.ConnectionHandler;
import net.psammead.linky.irc.ConnectionHandlerAdapter;
import net.psammead.linky.irc.Routing;

public final class SeenPlugin extends PluginBase {
	SeenModel	seenModel	= new SeenModel();
	
	public SeenPlugin() {}
	
	@Override
	public void init() {
		seenModel	= new SeenModel();
	}

	//------------------------------------------------------------------------------
	//## life cycle
	
	/** is called before the plugin receives any messages - may be overwritten by the plugin */
	@Override
	public void afterLoad() {
		context.registerPersistent(seenModel);
	}
	
	/** is called before the plugin is removed and after any messages - may be overwritten by the plugin */
	@Override
	public void beforeUnload() {
		context.unregisterPersistent(seenModel);
	}
	
	//------------------------------------------------------------------------------
	//## status output
	
	/** called by the HelpPlugin - output information in the status command */
	@Override
	public String status() {
		return context.message("status", 
				seenModel.getSeenCount());
	}
	
	//------------------------------------------------------------------------------
	//## irc event handler
	
	@Override
	public ConnectionHandler handler() {
		return new ConnectionHandlerAdapter() {
			/** This method is called whenever a message is sent to a channel. */
			@Override
			public void onPrivMsg(Routing routing, String message) {
				if (!routing.target.channelFlag)	return;
				seenModel.remember(routing.target.identifier, routing.source.nick);
			}
			
			/** This method is called whenever an ACTION is sent from a user. */
			@Override
			public void onAction(Routing routing, String action) {
				if (!routing.target.channelFlag)	return;
				seenModel.remember(routing.target.identifier, routing.source.nick);
			}
		};
	}
	
	//------------------------------------------------------------------------------
	//## command handler
	
	@Override
	public Command[] commands() { 
		return new Command[] {
			new CommandBase("seen") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdSeen(routing, (String)args[0]);
				}
			}
		};
	}
	
	/** when somebody has said something the last time */
	private void cmdSeen(Routing routing, String nick) {
		Seen	seen	= seenModel.query(nick);
		if (seen != null)	routing.reply(context.message("haveSeen", nick, seen.getLastPost(), seen.getChannel()));
		else				routing.reply(context.message("notSeen", nick));
	}
}
