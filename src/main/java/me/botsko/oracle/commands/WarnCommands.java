package me.botsko.oracle.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import me.botsko.oracle.Oracle;
import me.botsko.oracle.commandlibs.CallInfo;
import me.botsko.oracle.commandlibs.Executor;
import me.botsko.oracle.commandlibs.SubHandler;
import me.botsko.oracle.utils.WarningUtil;

public class WarnCommands extends Executor {
	
	/**
	 * 
	 * @param prism
	 */
	public WarnCommands(Oracle oracle) {
		super( oracle, "command", "warn" );
		setupCommands();
	}
	

	/**
	 * 
	 */
	private void setupCommands() {
		
		
		/**
		 * /warn [username] [msg]
		 */
		addSub( new String[]{"default"}, "oracle.warn")
		.allowConsole()
		.setMinArgs(1)
		.setHandler(new SubHandler() {
            public void handle(CallInfo call) {
            	
            	if(call.getArgs().length >= 2){

            		if( call.getArg(0).equals("delete") ){
        				WarningUtil.deleteWarning( Integer.parseInt( call.getArg(1) ) );
        				call.getSender().sendMessage( Oracle.messenger.playerMsg("Warning deleted successfully."));
        				return;
            		}
					
					String reason = "";
					for (int i = 1; i < call.getArgs().length; i = i + 1){
						reason += call.getArgs()[i]+" ";
					}
					
					// Find the player whether online or not
					OfflinePlayer warned_player = Bukkit.getPlayer( call.getArg(0) );
					if( warned_player == null ){
						warned_player = Bukkit.getOfflinePlayer( call.getArg(0) );
					}
					
					if( warned_player == null ){
						call.getSender().sendMessage( Oracle.messenger.playerError( "Could not find a player by that name." ) );
						return;
					}

					// File warning
					WarningUtil.fileWarning( warned_player, reason, call.getSender() );
					
					// Alert them
					if( warned_player instanceof Player ){
						Player pl = (Player) warned_player;
						pl.sendMessage( Oracle.messenger.playerError("=== OFFICIAL WARNING FILED FOR YOU ===") );
						pl.sendMessage( Oracle.messenger.playerMsg(reason) );
						pl.sendMessage( Oracle.messenger.playerError("Three warnings will result in a ban!") );
					}

					call.getSender().sendMessage( Oracle.messenger.playerMsg("Warning file successfully."));

					// This may be a third warning!
					WarningUtil.alertStaffOnWarnLimit( warned_player );

				}       	
            }
		});
	}
}