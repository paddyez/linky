package net.psammead.linky.settings;

import java.util.Set;

import net.psammead.linky.security.Role;

public final class CommandSettings {
	public final String		syntax;
	public final String		description;
	public final Set<Role>	visibleFor;
	public final Set<Role>	allowedFor;
	public final int		weight;

	public CommandSettings(String syntax, String description, Set<Role> visibleFor, Set<Role> allowedFor, int weight) {
		this.syntax			= syntax;
		this.description	= description;
		this.visibleFor		= visibleFor;
		this.allowedFor		= allowedFor;
		this.weight			= weight;
	}
}
