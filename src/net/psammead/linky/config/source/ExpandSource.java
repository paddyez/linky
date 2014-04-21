package net.psammead.linky.config.source;

import net.psammead.linky.config.ConfigSource;

/** expand ${key} patterns in the properties from the expansion */
public final class ExpandSource implements ConfigSource {
	private final ConfigSource	source;
	private final ConfigSource	expansion;

	public ExpandSource(ConfigSource source, ConfigSource expansion) {
		this.source		= source;
		this.expansion	= expansion;
	}

	public boolean has(String key) {
		return source.has(key);
	}
	
	public String get(String key) {
		String	s	= source.get(key);
		
		StringBuffer	out	= new StringBuffer();
		int	pos	= 0;
		for (;;) {
			int	next	= s.indexOf("${", pos);
			if (next == -1) {
				out.append(s.substring(pos, s.length()));
				return out.toString();
			}
			out.append(s.substring(pos, next));
			pos		= next+2;
			next	= s.indexOf("}", pos);
			if (next == -1)	throw new IllegalArgumentException("missing '}'");
			String	replaceKey		= s.substring(pos, next);
			String	replaceValue	= expansion.get(replaceKey);
			out.append(replaceValue);
			pos	= next + 1;
		}
	}
}
