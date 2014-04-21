package net.psammead.linky.security;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public final class SecuredUtil {
	private SecuredUtil() {}
	
	public static <T extends Secured> List<T> allowed(List<T> secureds, Set<Role> roles) {
		List<T>	out	= new ArrayList<T>();
		for (T secured : secureds) {
			if (allowed(secured, roles)) {
				out.add(secured);
			}
		}
		return out;
	}
	
	public static <T extends Secured> List<T> visible(List<T> secureds, Set<Role> roles) {
		List<T>	out	= new ArrayList<T>();
		for (T secured : secureds) {
			if (visible(secured, roles)) {
				out.add(secured);
			}
		}
		return out;
	}
	
	public static boolean allowed(Secured secured, Set<Role> roles) {
		return permitted(secured.allowedFor(), roles);
	}
	
	public static boolean visible(Secured secured, Set<Role> roles) {
		return permitted(secured.visibleFor(), roles);
	}
	
	private static boolean permitted(Set<Role> securedRoles, Set<Role> userRoles) {
		Set<Role>	appliable	= new HashSet<Role>(userRoles);
		appliable.retainAll(securedRoles);
		return !appliable.isEmpty();
	}
	
	public static  Set<Role> parseRoles(String descriptor) {
		Set<Role>	out		= new HashSet<Role>();
		String[]	parts	= descriptor.split(",");
		for (String part : parts) {
			part	= part.trim();
			Role	role	= new Role(part);
			out.add(role);
		}
		return Collections.unmodifiableSet(out);
	}
}
