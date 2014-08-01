package me.botsko.oracle.tasks;

import me.botsko.oracle.Oracle;
import me.botsko.oracle.events.OraclePlaytimeMilestoneEvent;
import me.botsko.oracle.utils.Playtime;
import me.botsko.oracle.utils.PlaytimeUtil;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlaytimeMonitor implements Runnable {

	
	/**
	 * 
	 */
	public void run() {

		for( Player p : Bukkit.getServer().getOnlinePlayers() ){

			Playtime playtime;
            try {
                
                playtime = PlaytimeUtil.getPlaytime(p);
                
                if( !Oracle.playtimeHours.containsKey(p) ) continue;
                
                int lastHourCount = Oracle.playtimeHours.get(p);

                if( playtime.getHours() > lastHourCount ){
                    
                    Oracle.playtimeHours.put( p, playtime.getHours() );
                    
                    Oracle.log("Throwing playtime hour increase event for " + p.getName());
                    
                    // Throw event as this is a new player
                    OraclePlaytimeMilestoneEvent event = new OraclePlaytimeMilestoneEvent( p, playtime.getHours() );
                    Bukkit.getServer().getPluginManager().callEvent( event );
                    
                }
                
            } catch ( Exception e ) {
                e.printStackTrace();
                continue;
            }
			
		}
	}
}