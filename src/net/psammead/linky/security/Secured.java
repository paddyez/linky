package net.psammead.linky.security;

import java.util.Set;


public interface Secured {
	/** roles allowed to use this object */
	Set<Role> allowedFor();
	
	/** roles allowed to see this object */
	Set<Role>  visibleFor();
}
