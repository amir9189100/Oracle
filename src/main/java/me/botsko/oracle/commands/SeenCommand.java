package me.botsko.oracle.commands;

import java.text.ParseException;

import me.botsko.oracle.Oracle;
import me.botsko.oracle.commandlibs.CallInfo;
import me.botsko.oracle.commandlibs.SubHandler;
import me.botsko.oracle.utils.SeenUtil;

public class SeenCommand implements SubHandler {
	
	/**
	 * 
	 */
	private Oracle plugin;
	
	
	/**
	 * 
	 * @param plugin
	 * @return 
	 */
	public SeenCommand(Oracle plugin) {
		this.plugin = plugin;
	}
	
	
	/**
	 * Handle the command
	 */
	public void handle(CallInfo call) {
		
		String username = null;
		if(call.getArgs().length > 0){
			// Expand partials
			String tmp = plugin.expandName( call.getArg(0) );
			if(tmp != null){
	    		username = tmp;
	    	}
		} else {
			username = call.getPlayer().getName();
		}
		
		call.getPlayer().sendMessage( plugin.messenger.playerHeaderMsg( "Join & Last Seen Dates for " + username ) );
		try {
			call.getSender().sendMessage( plugin.messenger.playerMsg("Joined " + SeenUtil.getPlayerFirstSeen(username)) );
			call.getSender().sendMessage( plugin.messenger.playerMsg("Last Seen " + SeenUtil.getPlayerLastSeen(username)) );
		} catch (ParseException e){
		}
	}
}