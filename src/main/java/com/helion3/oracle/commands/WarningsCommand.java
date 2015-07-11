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

import java.sql.SQLException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.helion3.oracle.Oracle;
import com.helion3.oracle.commandlibs.CallInfo;
import com.helion3.oracle.commandlibs.SubHandler;
import com.helion3.oracle.utils.Warning;
import com.helion3.oracle.utils.WarningUtil;

public class WarningsCommand implements SubHandler {
	
	/**
	 * Handle the command
	 */
	public void handle(CallInfo call) {

		// If no username found, assume they mean themselves
		String user = "";
		if(call.getArgs().length == 0){
			if( call.getPlayer() != null ){
				user = call.getPlayer().getName();
			}
		} else {
			user = call.getArg(0);
		}

		if(!user.isEmpty()){
			try {
				listWarnings(user, call.getSender());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			call.getSender().sendMessage( Oracle.messenger.playerError("Player name must be specified.") );
		}
	}

	/**
	 * 
	 * @param username
	 * @param sender
	 * @throws SQLException
	 */
    public void listWarnings(String username, CommandSender sender) throws SQLException{
    	
    	OfflinePlayer warned_player = Bukkit.getOfflinePlayer( username );
    	if( warned_player == null ){
    		sender.sendMessage( Oracle.messenger.playerError( "Could not find a player by that name." ) );
			return;
		}
    	
    	sender.sendMessage( Oracle.messenger.playerHeaderMsg( "Warnings filed for " + username + ": " ) );
    	
    	// Pull all items matching this name
		List<Warning> warnings = WarningUtil.getPlayerWarnings( warned_player );
		if(!warnings.isEmpty()){
			for(Warning warn : warnings){
				sender.sendMessage( Oracle.messenger.playerMsg( "["+ warn.id + "] " + warn.dateFiled + ": " + ChatColor.RED + warn.reason + ChatColor.WHITE + "By: " + ChatColor.WHITE + warn.moderator) );
			}
		} else {
			sender.sendMessage( Oracle.messenger.playerError("No warnings filed.") );
		}
    }
}
