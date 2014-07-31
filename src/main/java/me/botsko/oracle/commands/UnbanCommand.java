package me.botsko.oracle.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import me.botsko.oracle.Oracle;
import me.botsko.oracle.commandlibs.CallInfo;
import me.botsko.oracle.commandlibs.SubHandler;
import me.botsko.oracle.utils.BanUtil;

public class UnbanCommand implements SubHandler {
	
	
	/**
	 * Handle the command
	 */
	public void handle(CallInfo call) {
		
		if(call.getArgs().length <= 0){
			call.getSender().sendMessage( Oracle.messenger.playerError("You must provide a username to unban.") );
			return;
		}
		
		// Who
		OfflinePlayer player = Bukkit.getOfflinePlayer( call.getArg(0) );

		// Save to db
		try {
            BanUtil.unbanByUsername( call.getSender(), player );
        } catch ( Exception e ) {
            call.getSender().sendMessage( Oracle.messenger.playerError( e.getMessage() ) );
            return;
        }
		
		// Success
		call.getSender().sendMessage( Oracle.messenger.playerHeaderMsg("Player has been unbanned.") );
    
	}
}