package net.psammead.linky.plugin.help;

import java.util.Set;

import net.psammead.linky.Command;
import net.psammead.linky.CommandBase;
import net.psammead.linky.CommandHelp;
import net.psammead.linky.PersonalityHelp;
import net.psammead.linky.PersonalityStatus;
import net.psammead.linky.PluginBase;
import net.psammead.linky.PluginHelp;
import net.psammead.linky.irc.Routing;
import net.psammead.linky.security.Role;
import net.psammead.util.StringUtil;

public class HelpPlugin extends PluginBase {
	private static final String	LINE	= "                                                                                                                                            ";
	
	private String	version;
	
	public HelpPlugin() {}
	
	@Override
	public void init() {
		version	= context.config().getString("version");
	}

	//------------------------------------------------------------------------------
	//## status output

	/** print out status */
	@Override
	public String status() {
		return context.message("status", version);
	}
	
	//------------------------------------------------------------------------------
	//## command handler
	
	@Override
	public Command[] commands() { 
		return new Command[] {
			new CommandBase("status") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdStatus(routing);
				}
			},
			new CommandBase("help") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdHelp(routing);
				}
			}
		};
	}
	
	/** print out status */
	private void cmdStatus(Routing routing) {
		Set<Role>	roles	= context.userRoles(routing);
		PersonalityStatus		status	= context.status(roles);
		routing.replyMulti(StringUtil.join(status.lines,"\n"));
	}
	
	/** print out usage */
	private void cmdHelp(Routing routing) {
		Set<Role>		roles		= context.userRoles(routing);
		PersonalityHelp	personality	= context.help(roles);
		
		// calculate maximum syntax width
		int	width	= 0;
		for (PluginHelp plugin : personality.plugins) {
			for (CommandHelp command : plugin.commands) {
				int	w	= command.syntax.length();
				if (width < w)	width	= w;
			}
		}
		
		// compile text
		String	text	= "";
		for (PluginHelp plugin : personality.plugins) {
			// TODO re-enable headers
			/*text	+=  Colors.BOLD + plugin.description + "\n";//  + Colors.NORMAL + " (" + plugin.name + ")\n";*/
			for (CommandHelp command : plugin.commands) {
				text	+= /*"   " + */ command.syntax
						+ LINE.substring(0, width - command.syntax.length() + 2) 
						+  command.description + "\n";
			}
		}
		
		routing.replyPrivate(text);
	}
}
