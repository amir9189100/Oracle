/**
 * This file is part of Oracle, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Helion3 http://helion3.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.helion3.oracle.commands;

import com.helion3.oracle.Oracle;
import com.helion3.oracle.commandlibs.CallInfo;
import com.helion3.oracle.commandlibs.Executor;
import com.helion3.oracle.commandlibs.SubHandler;

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
		.setHandler(new BanCommand());
		
		/**
		 * /ban
		 */
		addSub("ban-ip", "oracle.ban")
		.setMinArgs(1)
		.allowConsole()
		.setHandler(new BanIpCommand());
		
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
		.setHandler(new IsonCommand());
		
		/**
		 * /played
		 */
		addSub("played", "oracle.played")
		.allowConsole()
		.setHandler(new PlayedCommand());
		
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
		.setHandler(new SeenCommand());

		/**
		 * /unban
		 */
		addSub("unban", "oracle.unban")
		.setMinArgs(1)
		.allowConsole()
		.setHandler(new UnbanCommand());
		
		/**
		 * /unban-ip
		 */
		addSub("unban-ip", "oracle.unban")
		.setMinArgs(1)
		.allowConsole()
		.setHandler(new UnbanIpCommand());
		
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