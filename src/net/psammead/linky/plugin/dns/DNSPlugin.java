package net.psammead.linky.plugin.dns;

import java.net.InetAddress;
import java.net.UnknownHostException;

import net.psammead.linky.Command;
import net.psammead.linky.CommandBase;
import net.psammead.linky.PluginBase;
import net.psammead.linky.irc.Routing;

public final class DNSPlugin extends PluginBase {
	public DNSPlugin() {}
	
	//------------------------------------------------------------------------------
	//## command handler
	
	@Override
	public Command[] commands() { 
		return new Command[] {
			new CommandBase("dns") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdDns(routing, (String)args[0]);
				}
			}
		};
	}
	
	/** resolve an ip or hostname into each other */
	private void cmdDns(Routing routing, String what) {
		InetAddress	address;
		try {
			address	= InetAddress.getByName(what);
		}
		catch (UnknownHostException e) {
			routing.reply(context.message("unknownHost", what));
			return;
		}
		
			 if (!what.equals(address.getHostAddress()))		routing.reply(context.message("knownHost", what, address.getHostAddress()));
		else if (!what.equals(address.getCanonicalHostName()))	routing.reply(context.message("knownHost", what, address.getCanonicalHostName()));
		else													routing.reply(context.message("unknownHost", what));
	}
}
