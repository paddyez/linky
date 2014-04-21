package net.psammead.linky.plugin.factoid;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.psammead.linky.persistence.Persistent;

/** stores and retrieves Factoids */
public class FactoidModel implements Persistent {
	public static final int TYPE_SENTENCE	= 1;
	public static final int TYPE_ANSWER		= 2;
	public static final int TYPE_ACTION		= 3;
	
	/** Factoid Sets hashed by lower(topic) */
	private final Map<String, Set<Factoid>>	factMap;
	
	public FactoidModel() {
		factMap	= new HashMap<String, Set<Factoid>>();
	}
	
	//------------------------------------------------------------------------------
	//## public API
	
	/** adds a factoid */
	public void learn(String channel, String author, String topic, String text, int type) {
		// this is necessary to keep xstream's XML clean :/
		topic	= filterInput(topic);
		text	= filterInput(text);
		
		final String	key			= topic.toLowerCase();
		Set<Factoid>	factSet	= factMap.get(key);
		if (factSet == null) {
			factSet	= new HashSet<Factoid>();
			factMap.put(key, factSet);
		}
		final Factoid	factoid	= new Factoid(channel, author, new Date(), topic, text, type);
		factSet.add(factoid);
	}
	
	/** for gets all factoids about a topic */
	public void forget(String topic) {
		final String	key	= topic.toLowerCase();
		factMap.remove(key);
	}

	/** gets one random factoid about a topic */
	public Factoid getRandom(String topic) {
		final String	key	= topic.toLowerCase();
		final Set<Factoid>	factSet	= factMap.get(key);
		if (factSet == null)	return null;
		final int		index	= (int)(Math.random() * factSet.size());
		final Factoid	factoid	= (Factoid)factSet.toArray()[index];
		return factoid;
	}
	
	/** get all topics matching a substring, "." is a synonym for all topics */
	public Set<String> search(String topicPart) {
		final Set<String>	topics	= new HashSet<String>();
		if (".".equals(topicPart))	return topics;
		final String		search	= topicPart.toLowerCase();
		final Set<String>	out		= new HashSet<String>();
		for (String topic : topics) {
			if (topic.toLowerCase().contains(search))	out.add(topic);
		}
		return out;
	}
	
	/** gets all factoids about a topic 
	public Set getAll(String topic) {
		String	key	= topic.toLowerCase();
		Set	factSet	= factMap.get(key);
		if (factSet == null)	return null;
		return Collections.unmodifiableSet(factSet);
	}
	*/
	
	/** returns the number of topics which have any factoids assigned */
	public int getTopicCount() {
		return factMap.size();
	}
	
	/** returns the number of factoids */
	public int getFactoidCount() {
		int	result	= 0;
		for (Set<Factoid> set : factMap.values()) {
			result	+= set.size();
		}
		return result;
	}
	
	//------------------------------------------------------------------------------
	//## implementation of the Persistent interface
	
	public String getPersistentName()				{ return "factoid";	}
	public Object getPersistentModel()				{ return factMap;	}
	@SuppressWarnings("unchecked")
	public void setPersistentModel(Object model)	{ factMap.clear(); factMap.putAll((Map)model);	}
	
	//### UGLY HACK - prevents xstream from saving unloadable data
	/** filter out characters xstream barfs upon */
	private String filterInput(String s) {
		return  s.replaceAll("[\\000-\\037]+", "");
	}

}
