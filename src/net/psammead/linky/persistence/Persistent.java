package net.psammead.linky.persistence;

public interface Persistent {
	public String getPersistentName();
	public Object getPersistentModel();
	public void setPersistentModel(Object model);
}
