package me.botsko.oracle.commands;

import me.botsko.oracle.Oracle;
import me.botsko.oracle.commandlibs.CallInfo;
import me.botsko.oracle.commandlibs.SubHandler;
import me.botsko.oracle.utils.BanUtil;

public class UnbanIpCommand implements SubHandler {
	
	/**
	 * Handle the command
	 */
	public void handle(CallInfo call) {
		
		if(call.getArgs().length <= 0){
			call.getSender().sendMessage( Oracle.messenger.playerError("You must provide an IP to unban.") );
			return;
		}

		// Save to db
		BanUtil.unbanByIp( call.getSender(), call.getArg(0) );
		
		// Success
		call.getSender().sendMessage( Oracle.messenger.playerHeaderMsg("IP has been unbanned.") );
    
	}
}