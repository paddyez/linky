package net.psammead.linky.plugin.proxy;

import net.psammead.linky.Command;
import net.psammead.linky.CommandBase;
import net.psammead.linky.PluginBase;
import net.psammead.linky.irc.Routing;

public final class ProxyPlugin extends PluginBase {
	public ProxyPlugin() {}
	
	//------------------------------------------------------------------------------
	//## command handler
	
	@Override
	public Command[] commands() { 
		return new Command[] {
			new CommandBase("action") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdAction(routing, (String)args[0], (String)args[1]);
				}
			},
			new CommandBase("msg") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdMsg(routing, (String)args[0], (String)args[1]);
				}
			},
			new CommandBase("notice") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdNotice(routing, (String)args[0], (String)args[1]);
				}
			},
			new CommandBase("nick") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdNick(routing, (String)args[0]);
				}
			},
			new CommandBase("topic") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdTopic(routing, (String)args[0], (String)args[1]);
				}
			},
			new CommandBase("join") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdJoin(routing, (String)args[0]);
				}
			},
			new CommandBase("part") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdPart(routing, (String)args[0]);
				}
			},
			new CommandBase("op") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdOp(routing, (String)args[0], (String)args[1]);
				}
			},
			new CommandBase("deop") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdDeop(routing, (String)args[0], (String)args[1]);
				}
			},
			new CommandBase("kick") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdKick(routing, (String)args[0], (String)args[1]);
				}
			}
		};
	}

	/** send an action to a channel or user */
	private void cmdAction(Routing routing, String target, String text) { 
		routing.connection.sendAction(target, text); 
	}
	
	/** send a message to a user or channel */
	private void cmdMsg(Routing routing, String target, String text) { 
		routing.connection.sendMessage(target, text); 
	}
	
	/** send a notice to a user */
	private void cmdNotice(Routing routing, String targetUser, String text) { 
		routing.connection.sendNotice(targetUser, text); 
	}
	
	/** change the bots nickname */
	private void cmdNick(Routing routing, String name) { 
		routing.connection.changeNick(name); 
	}
	
	/** change a channel's topic */
	private void cmdTopic(Routing routing, String channel, String topic) {
		routing.connection.setTopic(channel, topic); 
	}
	
	/** join a channel */
	private void cmdJoin(Routing routing, String channel) {
		routing.connection.joinChannel(channel);
	}
	
	/** part a channel */
	private void cmdPart(Routing routing, String channel) {
		routing.connection.partChannel(channel);
	}
	
	/** op a user in a channel */
	private void cmdOp(Routing routing, String channel, String nick) {
		routing.connection.op(channel, nick);
	}
	
	/** deop a user in a channel */
	private void cmdDeop(Routing routing, String channel, String nick) {
		routing.connection.deOp(channel, nick);
	}
	
	/** kick a user */
	private void cmdKick(Routing routing, String channel, String nick) {
		routing.connection.kick(channel, nick);
	}
}