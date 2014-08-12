package me.botsko.oracle.utils;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import me.botsko.oracle.Oracle;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BungeeCord implements PluginMessageListener {
	
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
                    try {
                    	
                    	try {
        					BanUtil.ipMayJoin( ip );
        				} catch (Exception e){
        					player.kickPlayer( "Banned. " + e.getMessage() );
        					Oracle.log( "Rejecting player login due to ban. For: " + player.getName() );
        				}
                    	
						JoinUtil.setPlayerSessionIp( player, ip );
					} catch (Exception e) {
					}
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}