package net.psammead.linky.plugin.factoid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.psammead.linky.Command;
import net.psammead.linky.CommandBase;
import net.psammead.linky.PluginBase;
import net.psammead.linky.irc.Routing;
import net.psammead.util.Scan;

public final class FactoidPlugin extends PluginBase {
	private int				topicLimit;
	private FactoidModel	factoidModel;
	
	public FactoidPlugin() {}
	
	@Override
	public void init() {
		topicLimit		= context.config().getInt("topicLimit");
		factoidModel	= new FactoidModel();
	}
	
	//------------------------------------------------------------------------------
	//## life cycle
	
	/** is called before the plugin receives any messages - may be overwritten by the plugin */
	@Override
	public void afterLoad() {
		context.registerPersistent(factoidModel);
	}
	
	/** is called before the plugin is removed and after any messages - may be overwritten by the plugin */
	@Override
	public void beforeUnload() {
		context.unregisterPersistent(factoidModel);
	}
	
	//------------------------------------------------------------------------------
	//## status output
	
	/** called by the HelpPlugin - output information in the status command */
	@Override
	public String status() {
		return context.message("status",
				factoidModel.getTopicCount(), 
				factoidModel.getFactoidCount());
	}
	
	//------------------------------------------------------------------------------
	//## command handler
	
	@Override
	public Command[] commands() { 
		return new Command[] {
			new CommandBase("query") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdQuery(routing, (String[])args[0]);
				}
			},
			new CommandBase("fallback") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdQuery(routing, (String[])args[0]);
				}
			},
			new CommandBase("learnSentence") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdLearnSentence(routing, (String)args[0], (String)args[1]);
				}
			},
			new CommandBase("learnAnswer") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdLearnAnswer(routing, (String)args[0], (String)args[1]);
				}
			},
			new CommandBase("learnAction") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdLearnAction(routing, (String)args[0], (String)args[1]);
				}
			},
			new CommandBase("forget") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdForget(routing, (String)args[0]);
				}
			},
			new CommandBase("search") {
				@Override public void execute(Routing routing, Object[] args) {
					cmdSearch(routing, (String)args[0]);
				}
			}
		};
	}
	
	/** lets the bot learn a is-sentence about a topic */
	private void cmdLearnSentence(Routing routing, String topic, String text) {
		String	channel	= routing.target.channelFlag ? routing.target.identifier : null;
		factoidModel.learn(channel, routing.source.nick, topic, text, FactoidModel.TYPE_SENTENCE);
		routing.reply(context.message("learned"));
	}
	
	/** lets the bot learn an answer-factoid for a topic */
	private void cmdLearnAnswer(Routing routing, String topic, String text) {
		String	channel	= routing.target.channelFlag ? routing.target.identifier : null;
		factoidModel.learn(channel, routing.source.nick, topic, text, FactoidModel.TYPE_ANSWER);
		routing.reply(context.message("learned"));
	}
	
	/** lets the bot learn an action-factoid for a topic */
	private void cmdLearnAction(Routing routing, String topic, String text) {
		String	channel	= routing.target.channelFlag ? routing.target.identifier : null;
		factoidModel.learn(channel, routing.source.nick, topic, text, FactoidModel.TYPE_ACTION);
		routing.reply(context.message("learned"));
	}
	
	/** makes the bot forget everything about a topic */
	private void cmdForget(Routing routing, String topic) {
		Factoid	factoid	= factoidModel.getRandom(topic);
		if (factoid == null) {
			routing.reply(context.message("unknown", topic));
			return;
		}
		factoidModel.forget(topic);
		routing.reply(context.message("forgotten"));
	}
	
	/** asks the bot for a random factoid about a topic */
	private void cmdQuery(Routing routing, String[] args) {
		String			query		= args[0];
		List<String>	ingredients	= new ArrayList<String>(Arrays.asList(args));
		ingredients.set(0, routing.source.nick);

		Factoid	factoid	= factoidModel.getRandom(query);
		if (factoid == null) {
			routing.reply(context.message("unknown", query));
			return;
		}
		
		String	cookedText		= cook(factoid.text, ingredients);
		switch (factoid.type) {
			case FactoidModel.TYPE_SENTENCE:
				routing.reply(context.message("is", factoid.topic, cookedText));
				break;
			case FactoidModel.TYPE_ANSWER:
				routing.reply(cookedText);
				break;
			case FactoidModel.TYPE_ACTION:
				routing.replyAction(cookedText);
				break;
		}
	}
	
	/** 
	 * substitute $0..n with ingredients[0]..ingredients[n] and $$ with $.
	 * if ingredients[n] does not exist, no substitution takes place.
	 * 
	 */
	private String cook(String raw, List<String> ingredients) {
		final StringBuilder	b		= new StringBuilder();
		final Scan			scan	= new Scan(raw);
		for (;;) {
			final String	s	= scan.stopAt("$");
			b.append(s);
			if (scan.isFinished())	break;
			if (scan.is("$$")) {
				b.append("$");
			}
			else if (scan.is("$")) {
				final String	dec	= scan.take(Scan.DEC_NUM, 2);
				if (dec.length() > 0) {
					int	index	= Integer.parseInt(dec);
					if (index < ingredients.size()) {
						b.append(ingredients.get(index));
					}
					else {
						b.append("$").append(dec);
					}
				}
				else { 
					b.append("$"); 
				}
			}
			
		}
		return b.toString();
	}
	
	/** asks the bot about topics it knows something about */
	private void cmdSearch(Routing routing, String topicPart) {
		// TODO use a MultiType
		Set<String>	topics	= factoidModel.search(topicPart);
		if (!topics.isEmpty()) {
			String	found	= enumerate(topics, topicLimit);
			routing.reply(context.message("found", topicPart, found));
		}
		else {
			routing.reply(context.message("nothing", topicPart));
		}
	}
	
	//------------------------------------------------------------------------------
	//## private stuff
	
	//### should not generate lines with more than ~500 characters
	/** enumerate Strings */
	private String enumerate(Collection<String> strings, int limit) {
		int	size	= strings.size();
		int	last	= Math.min(size, limit);
		int	current	= 0;
		StringBuffer	out		= new StringBuffer();
		for (String string : strings) {
			out.append(string);
				 if (current == last-1)						break;
			else if (current == last-2 && size <= limit)	out.append(context.message("enumLast"));
			else											out.append(context.message("enumSeparator"));
			current ++;
		}
		if (size > limit) {
			out.append(context.message("enumMore", Integer.toString(size - limit)));
		}
		return out.toString();
	}
}