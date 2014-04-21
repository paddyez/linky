==linky==
===abstract===

linky is a little irc-bot consisting of a small framework and set of modules. at the moment, linky speaks german, english and polish. it is possible to run multiple, completely separate personalities within a single java-VM.

existing modules can expand MediaWiki-links, store simple factoids and support seen-queries and online-help.

linky is written in java and uses the pircbot-framework to connect to an IRC-server.

===contact===

the dot gray at gmx dot net (write in english or german)

===license===

linky is released under the GPL.

===requirements===

JRE 1.6.0 or newer	http://java.sun.com/javase/

===usage===

    modify data/linky.properties according to your needs. especially *.plugin.Owner.initialOwnerName and *.plugin.Owner.initialOwnerPass
    on linux or mac os x start linky with
    $ bin/linky
    on windows start it with
    $ bin\linky.bat
    join the channel the bot is configured to be in
    type !hilfe (or !help)

