package me.botsko.oracle.commands;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import me.botsko.oracle.Oracle;
import me.botsko.oracle.commandlibs.CallInfo;
import me.botsko.oracle.commandlibs.SubHandler;
import me.botsko.oracle.utils.Playtime;
import me.botsko.oracle.utils.PlaytimeUtil;

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