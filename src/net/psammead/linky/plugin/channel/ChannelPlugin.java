package net.psammead.linky.plugin.channel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.psammead.linky.PluginBase;
import net.psammead.linky.irc.Connection;
import net.psammead.linky.irc.ConnectionHandler;
import net.psammead.linky.irc.ConnectionHandlerAdapter;
import net.psammead.linky.irc.Routing;
import net.psammead.util.StringUtil;

/** manages the channels a personality joins */
public class ChannelPlugin extends PluginBase {
	private ChannelModel	channelModel;
	
	public ChannelPlugin() {}
	
	@Override
	public void init() {
		channelModel	= new ChannelModel();
		channelModel.setChannels(channelList(
				context.config().getStringArray("initialChannels")));
	}
	
	//------------------------------------------------------------------------------
	//## life cycle

	/** is called before the plugin receives any messages - may be overwritten by the plugin */
	@Override
	public void afterLoad() {
		context.registerPersistent(channelModel);
	}
	
	/** is called before the plugin is removed and after any messages - may be overwritten by the plugin */
	@Override
	public void beforeUnload() {
		context.unregisterPersistent(channelModel);
	}
	
	//------------------------------------------------------------------------------
	//## status output

	/** called by the HelpPlugin - output information in the status command */
	@Override
	public String status() {
		List<String>	channels	= channelModel.getChannels();
		if (channels.size() == 0)	return context.message("noChannel");
		else						return context.message("inChannels", StringUtil.join(channels, " "));
	}
	
	//------------------------------------------------------------------------------
	//## irc event handler
	
	@Override
	public ConnectionHandler handler() {
		return new ConnectionHandlerAdapter() {
			/** This method is called once the PircBot has successfully connected to the IRC server. */
			@Override
			public void onConnect(Connection connection) {
				joinChannels(connection);
			}
//			/** This method carries out the actions to be performed when the PircBot gets disconnected. */
//			@Override
//			public void onDisconnect(Connection connection) {
//				// TODO doesn't belong here at all
//				if (!connection.isAlive()) {
//					context.log("disconnected: done");
//					return;
//				}
//				reconnect(connection);
//				joinChannels(connection);
//			}
			
			/** This method is called whenever someone (possibly us) joins a channel which we are on. */
			@Override
			public void onJoin(Routing routing, String channel) {
				rememberChannels(routing.connection);
			}
			/** This method is called whenever someone (possibly us) parts a channel which we are on. */
			@Override
			public void onPart(Routing routing, String channel) {
				rememberChannels(routing.connection);
			}
		};
	}
	
//	private void reconnect(Connection connection) {
//		context.log("reconnecting");
//		while (!connection.isConnected()) {
//			try {
//				connection.connect();
//			}
//			catch (Exception e) {
//				context.error(e);
//				context.log("error: retrying in 15 seconds");
//				try {
//					Thread.sleep(15000);
//				}
//				catch (InterruptedException e1) {
//					context.error(e1);
//				}
//			}
//		}
//		context.log("reconnected");
//	}
	
	private void rememberChannels(Connection connection) {
		channelModel.setChannels(channelList(connection.getChannels()));
	}
	
	private List<String> channelList(String[] channels) {
		return new ArrayList<String>(Arrays.asList(channels));
	}
	
	private void joinChannels(Connection connection) {
		for (String channel : channelModel.getChannels()) {
			context.log("joining channel: " + channel);
			connection.joinChannel(channel);
		}
	}
}
