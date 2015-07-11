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

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import com.helion3.oracle.Oracle;
import com.helion3.oracle.commandlibs.CallInfo;
import com.helion3.oracle.commandlibs.SubHandler;
import com.helion3.oracle.utils.Playtime;
import com.helion3.oracle.utils.PlaytimeUtil;

public class PlayhistCommand implements SubHandler {
	
	/**
	 * Handle the command
	 */
	public void handle(CallInfo call){
	    
	    OfflinePlayer player = Bukkit.getOfflinePlayer( call.getArg(1) );
	    if( player == null ){
	        call.getSender().sendMessage( Oracle.messenger.playerError( "Cannot find player with that name." ) );
	        return;
	    }
		
		call.getSender().sendMessage( Oracle.messenger.playerMsg( "Most recent 7 days of playtime for " + call.getArg(1) + ": " ) );
    	
        try {
            
            LinkedHashMap<Playtime, String> playdates = PlaytimeUtil.getPlayerPlaytimeHistory( player );
            
            for( Entry<Playtime,String> entry : playdates.entrySet() ){
                Playtime pt = entry.getKey();
                String playdate = entry.getValue();
                call.getSender().sendMessage( Oracle.messenger.playerMsg( playdate + ": " + pt.getHours() + "hrs, " + pt.getMinutes() + " mins"  ) );
            }
        } catch ( Exception e ) {
            call.getSender().sendMessage( Oracle.messenger.playerError( e.getMessage() ) );
            e.printStackTrace();
        }
	}
}