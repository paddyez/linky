package net.psammead.linky.plugin.identify;

import net.psammead.linky.PluginBase;
import net.psammead.linky.irc.Connection;
import net.psammead.linky.irc.ConnectionHandler;
import net.psammead.linky.irc.ConnectionHandlerAdapter;

/** registers a personality with freenode's nickserv */
public class IdentifyPlugin extends PluginBase {
	private String	nick;
	private String	register;

	public IdentifyPlugin() {}
	
	@Override
	public void init() {
		nick		= context.config().getString("nick");
		register	= context.config().getString("register");
	}
	
//	//------------------------------------------------------------------------------
//	//## status output
//
//	/** called by the HelpPlugin - output information in the status command */
//	public String status() {
//		List<String>	channels	= channelModel.getChannels();
//		if (channels.size() == 0)	return context.message("noChannel");
//		else						return context.message("inChannels", StringUtil.join(channels, " "));
//	}
	
	//------------------------------------------------------------------------------
	//## irc event handler

	// TODO check for success and print success in status
	
	@Override
	public ConnectionHandler handler() {
		return new ConnectionHandlerAdapter() {
			/** This method is called once the PircBot has successfully connected to the IRC server. */
			@Override
			public void onConnect(Connection connection) {
				if ("".equals(nick) || "".equals("register"))	return;
				context.log("registering at " + nick);
				connection.sendMessage(nick, register);
			}
		};
	}
	
    //------------------------------------------------------------------------------
    //## command handler
	
//	public Command[] commands() { 
//        return new Command[] {
//        	new NamedCommand("ident") {
//        		public void execute(Routing routing, Object[] args) {
//        			cmdIdent(routing, (String)args[0]);
//        		}
//        	}
//        };
//    };
//	
// /** identify the bot at the nickserv */
//  private void cmdIdent(Routing routing, String password) { 
//      routing.sendMessage("nickserv", "identify " + password); 
//  }
}
