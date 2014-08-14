package me.botsko.oracle.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.botsko.oracle.Oracle;
import me.botsko.oracle.players.PlayerIdentification;
import me.botsko.oracle.players.PluginPlayer;

public class BanUtil {
	
	/**
	 * 
	 * @param person
	 * @param account_name
	 * @throws Exception 
	 */
	public static void banByUsername( CommandSender staff, OfflinePlayer player, String reason ) throws Exception{
		Connection conn = null;
		PreparedStatement s = null;
		try {
			
			// Insert/Get Player ID
		    PluginPlayer pluginPlayer = PlayerIdentification.getOraclePlayer( player );
            if( pluginPlayer == null ){
                throw new Exception("That player has not played on this server.");
            }
	
            int staff_id = 0;
            if( staff instanceof Player ){
                PluginPlayer staffPluginPlayer = PlayerIdentification.cacheOraclePlayer( (Player)staff );
                staff_id = staffPluginPlayer.getId();
            }
			
			conn = Oracle.dbc();
	        s = conn.prepareStatement("INSERT INTO oracle_bans (player_id,staff_player_id,reason,epoch) VALUES (?,?,?,?)");
	        s.setInt(1, pluginPlayer.getId());
	        s.setInt(2, staff_id);
	        s.setString(3, reason);
	        s.setLong(4, System.currentTimeMillis() / 1000L);
	        s.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
        	if(s != null) try { s.close(); } catch (SQLException e) {}
        	if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
	}
	
	/**
	 * 
	 * @param person
	 * @param account_name
	 */
	public static void banByIp( CommandSender staff, String ip, String reason ){
		Connection conn = null;
		PreparedStatement s = null;
		try {
			
			// Insert/Get IP ID
			int ip_id = JoinUtil.lookupIp( ip );
			
			int staff_id = 0;
            if( staff instanceof Player ){
                PluginPlayer staffPluginPlayer = PlayerIdentification.cacheOraclePlayer( (Player)staff );
                staff_id = staffPluginPlayer.getId();
            }
			
			conn = Oracle.dbc();
	        s = conn.prepareStatement("INSERT INTO oracle_bans (ip_id,staff_player_id,reason,epoch) VALUES (?,?,?,?)");
	        s.setInt(1, ip_id);
	        s.setInt(2, staff_id);
	        s.setString(3, reason);
	        s.setLong(4, System.currentTimeMillis() / 1000L);
	        s.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
        	if(s != null) try { s.close(); } catch (SQLException e) {}
        	if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
	}
	
	/**
	 * 
	 * @param person
	 * @param account_name
	 * @throws Exception  
	 */
	public static void unbanByUsername( CommandSender staff, OfflinePlayer player ) throws Exception {
		
		if( player == null ){
			throw new IllegalArgumentException("Argument may not be null");
		}
		
		Connection conn = null;
		PreparedStatement s = null;
		try {
			conn = Oracle.dbc();
			
			// Insert/Get Player ID
	        PluginPlayer pluginPlayer = PlayerIdentification.getOraclePlayer( player );
            if( pluginPlayer == null ){
                throw new Exception("That player has not played on this server.");
            }
  
            int staff_id = 0;
            if( staff instanceof Player ){
                PluginPlayer staffPluginPlayer = PlayerIdentification.cacheOraclePlayer( (Player)staff );
                staff_id = staffPluginPlayer.getId();
            }
			
			// Add unban record
	        s = conn.prepareStatement("INSERT INTO oracle_unbans (player_id,staff_player_id,epoch) VALUES (?,?,?)");
	        s.setInt(1, pluginPlayer.getId());
	        s.setInt(2, staff_id);
	        s.setLong(3, System.currentTimeMillis() / 1000L);
	        s.executeUpdate();
	        
	        // Mark as unbanned
	        s = conn.prepareStatement("UPDATE oracle_bans SET unbanned = 1 WHERE player_id = ?");
	        s.setInt(1, pluginPlayer.getId());
	        s.executeUpdate();
	        
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
        	if(s != null) try { s.close(); } catch (SQLException e) {}
        	if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
	}
	
	/**
	 * 
	 * @param person
	 * @param account_name
	 */
	public static void unbanByIp( CommandSender staff, String ip ){
		
		Connection conn = null;
		PreparedStatement s = null;
		try {
			conn = Oracle.dbc();
			
			// Insert/Get Player ID
			int ip_id = JoinUtil.lookupIp( ip );
			
			int staff_id = 0;
            if( staff instanceof Player ){
                PluginPlayer staffPluginPlayer = PlayerIdentification.cacheOraclePlayer( (Player)staff );
                staff_id = staffPluginPlayer.getId();
            }
			
			// Add unban record
	        s = conn.prepareStatement("INSERT INTO oracle_unbans (ip_id,staff_player_id,epoch) VALUES (?,?,?)");
	        s.setInt(1, ip_id);
	        s.setInt(2, staff_id);
	        s.setLong(3, System.currentTimeMillis() / 1000L);
	        s.executeUpdate();
	        
	        // Mark as unbanned
	        s = conn.prepareStatement("UPDATE oracle_bans SET unbanned = 1 WHERE ip_id = ?");
	        s.setInt(1, ip_id);
	        s.executeUpdate();
	        
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
        	if(s != null) try { s.close(); } catch (SQLException e) {}
        	if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
	}
	
	/**
	 * 
	 * @param player
	 * @throws Exception
	 */
	public static void playerMayJoin( OfflinePlayer player ) throws Exception{
		playerMayJoin( player, null );
	}
	
	/**
	 * 
	 * @param username
	 * @throws Exception 
	 */
	public static void playerMayJoin( OfflinePlayer player, String ip ) throws Exception{
		
		if( player == null ){
			throw new IllegalArgumentException("Argument may not be null");
		}
		
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			
		    // Insert/Get Player ID
		    PluginPlayer pluginPlayer = PlayerIdentification.getOraclePlayer( player );

			// A player we've never seen doesn't need to be matched
			if( pluginPlayer != null ){
			
				// Insert/Get Player ID
				int ip_id = 0;
				if( ip != null ){
					ip_id = JoinUtil.lookupIp( ip );
				}
	
				conn = Oracle.dbc();
	    		s = conn.prepareStatement ("SELECT reason FROM oracle_bans WHERE ( player_id = ? OR ip_id = ? ) AND unbanned = 0 LIMIT 1");
	    		s.setInt(1, pluginPlayer.getId());
	    		s.setInt(2, ip_id);
	    		s.executeQuery();
	    		rs = s.getResultSet();
	    		
	    		if(rs.first()){
	    			throw new Exception( rs.getString("reason") );
	    		}
			}
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	if(rs != null) try { rs.close(); } catch (SQLException e) {}
        	if(s != null) try { s.close(); } catch (SQLException e) {}
        	if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
	}
	
	/**
	 * 
	 * @param username
	 * @throws Exception 
	 */
	public static void ipMayJoin( String ip ) throws Exception{
		
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			
			// Insert/Get Player ID
			int ip_id = JoinUtil.lookupIp( ip );

			conn = Oracle.dbc();
    		s = conn.prepareStatement ("SELECT reason FROM oracle_bans WHERE ip_id = ? AND unbanned = 0 LIMIT 1");
    		s.setInt(1, ip_id);
    		s.executeQuery();
    		rs = s.getResultSet();
    		
    		if(rs.first()){
    			throw new Exception( rs.getString("reason") );
    		}

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	if(rs != null) try { rs.close(); } catch (SQLException e) {}
        	if(s != null) try { s.close(); } catch (SQLException e) {}
        	if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
	}
}