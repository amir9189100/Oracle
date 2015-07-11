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

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.helion3.oracle.Oracle;
import com.helion3.oracle.commandlibs.CallInfo;
import com.helion3.oracle.commandlibs.SubHandler;
import com.helion3.oracle.utils.BanUtil;

public class BanIpCommand implements SubHandler {
	
	/**
	 * Handle the command
	 */
	public void handle(CallInfo call) {
		
		if(call.getArgs().length <= 0){
			call.getSender().sendMessage( Oracle.messenger.playerError("You must provide a player name or IP address to ban.") );
			return;
		}
		
		String ip = call.getArg(0);

		String ip_regex = "(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
		if( !ip.matches( ip_regex ) ){
		    
		    // Try to find player
		    Player player = Bukkit.getPlayer( ip );
		    
		    if( player == null ){
		        call.getSender().sendMessage( Oracle.messenger.playerError("No player online named " + ip) );
	            return;
		    }
		    
		    ip = player.getAddress().getAddress().toString().replace( "/", "" );
		    
		}

		if( ip.equals("127.0.0.1") ){
			call.getSender().sendMessage( Oracle.messenger.playerError("You may not ban a localhost IP.") );
			return;
		}
		
		String reason = "You're banned. No reason provided.";
		if(call.getArgs().length > 1){
			String[] messageArgs = new String[(call.getArgs().length - 1)];
			for(int i = 1; i < call.getArgs().length; i++ ){
				messageArgs[ (i-1) ] = call.getArgs()[i];
			}
			reason = StringUtils.join( messageArgs, " ");
		}

		// Save to db
		BanUtil.banByIp( call.getSender(), ip, reason );
		
		// Kick all players with this IP
		System.out.println("Kicking all players with IP: " + ip);
		for( Player p : Bukkit.getOnlinePlayers() ){
		    String p_ip = p.getAddress().getAddress().toString().replace( "/", "" );
		    System.out.println("Player IP: " + p_ip);
		    if( p_ip.equals( ip ) ){
		        p.kickPlayer( reason );
		    }
		}
		
		// Tell peeps
		for( Player p : Bukkit.getOnlinePlayers() ){
            if( p.hasPermission( "oracle.bans.alert" ) ){
                p.sendMessage( Oracle.messenger.playerHeaderMsg( call.getSender().getName() + " banned IP " + ip + " for: " + reason ) );
            }
        }
	}
}