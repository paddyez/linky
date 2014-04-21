package net.psammead.linky.plugin.owner;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.psammead.linky.persistence.Persistent;

/** manages and persists Owner objects */
public class OwnerModel implements Persistent {
	private final String	initialUser;
	private final String	initialPass;

	/** Owner objects hashed by their nick */
	private Map<String,Owner>	owners;

	public OwnerModel(String initialUser, String initialPass) {
		this.initialUser = initialUser;
		this.initialPass = initialPass;
		
		owners		= new HashMap<String, Owner>();
		addDefault();
	}
	
	//------------------------------------------------------------------------------
	//## public API
	
	/** get a Set of all owner nicks */
	public Set<String> getNicks() {
		return Collections.unmodifiableSet(owners.keySet());
	}
	
	/** get an Owner by his nick */
	public Owner getOwner(String nick) {
		return owners.get(nick);
	}
	
	/** returns a Collection containing all Owners */
	public Collection<Owner> getAllOwners() {
		return Collections.unmodifiableCollection(owners.values()); 
	}
	
	/** returns a Set containing all Owners nicknames */
	public Set<String> getOwnerNicks() {
		return Collections.unmodifiableSet(owners.keySet()); 
	}
	
	/** returns the number of registered Owners */
	public int getOwnerCount() {
		return owners.size();
	}
	
	/** adds an Owner and returns false if it's impossible */
	public boolean addOwner(String nick, String password) {
		if (owners.containsKey(nick))	return false;
		Owner	owner	= new Owner();
		owner.setNick(nick);
		owner.setPassword(password);
		owners.put(owner.getNick(), owner);
		return true;
	}

	/** deletes an Owner and returns true if it was possible */
	public boolean deleteOwner(String nick) {
		return owners.remove(nick) != null;
	}

	//------------------------------------------------------------------------------
	//## implementation of the Persistent interface
	
	public String getPersistentName()				{ return "owner";	}
	public Object getPersistentModel()				{ return owners;	}
	@SuppressWarnings("unchecked")
	public void setPersistentModel(Object model)	{ owners = (Map)model; fixTransient(); addDefault(); }

	/** initialize transient fields	*/
	private void fixTransient() {
		for (Owner owner : owners.values())	owner.setLoggedIn(false);
	}
	
	/** adds a dealt owner if no owner exists */
	private void addDefault() {
		if (!owners.isEmpty())	return;
		addOwner(initialUser, initialPass);
	}
}
