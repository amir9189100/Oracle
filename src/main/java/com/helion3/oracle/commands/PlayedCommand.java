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
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import com.helion3.oracle.Oracle;
import com.helion3.oracle.commandlibs.CallInfo;
import com.helion3.oracle.commandlibs.SubHandler;
import com.helion3.oracle.utils.Playtime;
import com.helion3.oracle.utils.PlaytimeUtil;

public class PlayedCommand implements SubHandler {

	/**
	 * Handle the command
	 */
	public void handle( final CallInfo call ){
		
		String username = call.getSender().getName();
		if( call.getArgs().length > 0 ){
			username = Oracle.expandName( call.getArg(0) );
		}
		
		final OfflinePlayer player = Bukkit.getOfflinePlayer(username);
		
		if( player == null ){
			call.getSender().sendMessage( Oracle.messenger.playerError( "Could not find a player by that name." ) );
			return;
		}
		
		// Check for alt accounts in async thread
    	new Thread(new Runnable(){
			public void run(){
			    
				Playtime playtime;
                try {
                    playtime = PlaytimeUtil.getPlaytime( player );
                    String msg = ChatColor.GOLD + player.getName() + " has played for " + playtime.getHours() + " hours, " + playtime.getMinutes() + " minutes, and " + playtime.getSeconds() + " seconds. Nice!";
                    call.getSender().sendMessage( Oracle.messenger.playerHeaderMsg( msg ) );
                } catch ( Exception e ) {
                    call.getSender().sendMessage( Oracle.messenger.playerError( e.getMessage() ) );
                }
			}
    	}).start();
	}
}