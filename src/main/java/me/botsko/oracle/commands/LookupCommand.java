package me.botsko.oracle.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import me.botsko.oracle.Oracle;
import me.botsko.oracle.commandlibs.CallInfo;
import me.botsko.oracle.commandlibs.SubHandler;
import me.botsko.oracle.utils.BanUtil;

public class LookupCommand implements SubHandler {
	
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

				try {
					BanUtil.playerMayJoin( player );
					call.getSender().sendMessage(Oracle.messenger.playerHeaderMsg( call.getArg(0) + " is not banned." ));
				} catch ( Exception e ){
					call.getSender().sendMessage(Oracle.messenger.playerHeaderMsg( call.getArg(0) + " is banned. Reason: " + e.getMessage() + "."));
				}
			}
    	}).start();
	}
}