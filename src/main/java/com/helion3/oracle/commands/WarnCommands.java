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

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.helion3.oracle.Oracle;
import com.helion3.oracle.commandlibs.CallInfo;
import com.helion3.oracle.commandlibs.Executor;
import com.helion3.oracle.commandlibs.SubHandler;
import com.helion3.oracle.utils.WarningUtil;

public class WarnCommands extends Executor {
	
	/**
	 * 
	 * @param prism
	 */
	public WarnCommands(Oracle oracle) {
		super( oracle, "command", "warn" );
		setupCommands();
	}

	/**
	 * 
	 */
	private void setupCommands() {
		
		/**
		 * /warn [username] [msg]
		 */
		addSub( new String[]{"default"}, "oracle.warn")
		.allowConsole()
		.setMinArgs(1)
		.setHandler(new SubHandler() {
            public void handle(CallInfo call) {
            	
            	if(call.getArgs().length >= 2){

            		if( call.getArg(0).equals("delete") ){
        				WarningUtil.deleteWarning( Integer.parseInt( call.getArg(1) ) );
        				call.getSender().sendMessage( Oracle.messenger.playerMsg("Warning deleted successfully."));
        				return;
            		}
					
					String reason = "";
					for (int i = 1; i < call.getArgs().length; i = i + 1){
						reason += call.getArgs()[i]+" ";
					}
					
					// Find the player whether online or not
					OfflinePlayer warned_player = Bukkit.getPlayer( call.getArg(0) );
					if( warned_player == null ){
						warned_player = Bukkit.getOfflinePlayer( call.getArg(0) );
					}
					
					if( warned_player == null ){
						call.getSender().sendMessage( Oracle.messenger.playerError( "Could not find a player by that name." ) );
						return;
					}

					// File warning
					try {
                        WarningUtil.fileWarning( warned_player, reason, call.getSender() );
                    } catch ( Exception e ) {
                        call.getSender().sendMessage( Oracle.messenger.playerError( e.getMessage() ) );
                        return;
                    }
					
					// Alert them
					if( warned_player instanceof Player ){
						Player pl = (Player) warned_player;
						pl.sendMessage( Oracle.messenger.playerError("=== OFFICIAL WARNING FILED FOR YOU ===") );
						pl.sendMessage( Oracle.messenger.playerMsg(reason) );
						pl.sendMessage( Oracle.messenger.playerError("Three warnings will result in a ban!") );
					}

					call.getSender().sendMessage( Oracle.messenger.playerMsg("Warning file successfully."));

					// This may be a third warning!
					WarningUtil.alertStaffOnWarnLimit( warned_player );

				}       	
            }
		});
	}
}