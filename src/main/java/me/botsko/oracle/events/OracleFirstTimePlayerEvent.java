package me.botsko.oracle.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
 
public class OracleFirstTimePlayerEvent extends Event {
	
    private static final HandlerList handlers = new HandlerList();
    private final Player player;

    /**
     * 
     * @param plugin
     * @param action_type_name
     * @param player
     * @param message
     */
    public OracleFirstTimePlayerEvent( Player player ){
    	this.player = player;
    }

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
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