package net.psammead.linky.irc;

import java.util.ArrayList;
import java.util.List;

public final class ConnectionHandlerCaster implements ConnectionHandler {
	private final List<ConnectionHandler>	handlers;
	
	public ConnectionHandlerCaster() {
		handlers	= new ArrayList<ConnectionHandler>();
	}
	
	public void addConnectionHandler(ConnectionHandler handler) {
		handlers.add(handler);
	}

	public void removeConnectionHandler(ConnectionHandler handler) {
		handlers.remove(handler);
	}
	
	//-------------------------------------------------------------------------
	
	public void onRawLine(Connection connection, String line) {
		for (ConnectionHandler handler : handlers)	handler.onRawLine(connection, line);
	}
	
	public void onError(Connection connection, Throwable t) {
		for (ConnectionHandler handler : handlers)	handler.onError(connection, t);
	}
	
	public void onConnect(Connection connection) {
		for (ConnectionHandler handler : handlers)	handler.onConnect(connection);
	}

	public void onDisconnect(Connection connection) {
		for (ConnectionHandler handler : handlers)	handler.onDisconnect(connection);
	}
	
	public void onAction(Routing routing, String action) {
		for (ConnectionHandler handler : handlers)	handler.onAction(routing, action);
	}

	public void onJoin(Routing routing, String channel) {
		for (ConnectionHandler handler : handlers)	handler.onJoin(routing, channel);
	}

	public void onKick(Routing routing, String recipientNick, String reason) {
		for (ConnectionHandler handler : handlers)	handler.onKick(routing, recipientNick, reason);
	}

	public void onNickChange(Routing routing, String newNick) {
		for (ConnectionHandler handler : handlers)	handler.onNickChange(routing, newNick);
	}

	public void onNotice(Routing routing, String notice) {
		for (ConnectionHandler handler : handlers)	handler.onNotice(routing, notice);
	}

	public void onPart(Routing routing, String channel) {
		for (ConnectionHandler handler : handlers)	handler.onPart(routing, channel);
	}

	public void onPrivMsg(Routing routing, String message) {
		for (ConnectionHandler handler : handlers)	handler.onPrivMsg(routing, message);
	}
}
