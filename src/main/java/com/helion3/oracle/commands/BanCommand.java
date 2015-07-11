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
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.helion3.oracle.Oracle;
import com.helion3.oracle.commandlibs.CallInfo;
import com.helion3.oracle.commandlibs.SubHandler;
import com.helion3.oracle.utils.BanUtil;

public class BanCommand implements SubHandler {
	
	/**
	 * Handle the command
	 */
	public void handle(CallInfo call) {
		
		if(call.getArgs().length <= 0){
			call.getSender().sendMessage( Oracle.messenger.playerError("You must provide a username to ban.") );
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
		
		// Who
		String username = call.getArg(0);
		OfflinePlayer player = Bukkit.getOfflinePlayer( username );
		
		// Is player online - kick them with ban reason
		Player bannedPlayer = Bukkit.getServer().getPlayer( username );
		if( bannedPlayer != null ){
			bannedPlayer.kickPlayer( "Banned: " + reason );
		}
	
		// Save to db
		try {
			BanUtil.banByUsername( call.getSender(), player, reason );
			for( Player p : Bukkit.getOnlinePlayers() ){
			    if( p.hasPermission( "oracle.bans.alert" ) ){
			        p.sendMessage( Oracle.messenger.playerHeaderMsg( call.getSender().getName() + " banned " + username + " for: " + reason ) );
			    }
			}
			if( !(call.getSender() instanceof Player) ){
			    call.getSender().sendMessage( Oracle.messenger.playerHeaderMsg( call.getSender().getName() + " banned " + username + " for: " + reason ) );
			}
		} catch (Exception e) {
			call.getSender().sendMessage( Oracle.messenger.playerError( e.getMessage() ) );
		}
	}
}