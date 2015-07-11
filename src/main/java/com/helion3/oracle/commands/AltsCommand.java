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

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.helion3.oracle.Oracle;
import com.helion3.oracle.commandlibs.CallInfo;
import com.helion3.oracle.commandlibs.SubHandler;
import com.helion3.oracle.utils.JoinUtil;

import me.botsko.elixr.TypeUtils;

public class AltsCommand implements SubHandler {
	
	/**
	 * Handle the command
	 */
	public void handle( final CallInfo call ){
		
		// Check for alt accounts in async thread
    	new Thread(new Runnable(){
			public void run(){
				try {
					
					OfflinePlayer player = Bukkit.getOfflinePlayer( call.getArg(0) );
					List<String> alts = JoinUtil.getPlayerAlts( player );
					
					if( alts.isEmpty() ){
					    call.getSender().sendMessage( Oracle.messenger.playerError( "There are no known alts for that player" ) );
					    return;
					}
					
					call.getSender().sendMessage( Oracle.messenger.playerMsg( player.getName() + "'s alts: " + TypeUtils.join( alts, ", " )) );
					
				} catch (Exception e){
				    e.printStackTrace();
				}
			}
    	}).start();
	}
}