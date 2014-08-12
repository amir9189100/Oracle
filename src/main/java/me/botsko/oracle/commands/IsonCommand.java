package me.botsko.oracle.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.botsko.oracle.Oracle;
import me.botsko.oracle.commandlibs.CallInfo;
import me.botsko.oracle.commandlibs.SubHandler;

public class IsonCommand implements SubHandler {
	
	/**
	 * Handle the command
	 */
	public void handle(CallInfo call) {
		
		String username = null;
		if(call.getArgs().length > 0){
			// Expand partials
			String tmp = Oracle.expandName( call.getArg(0) );
			if(tmp != null){
	    		username = tmp;
	    	}
		} else {
			username = call.getPlayer().getName();
		}
		
		if(isOnline( username )){
			call.getSender().sendMessage( Oracle.messenger.playerHeaderMsg( username + " is online" ) ); 
		} else {
			call.getSender().sendMessage( Oracle.messenger.playerError( call.getArg(0) + " is not online" ) ); 
		}
	}
	
	/**
	 * 
	 * @param username
	 * @return
	 */
	public boolean isOnline( String username ){
		for(Player pl : Bukkit.getServer().getOnlinePlayers()){
			if(pl.getName().equals(username)){
				return true;
			}
		}
		return false;
	}
}