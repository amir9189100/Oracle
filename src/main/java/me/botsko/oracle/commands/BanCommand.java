package me.botsko.oracle.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.botsko.oracle.Oracle;
import me.botsko.oracle.commandlibs.CallInfo;
import me.botsko.oracle.commandlibs.SubHandler;
import me.botsko.oracle.utils.BanUtil;

public class BanCommand implements SubHandler {
	
	/**
	 * Handle the command
	 */
	public void handle(CallInfo call) {
		
		if(call.getArgs().length <= 0){
			call.getSender().sendMessage( Oracle.messenger.playerError("You must provide a username to ban.") );
			return;
		}
		
		String reason = "You're banned. No reason provided.";
		if(call.getArgs().length > 1){
			String[] messageArgs = new String[(call.getArgs().length - 1)];
			for(int i = 1; i < call.getArgs().length; i++ ){
				messageArgs[ (i-1) ] = call.getArgs()[i];
			}
			reason = StringUtils.join( messageArgs, " ");
		}
		
		// Who
		String username = call.getArg(0);
		OfflinePlayer player = Bukkit.getOfflinePlayer( username );
		
		// Is player online - kick them with ban reason
		Player bannedPlayer = Bukkit.getServer().getPlayer( username );
		if( bannedPlayer != null ){
			bannedPlayer.kickPlayer( "Banned: " + reason );
		}
	
		// Save to db
		try {
			BanUtil.banByUsername( call.getSender(), player, reason );
			for( Player p : Bukkit.getOnlinePlayers() ){
			    if( p.hasPermission( "oracle.bans.alert" ) ){
			        p.sendMessage( Oracle.messenger.playerHeaderMsg( call.getSender().getName() + " banned " + username + " for: " + reason ) );
			    }
			}
		} catch (Exception e) {
			call.getSender().sendMessage( Oracle.messenger.playerError( e.getMessage() ) );
		}
	}
}