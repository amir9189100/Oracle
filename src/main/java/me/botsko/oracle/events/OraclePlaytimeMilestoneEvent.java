package me.botsko.oracle.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
 
public class OraclePlaytimeMilestoneEvent extends Event {
	
    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final int newHourCount;

    /**
     * 
     * @param plugin
     * @param action_type_name
     * @param player
     * @param message
     */
    public OraclePlaytimeMilestoneEvent( Player player, int newHourCount ){
    	this.player = player;
    	this.newHourCount = newHourCount;
    }
    
	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * 
	 * @return
	 */
	public int getNewHourCount(){
		return newHourCount;
	}

	/**
     * Required by bukkit for proper event handling.
     */
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Required by bukkit for proper event handling.
     * @return
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}