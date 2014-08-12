package me.botsko.oracle.commands;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import me.botsko.oracle.Oracle;
import me.botsko.oracle.commandlibs.CallInfo;
import me.botsko.oracle.commandlibs.SubHandler;
import me.botsko.oracle.utils.Alt;
import me.botsko.oracle.utils.JoinUtil;

public class AltsCommand implements SubHandler {
	
	/**
	 * Handle the command
	 */
	public void handle( final CallInfo call ){
		
		// Check for alt accounts in async thread
    	new Thread(new Runnable(){
			public void run(){
		
				List<Alt> alt_accts;
				try {
					
					OfflinePlayer player = Bukkit.getOfflinePlayer( call.getArg(0) );
					alt_accts = JoinUtil.getPlayerAlts( player );
					
					if( alt_accts.isEmpty() ){
					    call.getSender().sendMessage( Oracle.messenger.playerError( "There are no known alts for that player" ) );
					}
					
					String alts_list = "";
					int i = 1;
					for(Alt alt : alt_accts){
						alts_list += alt.username + (i == alt_accts.size() ? "" : ", ");
						i++;
					}
					call.getSender().sendMessage( Oracle.messenger.playerMsg( player.getName() + "'s alts: " + alts_list) );
					
				} catch (Exception e){
				    e.printStackTrace();
				}
			}
    	}).start();
	}
}