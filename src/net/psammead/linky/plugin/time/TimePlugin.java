package net.psammead.linky.plugin.time;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import net.psammead.linky.Command;
import net.psammead.linky.CommandBase;
import net.psammead.linky.PluginBase;
import net.psammead.linky.irc.Routing;

public final class TimePlugin extends PluginBase {
	//------------------------------------------------------------------------------
	//## implementation
	
	// config
	private DateFormat	dateFormat;
	private String[]	timeZones;
	
	public TimePlugin() {}
	
	@Override
	public void init() {
		// config
		dateFormat	= context.config().getDateFormat("dateFormat");
		timeZones	= context.config().getStringArray("timeZones");
	}
	
	//------------------------------------------------------------------------------
	//## command handler
	
	@Override
	public Command[] commands() { 
		return new Command[] {
			new CommandBase("time") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdTime(routing);
				}
			}
		};
	}
	
	/** print world time */
	private void cmdTime(Routing routing) {
		DateFormat	format	= (DateFormat)dateFormat.clone();
		Date		now		= new Date();                                            
		String		reply	= "";
		int			oldHour	= 0; 
		for (String id : timeZones) {
			format.setTimeZone(TimeZone.getTimeZone(id));
			String	name	= formatTimezoneName(id);
			String	time	= format.format(now);
	
			int	newHour	= format.getCalendar().get(Calendar.HOUR_OF_DAY);
			if (newHour < oldHour)	reply	+= " || ";
			else					reply	+= " | ";
			oldHour	= newHour;
			
			reply	+= name + " " + time;
		}
		reply	= reply.substring(3);
		routing.reply(reply);
	}
	
	private String formatTimezoneName(String id) {
		//### BÄH - depends on the name format
		return id.replaceAll(".*/", "").replaceAll("_", " ");
	}
}