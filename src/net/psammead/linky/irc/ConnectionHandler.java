package net.psammead.linky.irc;

import net.psammead.linky.irc.Connection;
import net.psammead.linky.irc.Routing;

public interface ConnectionHandler {
	/** This method is called on every line we get from the IRC-server */
	void onRawLine(Connection connection, String line);
	/** This method is called if ány handler method threw an Exception */
	void onError(Connection connection, Throwable t);
	
	/** This method is called once the PircBot has successfully connected to the IRC server. */
	void onConnect(Connection connection);
	/** This method carries out the actions to be performed when the PircBot gets disconnected. */
	void onDisconnect(Connection connection);
	
	// ### Connection instead of Routing?
	/** This method is called whenever someone (possibly us) joins a channel which we are on. */
	void onJoin(Routing routing, String channel);
	/** This method is called whenever someone (possibly us) parts a channel which we are on. */
	void onPart(Routing routing, String channel);
	
	/** This method is called whenever a message is sent to a channel. */
	void onPrivMsg(Routing routing, String message);
	/** This method is called whenever we receive a notice. */
	void onNotice(Routing routing, String notice);
	/** This method is called whenever an ACTION is sent from a user. */
	void onAction(Routing routing, String action);
	
	/** This method is called whenever someone (possibly us) changes nick on any of the channels that we are on. */
	void onNickChange(Routing routing, String newNick);
	/** This method is called whenever someone (possibly us) is kicked from any of the channels that we are in. */
	void onKick(Routing routing, String recipientNick, String reason);
	 
	//------------------------------------------------------------------------------
	
//	/** This method is called once the PircBot has successfully connected to the IRC server. */
//	void onConnect()
//	/** This method carries out the actions to be performed when the PircBot gets disconnected. */
//	void onDisconnect()
//          
//	/** This method is called whenever someone (possibly us) quits from the server. */
//	void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason)
//	
//	/** Called when a user (possibly us) gets granted operator status for a channel. */
//	void onOp(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient)
//	/** Called when a user (possibly us) gets operator status taken away. */
//	void onDeop(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient)
//	
//	/** This method is called whenever a message is sent to a channel. */
//	void onMessage(String channel, String sender, String login, String hostname, String message)
//	/** This method is called whenever a private message is sent to the PircBot. */
//	void onPrivateMessage(String sender, String login, String hostname, String message)
//	
//	/** Called when a user (possibly us) gets voice status granted in a channel. */  
//	void onVoice(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient)
//	/**  Called when a user (possibly us) gets voice status removed. */     
//	void onDeVoice(String channel, String sourceNick, String sourceLogin, String sourceHostname, String recipient)
//	
//	/** Called when we are invited to a channel by a user. */
//	void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel)
//	/** Called when the mode of a channel is set.  */
//	void onMode(String channel, String sourceNick, String sourceLogin, String sourceHostname, String mode)
//	/** This method is called whenever a user sets the topic, or when PircBot joins a new channel and discovers its topic. */
//	void onTopic(String channel, String topic, String setBy, long date, boolean changed)
//	
//	/** This method is called whenever we receive a FINGER request. */
//	void onFinger(String sourceNick, String sourceLogin, String sourceHostname, String target) 
//	/** This method is called whenever we receive a VERSION request. */
//	void onVersion(String sourceNick, String sourceLogin, String sourceHostname, String target)
//	/** This method is called whenever we receive a PING request from another user. */
//	void onPing(String sourceNick, String sourceLogin, String sourceHostname, String target, String pingValue)
}
