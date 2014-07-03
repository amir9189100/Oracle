package me.botsko.oracle.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.botsko.oracle.Oracle;
import me.botsko.oracle.commandlibs.CallInfo;
import me.botsko.oracle.commandlibs.SubHandler;
import me.botsko.oracle.utils.BanUtil;

public class BanIpCommand implements SubHandler {
	
	
	/**
	 * Handle the command
	 */
	public void handle(CallInfo call) {
		
		if(call.getArgs().length <= 0){
			call.getSender().sendMessage( Oracle.messenger.playerError("You must provide a player name or IP address to ban.") );
			return;
		}
		
		String ip = call.getArg(0);

		String ip_regex = "(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
		if( !ip.matches( ip_regex ) ){
		    
		    // Try to find player
		    Player player = Bukkit.getPlayer( ip );
		    
		    if( player == null ){
		        call.getSender().sendMessage( Oracle.messenger.playerError("No player online named " + ip) );
	            return;
		    }
		    
		    ip = player.getAddress().getAddress().toString().replace( "/", "" );
		    
		}

		if( ip.equals("127.0.0.1") ){
			call.getSender().sendMessage( Oracle.messenger.playerError("You may not ban a localhost IP.") );
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

		// Save to db
		BanUtil.banByIp( call.getSender(), ip, reason );
		
		// Kick all players with this IP
		for( Player p : Bukkit.getOnlinePlayers() ){
		    String p_ip = p.getAddress().getAddress().toString().replace( "/", "" );
		    if( p_ip.equals( ip ) ){
		        p.kickPlayer( reason );
		    }
		}
		
		// Tell peeps
		for( Player p : Bukkit.getOnlinePlayers() ){
            if( p.hasPermission( "oracle.bans.alert" ) ){
                p.sendMessage( Oracle.messenger.playerHeaderMsg( call.getSender().getName() + " banned IP " + ip + " for: " + reason ) );
            }
        }
    
	}
}