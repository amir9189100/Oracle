package me.botsko.oracle.utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import me.botsko.oracle.Oracle;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BungeeCord implements PluginMessageListener {
	
	
	Oracle plugin;
	
	
	/**
	 * 
	 * @param plugin
	 */
	public BungeeCord( Oracle plugin ){
		this.plugin = plugin;
	}

	
	/**
	 * 
	 */
    public void onPluginMessageReceived( String channel, Player player, byte[] message ) {
 
        if (channel.equals("BungeeCord")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
            try {
 
                String packetType = in.readUTF();
 
                if ( packetType.equals("IP") && player.isOnline() ) {
                    String ip = in.readUTF();
                    String username = player.getName();
                    JoinUtil.registerPlayerJoin( username, ip, plugin.getServer().getOnlinePlayers().length );
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}