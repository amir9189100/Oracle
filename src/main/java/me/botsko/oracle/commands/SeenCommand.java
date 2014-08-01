package me.botsko.oracle.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

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
	public void handle( final CallInfo call ){
		
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
		
		final OfflinePlayer player = Bukkit.getOfflinePlayer(username);
		
		if( player == null ){
			call.getSender().sendMessage( Oracle.messenger.playerError( "Could not find a player by that name." ) );
			return;
		}
		
		// Check for alt accounts in async thread
    	plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable(){
			public void run(){
		
				call.getPlayer().sendMessage( Oracle.messenger.playerHeaderMsg( "Join & Last Seen Dates for " + player.getName() ) );
				try {
					call.getSender().sendMessage( Oracle.messenger.playerMsg("Joined " + SeenUtil.getPlayerFirstSeen(player)) );
					call.getSender().sendMessage( Oracle.messenger.playerMsg("Last Seen " + SeenUtil.getPlayerLastSeen(player)) );
				} catch (Exception e){
				    call.getPlayer().sendMessage( Oracle.messenger.playerError( e.getMessage() ) );
				}
			}
    	});
	}
}