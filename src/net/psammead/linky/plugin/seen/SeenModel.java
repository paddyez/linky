package net.psammead.linky.plugin.seen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.psammead.linky.persistence.Persistent;

/** remembers when a User was seen the last time */
public class SeenModel implements Persistent {
	private static final int	MAX_ENTRIES	= 2048;
	
	/** Seen objects hashed by lower(nick) */
	private Map<String, Seen> seens;
	
	public SeenModel() {
		seens	= new HashMap<String, Seen>();
	}
	
	//------------------------------------------------------------------------------
	//## public API
	
	/** remembers we have Seen someone */
	public void remember(String channel, String nick) {
		shorten();
		Seen	seen	= new Seen(channel, nick, new Date());
		seens.put(nick.toLowerCase(), seen);
	}
	
	/** returns the Seen entry for a nickname */
	public Seen query(String nick) {
		return seens.get(nick.toLowerCase());
	}
	
	/** returns the number of users we have seen */
	public int getSeenCount() {
		return seens.size();
	}
	
	/** shortens the model so no more than MAX_ENTRIES entries are left */ 
	private void shorten() {
		List<Seen>	all	= new ArrayList<Seen>(seens.values());
		int	count	= all.size();
		if (count < MAX_ENTRIES)	return;
		Collections.sort(all, new Comparator<Seen>() {
			public int compare(Seen o1, Seen o2) {
				return o1.getLastPost().compareTo(o2.getLastPost());
			}
		});
		List<Seen>	less	= all.subList(count-MAX_ENTRIES, count);
		seens.clear();
		for (Seen seen : less) {
			seens.put(seen.getNick().toLowerCase(), seen);
		}
	}
	
	//------------------------------------------------------------------------------
	//## implementation of the Persistent interface
	
	public String getPersistentName()				{ return "seen";	}
	public Object getPersistentModel()				{ return seens;		}
	@SuppressWarnings("unchecked")
	public void setPersistentModel(Object model)	{ seens = (Map<String, Seen>)model;	}
}
