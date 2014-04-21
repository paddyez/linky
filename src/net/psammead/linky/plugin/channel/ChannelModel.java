package net.psammead.linky.plugin.channel;

import java.util.ArrayList;
import java.util.List;

import net.psammead.linky.persistence.Persistent;

public class ChannelModel implements Persistent {
	private	List<String>	channels;
	
	public ChannelModel() {
		channels	= new ArrayList<String>();
	}
	
	public List<String> getChannels() {
		return channels;
	}

	public void setChannels(List<String> channels) {
		this.channels = channels;
	}
	
	public Object getPersistentModel() { return channels; }
	public String getPersistentName() { return "channel"; }
	@SuppressWarnings("unchecked")
	public void setPersistentModel(Object model) { this.channels = (List<String>)model; }
}
