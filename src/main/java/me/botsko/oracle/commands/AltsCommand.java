package me.botsko.oracle.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import me.botsko.elixr.TypeUtils;
import me.botsko.oracle.Oracle;
import me.botsko.oracle.commandlibs.CallInfo;
import me.botsko.oracle.commandlibs.SubHandler;
import me.botsko.oracle.utils.JoinUtil;

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