package net.psammead.linky.plugin.owner;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import net.psammead.linky.Command;
import net.psammead.linky.CommandBase;
import net.psammead.linky.PluginBase;
import net.psammead.linky.irc.ConnectionHandler;
import net.psammead.linky.irc.ConnectionHandlerAdapter;
import net.psammead.linky.irc.Routing;
import net.psammead.linky.irc.Source;
import net.psammead.linky.security.Role;

public final class OwnerPlugin extends PluginBase {
	private long		autoLogoutTime;
	private OwnerModel	ownerModel;
	
	public OwnerPlugin() {}
	
	@Override
	public void init() {
		autoLogoutTime	= context.config().getInt("autoLogoutTime") * 60000;	// minutes!
		String initialOwnerName		= context.config().getString("initialOwnerName");
		String initialOwnerPass		= context.config().getString("initialOwnerPass");
		ownerModel	= new OwnerModel(initialOwnerName, initialOwnerPass);
	}
	
	//------------------------------------------------------------------------------
	//## security
	
	/** called to deterine roles of a user before executing any commands */
	@Override
	public Set<Role> provideRoles(Routing routing) {
		boolean		query		= routing.isPrivate();
		Owner		owner		= ownerModel.getOwner(routing.source.nick);
		boolean		loggedin	= false;
		if (owner != null) {
			loggedin	= owner.isLoggedIn();
			owner.setLastAction(new Date());
		}
		
		Set<Role>	out		= new HashSet<Role>();
		if (query)	out.add(new Role("user/query"));
		if (!query)	out.add(new Role("user/channel"));
		if (loggedin && query)	out.add(new Role("owner/query"));
		if (loggedin && !query)	out.add(new Role("owner/channel"));
		
		return out;
	}
	
	//------------------------------------------------------------------------------
	//## life cycle

	/** is called before the plugin receives any messages - may be overwritten by the plugin */
	@Override
	public void afterLoad() {
		context.registerPersistent(ownerModel);
		autoLogout.start();
	}
	
	/** is called before the plugin is removed and after any messages - may be overwritten by the plugin */
	@Override
	public void beforeUnload() {
		autoLogout.stop();
		context.unregisterPersistent(ownerModel);
	}
	
	//------------------------------------------------------------------------------
	//## status output

	/** called by the HelpPlugin - output information in the status command */
	@Override
	public String status() {
		return context.message("status", 
				ownerModel.getOwnerCount());
	}
	
	//------------------------------------------------------------------------------
	//## irc event handler
	
	@Override
	public ConnectionHandler handler() {
		return new ConnectionHandlerAdapter() {
			/** This method is called whenever someone (possibly us) changes nick on any of the channels that we are on. */
			@Override
			public void onNickChange(Routing routing, String newNick) {
				//### wenn der alte nickname mit dem neuen gelinkt ist, könnte ich jetzt ohne probleme die authorisierung übertragen..
				
				// deauthorize the old nickname
				String	oldNick	= routing.source.nick;
				Owner	owner1	= ownerModel.getOwner(oldNick);
				if (owner1 != null)	owner1.setLoggedIn(false);
				
				// deauthorize new nickname
				Owner	owner2	= ownerModel.getOwner(newNick);
				if (owner2 != null)	owner2.setLoggedIn(false);
			} 
			
			/** This method is called whenever we receive a notice. */
			@Override
			public void onNotice(Routing routing, String notice) {
				String	text	= "<" + routing.source.nick + "> " + notice;
				broadcast(routing,text);
			} 
			
			/** This method is called whenever someone (possibly us) is kicked from any of the channels that we are in. */
			@Override
			public void onKick(Routing routing, String recipientNick, String reason) {
				String	botNick	= routing.connection.getNick();
				Source	source	= routing.source;
				String	channel	= routing.target.identifier;
				
				//### dazu kommt er wohl nicht mehr..
				if (recipientNick.equalsIgnoreCase(botNick)) {
					context.log("has been kicked from " + channel +" by " + source.nick + "!" + source.login + "@" + source.host + ": " + reason);
					//broadcast(routing, message("kickedBy", source.nick, source.login, source.host, channel, reason));
					routing.connection.joinChannel(channel);
					routing.reply(context.message("kicked"));
				}
				else {
					Owner	owner	= ownerModel.getOwner(recipientNick);
					if (owner != null)	owner.setLoggedIn(false);
				}
			}
		};
	}
	
	//------------------------------------------------------------------------------
	//## command handler
	
	@Override
	public Command[] commands() { 
		return new Command[] {
			new CommandBase("login") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdLogin(routing, (String)args[0]);
				}
			},
			new CommandBase("logout") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdLogout(routing);
				}
			},
			new CommandBase("passwd") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdPasswd(routing, (String)args[0]);
				}
			},
			new CommandBase("addOwner") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdAddOwner(routing, (String)args[0], (String)args[1]);
				}
			},
			new CommandBase("delOwner") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdDelOwner(routing, (String)args[0]);
				}
			},
			new CommandBase("listOwner") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdListOwner(routing);
				}
			},
			new CommandBase("jump") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdJump(routing);
				}
			},
			new CommandBase("quit") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdQuit(routing);
				}
			}
		};
	}

	/** quit the bot */
	private void cmdJump(Routing routing) { 
		broadcast(routing, context.message("jumping"));
		context.jump();
	}
	
	/** quit the bot */
	private void cmdQuit(Routing routing) { 
		broadcast(routing, context.message("dying"));
		context.die();
	}
	
	/** login an owner */
	private void cmdLogin(Routing routing, String password) {
		Owner	owner	= ownerModel.getOwner(routing.source.nick);
		if (owner == null) {
			context.log("non-owner tried to login: " + routing.source.nick);
			return;
		}
		owner.setLoggedIn(false);
		if (!password.equals(owner.getPassword())) {
			context.log("login failed: " + owner.getNick());
			routing.reply(context.message("loginFailed"));
			return; 
		}
		
		owner.setLoggedIn(true);
		routing.reply(context.message("loginDone"));
		context.log("login successful: " + owner.getNick());
	}
		
	/** logout an owner */
	private void cmdLogout(Routing routing) {
		Owner	owner	= ownerModel.getOwner(routing.source.nick);
		owner.setLoggedIn(false);
		routing.reply(context.message("logoutDone"));
	}
	
	/** changes an owner's password */
	private void cmdPasswd(Routing routing, String password) { 
		Owner	owner	= ownerModel.getOwner(routing.source.nick);
		owner.setPassword(password);
		routing.reply(context.message("passwdDone"));
	}
	
	/** registers a new owner */
	private void cmdAddOwner(Routing routing, String nick, String password) {
		boolean	 success	= ownerModel.addOwner(nick, password);
		if (success)	routing.reply(context.message("addOwnerDone", nick));
		else			routing.reply(context.message("ownerAlreadyExisting", nick));
	}
	
	/** deletes an owner */
	private void cmdDelOwner(Routing routing, String nick) {
		boolean	 success	= ownerModel.deleteOwner(nick);
		if (success)	routing.reply(context.message("delOwnerDone", nick));
		else			routing.reply(context.message("ownerNotExisting", nick));
	}
	
	/** shows a list of owners */
	private void cmdListOwner(Routing routing) {
		Set<String>		nicks	= ownerModel.getOwnerNicks();
		Set<String>		sorted	= new TreeSet<String>(nicks);
		String	text	= context.message("knownOwners");
		for (String nick : sorted) {
			text	+= "\n" + "  " + nick;
		}
		routing.replyMulti(text);
	}
	
	//------------------------------------------------------------------------------
	//## autologout
	
	/** logs out owners when they did not issue any actions for autoLogoutTime milliseconds */
	private class AutoLogout implements Runnable {
		private boolean	stopThread	= false;
		
		public AutoLogout() {
		}
		
		//------------------------------------------------------------------------------
		//## public lifecycle
		
		public void start() {
			new Thread(this).start();
		}
		
		public void stop() {
			stopThread	= true;
		}
		
		//------------------------------------------------------------------------------
		//## private implementation
		
		/** the autoSave Thread */
		public void run() {
			while (!stopThread) {
				try {
					Thread.sleep(60 * 1000);	// every minute
					autoLogout();
				}
				catch (InterruptedException e) {
					break;
				}
			}
		}
	
		/** must be called at regular intervals */
		private void autoLogout() {
			// latest time an owner should have issued a command
			long	latest	= System.currentTimeMillis() - autoLogoutTime;
			for (Owner owner : ownerModel.getAllOwners()) {
				// logout owners that are logged in, but their last action has been too long ago
				if (owner.isLoggedIn() && owner.getLastAction().getTime() < latest) {
					context.log("logged out automatically: " + owner.getNick());
					owner.setLoggedIn(false);
				}
			}
		}
	}
	AutoLogout	autoLogout	= new AutoLogout();
	
	//------------------------------------------------------------------------------
	//## private helper
	
	/** broadcast a Message to all owners listening */
	private void broadcast(Routing routing, String text) {
		for (Owner owner : ownerModel.getAllOwners()) {	
			if (owner.isLoggedIn() && owner.isWantsNotice()) {
				routing.sendMessage(owner.getNick(), text);
			}
		}
	}
}
