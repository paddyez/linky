package net.psammead.linky.irc;


/** do-nothing implementation */
public class ConnectionHandlerAdapter implements ConnectionHandler {
	public void onRawLine(Connection connection, String line) {}
	public void onError(Connection connection, Throwable t) {}
	
	public void onConnect(Connection connection) {}
	public void onDisconnect(Connection connection) {}
	
	public void onJoin(Routing routing, String channel) {}
	public void onPart(Routing routing, String channel) {}
	
	public void onPrivMsg(Routing routing, String message) {}
	public void onNotice(Routing routing, String notice) {}
	public void onAction(Routing routing, String action) {}
	
	public void onNickChange(Routing routing, String newNick) {}
	public void onKick(Routing routing, String recipientNick, String reason) {}
}