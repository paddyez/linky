package net.psammead.linky.irc;

import java.io.IOException;

import net.psammead.linky.settings.IrcServerSetting;
import net.psammead.linky.settings.IrcSettings;
import net.psammead.linky.util.NamedLogger;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;

/** the irc bot */
public final class Connection extends PircBot {
	private NamedLogger			logger;
	private IrcSettings			settings;
	private ConnectionHandler	handler;
	private	boolean				keepRunning;
	private int					serverIndex;

	public Connection(NamedLogger logger, IrcSettings settings, ConnectionHandler handler) {
		this.logger		= logger;
		this.settings	= settings;
		this.handler	= handler;
		serverIndex		= 0;
	}
	
	//------------------------------------------------------------------------------
	//## lifecycle
	
	public void init() throws IOException, NickAlreadyInUseException, IrcException {
		keepRunning	= true;
		
		//startIdentServer();	// if you need one.. 
		//setVerbose(true);		// useful for debugging
		setAutoNickChange(true);
		setName(settings.nick);
		setLogin(settings.login);
		setVersion(settings.version);
		setFinger(settings.finger);
		setEncoding(settings.encoding);
		setMessageDelay(settings.delay);
		
		// TODO should try the next server instead of dying with a ConnectionRefusedException
		connect();
	}

	public void exit() {
		// for (var channel : getChannels())	partChannel(channel);
		keepRunning	= false;
		disconnect();
		dispose();
	}
	
	/** true unless the personality has been killed with a call to #exit */
	public boolean isAlive() {
		return keepRunning;
	}
	
	/** connect to the configured server */
	public void connect() throws NickAlreadyInUseException, IOException, IrcException {
		IrcServerSetting	server	= nextServer();
		String	host		= server.host;
		int		port		= server.port;
		String	password	= server.password;
		
		logger.message("connecting to server: " + host + "/" + port);
		if (!"".equals(password))
			connect(host, port, password);
		else
			connect(host, port);
	}
	
	/** iterate over the server list and return the next one */
	private IrcServerSetting nextServer() {
		IrcServerSetting	server	= settings.servers[serverIndex];
		serverIndex	= (serverIndex+1)%settings.servers.length;
		return server;
	}

	//------------------------------------------------------------------------------
	//## line listener
	
    /**
     * This method handles events when any line of text arrives from the server,
     * then calling the appropriate method in the PircBot.  This method is
     * protected and only called by the InputThread for this instance.
     *  <p>
     * This method may not be overridden!
     * 
     * @param line The raw line of text from the server.
     */
    @Override
	protected void handleLine(String line) {
		try { 
			super.handleLine(line);
			handler.onRawLine(this, line); 
		}
		catch (Throwable t) { 
			handler.onError(this, t); 
		}
	}
	
	//------------------------------------------------------------------------------
	//## connection listener
    
	/** This method is called once the PircBot has successfully connected to the IRC server. */
	@Override
	protected void onConnect() {
		logger.message("connected");
		
		handler.onConnect(this);
	}
	
	/** This method carries out the actions to be performed when the PircBot gets disconnected. */
	@Override
	protected void onDisconnect() {
		logger.message("disconnected");
		
		handler.onDisconnect(this);
	} 
	
	//------------------------------------------------------------------------------
	//## channel listener
	
	/** This method is called whenever someone (possibly us) joins a channel which we are on. */	
	@Override
	protected  void onJoin(String channel, String sender, String login, String hostname) {
		handler.onJoin(
			new Routing(
				this,
				new Source(                 
					sender, 
					login, 
					hostname),
				new Target(
						channel)),
			channel);
	}
	
	/** This method is called whenever someone (possibly us) parts a channel which we are on. */
	@Override
	protected void onPart(String channel, String sender, String login, String hostname) {
		handler.onPart(
			new Routing(
				this,
				new Source(                 
					sender, 
					login, 
					hostname),
				new Target(
						channel)),
			channel);
	}		
		  
	//------------------------------------------------------------------------------
	//## event handler
	
    /** This method is called whenever someone (possibly us) is kicked from any of the channels that we are in. */
	@Override
	protected void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
		handler.onKick(
			new Routing(
				this,
				new Source(                 
					kickerNick, 
					kickerLogin, 
					kickerHostname),
				new Target(
						channel)),
			recipientNick,
			reason);
	}
	
	/** This method is called whenever someone (possibly us) changes nick on any of the channels that we are on. */
	@Override
	protected void onNickChange(String oldNick, String login, String hostname, String newNick) {
		handler.onNickChange(
			new Routing(
				this,
				new Source(
					oldNick, 
					login, 
					hostname),
				new Target(
					getNick())),
			newNick);
	}
          

	/** This method is called whenever a message is sent to a channel. */
	@Override
	protected void onMessage(String channel, String sender, String login, String hostname, String message) {
		handler.onPrivMsg(
			new Routing(
				this,
				new Source(
					sender, 
					login, 
					hostname),
				new Target(
					channel)),
			message);
	}
	
	/** This method is called whenever a private message is sent to the PircBot. */
	@Override
	protected void onPrivateMessage(String sender, String login, String hostname, String message) {
		handler.onPrivMsg(
			new Routing(
				this,
				new Source(
					sender, 
					login, 
					hostname),
				new Target(
					getNick())),
			message);
	} 
	
	/** This method is called whenever an ACTION is sent from a user. */
	@Override
	protected void onAction(String sender, String login, String hostname, String target, String action) {
		handler.onAction(
			new Routing(
				this,
				new Source(
					sender, 
					login, 
					hostname),
				new Target(
					target)),
			action);
	}
	
	/** This method is called whenever we receive a notice. */
	@Override
	protected  void onNotice(String sourceNick, String sourceLogin, String sourceHostname, String target, String notice) {
		handler.onNotice(
			new Routing(
				this,
				new Source(
					sourceNick, 
					sourceLogin, 
					sourceHostname),
				new Target(
					target)),
			notice);
	} 
}
