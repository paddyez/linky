package net.psammead.linky.plugin.wikilinks;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.psammead.linky.Command;
import net.psammead.linky.CommandBase;
import net.psammead.linky.PluginBase;
import net.psammead.linky.irc.ConnectionHandler;
import net.psammead.linky.irc.ConnectionHandlerAdapter;
import net.psammead.linky.irc.Routing;

public final class WikiLinksPlugin extends PluginBase {
	// constants
	private static final int		LINK_LIMIT			= 5;								// maximum number of links accepted in a single message
	private static final String		INNER_PATTERN		= " *(.*?) *(?:\\|.*?)?";
	private static final Pattern	EXTRACT_PATTERN		= Pattern.compile(
		"\\[\\[" + INNER_PATTERN + "\\]\\]"
		+ "|" + 
		"\\{\\{" + INNER_PATTERN + "\\}\\}"
	);
	//### wrong, hardcoded! other namespaces are needed, too!
	private static final String	TEMPLATE_NS	= "Template";	
	
	// supported languages
	private static final List<String>	languages	= Arrays.asList((
		"aa,ab,af,ak,als,am,an,ar,arc,as,ast,av,ay,az,ba,be,bg,bh,bi,bm,bn,bo,br,bs,ca,ce,ch,cho,"	+
		"chr,chy,co,cr,cs,csb,cv,cy,da,de,dv,dz,ee,el,en,eo,es,et,eu,fa,ff,fi,fj,fo,fr,fy,ga,gd,"	+
		"gl,gn,gu,gv,ha,haw,he,hi,ho,hr,ht,hu,hy,hz,ia,id,ie,ig,ii,ik,io,is,it,iu,ja,jbo,jv,ka,kg,"	+
		"ki,kj,kk,kl,km,kn,ko,kr,ks,ku,kv,kw,ky,la,lb,lg,li,ln,lo,lt,lv,mg,mh,mi,mk,ml,mn,mo,mr,"	+
		"ms,mt,mus,my,na,nah,nb,nds,ne,ng,nl,nn,no,nv,ny,oc,om,or,pa,pi,pl,ps,pt,qu,rm,rn,ro,"		+
		"roa-rup,ru,rw,sa,sc,scn,sd,se,sg,sh,si,simple,sk,sl,sm,sn,so,sq,sr,ss,st,su,sv,sw,ta,te,"	+
		"tg,th,ti,tk,tl,tlh,tn,to,tokipona,tpi,tr,ts,tt,tw,ty,ug,uk,ur,uz,ve,vi,vo,wa,wo,xh,yi,yo,"	+
		"za,zh,zh-min-nan,zu")
		.split(",")
	);
	
	// supported wikis hashed by prefix
	private Map<String, Wiki> wikiByName;
	private Map<String, Wiki> wikiByShortcut;
	
	private boolean expandLinks;
	private int		expandedCount;

	// this wiki is used if no wiki prefix is given
	private Wiki	defaultWiki;
	
	// this language is used if no language prefix is given
	private String	defaultLanguage;
	
	public WikiLinksPlugin() {}
		
	@Override
	public void init() {
		expandLinks		= true;
		expandedCount	= 0;
		
		wikiByName		= new HashMap<String, Wiki>();
		wikiByShortcut	= new HashMap<String, Wiki>();
		
		// the * character in the URL is replaced with the language code.
		addWiki("wikipedia",	"w",		"UTF-8",	"http://*.wikipedia.org/wiki/");
		addWiki("meta",			"m",		"UTF-8",	"http://meta.wikimedia.org/wiki/");
		addWiki("commons",		"c",		"UTF-8",	"http://commons.wikimedia.org/wiki/");
		addWiki("wikibooks",	"b",		"UTF-8",	"http://*.wikibooks.org/wiki/");			
		addWiki("wikiquote",	"q",		"UTF-8",	"http://*.wikiquote.org/wiki/");			
		addWiki("wiktionary",	"wikt",		"UTF-8",	"http://*.wiktionary.org/wiki/");				
		addWiki("wikisource",	"s",		"UTF-8",	"http://*.wikisource.org/wiki/");
		addWiki("wikinews",		"n",		"UTF-8",	"http://*.wikinews.org/wiki/");				
		addWiki("wikiversity",	"v",		"UTF-8",	"http://*.wikiversity.org/wiki/");				
		addWiki("kamelopedia",	"kamelo",	"UTF-8",	"http://kamelopedia.mormo.org/index.php/");
		addWiki("intern",		"rk",		"UTF-8",	"https://intern.wikimedia.de/wiki/");
		addWiki("wikipress",	"pr",		"UTF-8",	"http://wikipress.wikidev.net/");
		// special
		addWiki("wikiweise",	"ww",		"UTF-8",	"http://www.wikiweise.de/wiki/");
		addWiki("bugzilla",		"bug",		"UTF-8",	"http://bugzilla.wikimedia.org/show_bug.cgi?id=");
		addWiki("otrs",			"o",		"UTF-8",	"https://secure.wikimedia.org/otrs/index.pl?Action=AgentTicketZoom&TicketNumber=");
		
		String defaultWikiId 		= context.config().getString("defaultWiki");
		defaultWiki		= findWiki(defaultWikiId);	// this wiki is used if no wiki prefix is given
		if (defaultWiki == null)		throw new IllegalArgumentException("wiki " + defaultWikiId + " is unknown");
		
		defaultLanguage	= context.config().getString("defaultLanguage");
		if (!language(defaultLanguage))	throw new IllegalArgumentException("language " + defaultLanguage + " is unknown");
	}

	//------------------------------------------------------------------------------
	//## status output

	/** called by the HelpPlugin - output information in the status command */
	@Override
	public String status() {
		return	context.message("defaultWiki", defaultWiki.name, defaultWiki.shortcut, defaultWiki.url)	+ "\n" 
			+	context.message("defaultLanguage", defaultLanguage)										+ "\n" 
			+	(expandLinks ? context.message("linksOn") : context.message("linksOff"))				+ "\n"
			+ 	context.message("expandCount", expandedCount);
	}
	
	//------------------------------------------------------------------------------
	//## irc event handler
	
	@Override
	public ConnectionHandler handler() {
		return new ConnectionHandlerAdapter() {
			/** This method is called whenever a message is sent to a channel. */
			@Override
			public void onPrivMsg(Routing routing, String message) {
				if (expandLinks)	expandLinks(routing, message);
			}
			
			/** This method is called whenever an ACTION is sent from a user. */
			@Override
			public void onAction(Routing routing, String action) {
				if (expandLinks)	expandLinks(routing, action);
			}
		};
	}
	
	//------------------------------------------------------------------------------
	//## command handler
	
	@Override
	public Command[] commands() { 
		return new Command[] {
			new CommandBase("links") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdLinks(routing, (Boolean)args[0]);
				}
			}
		};
	}
	
	/** links on/off : switch the link expansion on or off */
	private void cmdLinks(Routing routing, boolean on) {
		expandLinks	= on;
		routing.reply(context.message(
				on ? "linksOn" : "linksOff"));
	}
		
	//------------------------------------------------------------------------------
	//## private implementation
	
	/** find out whether the argument is a language (must be lowercase) */
	private boolean language(String candidate) {
		return languages.contains(candidate);
	}
	
	/** register a wiki */
	private void addWiki(String name, String shortcut, String encoding, String url) {
		Wiki	wiki	= new Wiki(name, shortcut, encoding, url);
		if (name != null)		wikiByName.put(name,		wiki);
		if (shortcut != null)	wikiByShortcut.put(shortcut, wiki);
	}
	
	/** get a wiki by its name or shortcut (must have the same case as registered!) */
	private Wiki findWiki(String candidate) {
		Wiki	wiki;
		wiki	= wikiByShortcut.get(candidate);
		if (wiki != null)	return wiki;
		wiki	= wikiByName.get(candidate);
		if (wiki != null)	return wiki;
		return null;	
	}
	
	private class Wiki {
		private final String	name;
		private final String	shortcut;
		private final String	encoding;
		private final String	url;

		public Wiki(String name, String shortcut, String encoding, String url) {
			this.name		= name;
			this.shortcut	= shortcut;
			this.encoding	= encoding;
			this.url		= url;
		}
	}
	
	/** parse the message and print out links */
	private void expandLinks(Routing routing, String message) {
		// reply only to a channel, in query every line is expected to be a command!
		if (!routing.target.channelFlag)	return;
		
		// parse the message and loop over page titles
		Set<String>		links	= new HashSet<String>();
		boolean	grinned	= false;
		Matcher matcher	= EXTRACT_PATTERN.matcher(message);
		while (matcher.find() && links.size() < LINK_LIMIT) {
			String	bracket		= matcher.group(1);
			String	curly		= matcher.group(2);
			boolean	template	= bracket == null;
			String	title		= template ? curly : bracket;
			if (title.length() == 0)	continue;
			
			// grin on [[linky]] if not done already
			String	ourNick	= routing.connection.getNick();
			if (title.toLowerCase().equals(ourNick.toLowerCase())) {
				if (!grinned) {
					routing.reply(context.message("grin"));
					grinned	= true;
				}
				continue;
			}
			
			// print encoded links if not done already
			String url;
			try {
				url = makeURL(title, template);
			}
			catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
			if (links.contains(url))	continue;
			expandedCount++;
			links.add(url);
			routing.reply(url);
		}
	}
	
	/** make an URL from a page title with interwiki- and interlanguage */
	private String makeURL(String title, boolean template) throws UnsupportedEncodingException {
		if (template) {
			// remove subst:
			if (title.startsWith("subst:")) {
				title	= title.substring("subst:".length());
			}
			// a : at the start means: article namespace
			if (title.startsWith(":")) {
				title		= title.substring(":".length());
				template	= false;
			}
		}
		else {
			// a : at the start is meaningless
			if (title.startsWith(":")) {
				title	= title.substring(":".length());
			}
		}
		
		// calculate an URL with a * in it for the language
		Split	split		= new Split(title, ':');
		String	candidate	= split.prefix.toLowerCase();
		Wiki	wiki		= findWiki(candidate);
		String	language	= defaultLanguage;
		if (wiki != null) {
			// the default wiki is not used as a prefix!
			if (!candidate.equalsIgnoreCase(defaultWiki.name)) {
				title	= split.suffix;
				if (candidate.equalsIgnoreCase(wiki.name)) {
					language	= "en";
				}
			}
		}
		else {
			wiki	= defaultWiki;
		}
		
		// insert a language if there is a * in the base
		String	base			= wiki.url;
		boolean	 multilingual	= base.indexOf('*') != -1;
		if (multilingual) {
			Split		split2		= new Split(title, ':');
			String	maybe		= split2.prefix.toLowerCase();
			if (language(maybe)) {
				language	= maybe;
				title		= split2.suffix;
			}
			base	= base.replaceAll("\\*", language);
		}
		
		// HACK: wikiweise behaves quite differently
		if (wiki.name.equals("wikiweise")) {	// the same as: base.equals("http://www.wikiweise.de/wiki/")
			String	page	= URLEncoder.encode(title, wiki.encoding)
								.replaceAll("\\+", "%20");
			return base + page;
		}
		
		// mediawiki uses underscores instead of spaces in page titles
		title	= title.replace(' ', '_');
		
		//### WRONG, must not prefix with the TEMPLATE_NS when there already is another template
		if (template)	title	= TEMPLATE_NS + ":" + title;
		
		// url-encode the title
		String	page	= encodeTitle(title, wiki.encoding);
	
		// check whether ?redirect=no is sensible
		boolean	redirect	= true;
		if (title.indexOf('#') != -1)									redirect	= false;
		if (wiki.name.equals("wikipedia") && title.startsWith("WP:"))	redirect	= false;	
		if (wiki.name.equals("wikiweise"))								redirect	= false;
		if (wiki.name.equals("bugzilla"))								redirect	= false;
		
		// return the complete url
		String	complete	= base + page;
		if (redirect) {
			complete	+= (complete.indexOf("?") == -1 ? "?" : "&");
			complete	+= "redirect=no";
		}
		return complete;
	}
	
	/** mediawiki-specific URL-encoding */
	private String encodeTitle(String title, String charset) throws UnsupportedEncodingException {
		return URLEncoder.encode(title, charset)
				.replaceAll("%%", 	"\u0000")						// save %% 
				.replaceAll("%3a",	":").replaceAll("%3A",	":")	// decode :
				.replaceAll("%2f",	"/").replaceAll("%2F",	"/")	// decode /
				.replaceAll("%23",	"#")							// decode #
				.replaceAll("\u0000", "%%");						// restore %%
	}
	
	/** split at the first separator into prefix and suffix */
	private static class Split {
		public String	prefix;
		public String	suffix;
		public Split(String title, char separator) {
			prefix	= "";
			suffix	= title;
			int	pos		= title.indexOf(separator);
			if (pos != -1) {
				prefix	= title.substring(0, pos);
				suffix	= title.substring(pos+1);
			}
		}
	}
}
