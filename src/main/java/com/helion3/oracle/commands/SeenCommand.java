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

import com.helion3.oracle.Oracle;
import com.helion3.oracle.commandlibs.CallInfo;
import com.helion3.oracle.commandlibs.SubHandler;
import com.helion3.oracle.utils.SeenUtil;

public class SeenCommand implements SubHandler {

	/**
	 * Handle the command
	 */
	public void handle( final CallInfo call ){
		
		String username = null;
		if(call.getArgs().length > 0){
			// Expand partials
			String tmp = Oracle.expandName( call.getArg(0) );
			if(tmp != null){
	    		username = tmp;
	    	}
		} else {
			username = call.getSender().getName();
		}
		
		final OfflinePlayer player = Bukkit.getOfflinePlayer(username);
		
		if( player == null ){
			call.getSender().sendMessage( Oracle.messenger.playerError( "Could not find a player by that name." ) );
			return;
		}
		
		// Check for alt accounts in async thread
    	new Thread(new Runnable(){
			public void run(){
				call.getSender().sendMessage( Oracle.messenger.playerHeaderMsg( player.getName() ) );
				try {
					call.getSender().sendMessage( Oracle.messenger.playerMsg("Joined " + SeenUtil.getPlayerFirstSeen(player)) );
					call.getSender().sendMessage( Oracle.messenger.playerMsg("Last Seen " + SeenUtil.getPlayerLastSeen(player)) );
				} catch (Exception e){
				    call.getSender().sendMessage( Oracle.messenger.playerError( e.getMessage() ) );
				}
			}
    	}).start();
	}
}