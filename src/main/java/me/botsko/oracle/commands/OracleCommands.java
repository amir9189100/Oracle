package me.botsko.oracle.commands;

import me.botsko.oracle.Oracle;
import me.botsko.oracle.commandlibs.CallInfo;
import me.botsko.oracle.commandlibs.Executor;
import me.botsko.oracle.commandlibs.SubHandler;

public class OracleCommands extends Executor {

	
	/**
	 * 
	 * @param oracle
	 */
	public OracleCommands(Oracle oracle) {
		super( oracle, "command", "oracle" );
		setupCommands();
	}
	

	/**
	 * 
	 */
	private void setupCommands() {
		
		final Oracle oracle = (Oracle) plugin;

		/**
		 * /alts
		 */
		addSub("alts", "oracle.alts")
		.setMinArgs(1)
		.allowConsole()
		.setHandler(new AltsCommand());
		
		/**
		 * /ban
		 */
		addSub("ban", "oracle.ban")
		.setMinArgs(1)
		.allowConsole()
		.setHandler(new BanCommand( oracle ));
		
		/**
		 * /lookup
		 */
		addSub("lookup", "oracle.lookup")
		.allowConsole()
		.setHandler(new LookupCommand());
		
		/**
		 * /ison
		 */
		addSub("ison", "oracle.ison")
		.setMinArgs(1)
		.allowConsole()
		.setHandler(new IsonCommand( oracle ));
		
		/**
		 * /played
		 */
		addSub("played", "oracle.played")
		.allowConsole()
		.setHandler(new PlayedCommand( oracle ));
		
		/**
		 * /playhist
		 */
		addSub("playhist", "oracle.played")
		.allowConsole()
		.setHandler(new PlayhistCommand());
		
		/**
		 * /seen
		 */
		addSub("seen", "oracle.seen")
		.allowConsole()
		.setHandler(new SeenCommand( oracle ));
		
		/**
		 * /stats
		 */
		addSub("stats", "oracle.stats")
		.allowConsole()
		.setHandler(new StatsCommand());
		
		/**
		 * /unban
		 */
		addSub("unban", "oracle.unban")
		.setMinArgs(1)
		.allowConsole()
		.setHandler(new UnbanCommand());
		
		/**
		 * /warnings
		 */
		addSub("warnings", "oracle.warnings")
		.allowConsole()
		.setHandler(new WarningsCommand());
		
		
		/**
		 * /oracle reload
		 */
		addSub("reload", "oracle.reload")
		.allowConsole()
		.setHandler(new SubHandler() {
            public void handle(CallInfo call) {
            	oracle.reloadConfig();
            	Oracle.config = oracle.getConfig();
				call.getSender().sendMessage( Oracle.messenger.playerMsg("Configuration reloaded successfully.") );
            }
		});
	}
}