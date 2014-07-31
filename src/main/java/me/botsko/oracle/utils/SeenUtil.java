package me.botsko.oracle.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import org.bukkit.OfflinePlayer;

import me.botsko.oracle.Oracle;
import me.botsko.oracle.players.PlayerIdentification;
import me.botsko.oracle.players.PluginPlayer;

public class SeenUtil {
	
	
	/**
	 * 
	 * @param person
	 * @param account_name
	 * @throws ParseException 
	 */
	public static Date getPlayerFirstSeen( OfflinePlayer player ) throws ParseException{
		Date joined = null;
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			
			// Insert/Get Player ID
		    PluginPlayer pluginPlayer = PlayerIdentification.getOraclePlayer( player.getName() );
			
			conn = Oracle.dbc();
    		s = conn.prepareStatement ("SELECT player_join FROM oracle_joins WHERE player_id = ? ORDER BY player_join LIMIT 1;");
    		s.setInt(1, pluginPlayer.getId());
    		s.executeQuery();
    		rs = s.getResultSet();
    		
    		if(rs.first()){
	        	joined = new Date(rs.getLong("player_join") * 1000);
    		}
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	if(rs != null) try { rs.close(); } catch (SQLException e) {}
        	if(s != null) try { s.close(); } catch (SQLException e) {}
        	if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
		return joined;
	}
	
	
	/**
	 * 
	 * @param person
	 * @param account_name
	 * @throws ParseException 
	 */
	public static Date getPlayerLastSeen( OfflinePlayer player ) throws ParseException{
		Date seen = null;
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			
			// Insert/Get Player ID
		    PluginPlayer pluginPlayer = PlayerIdentification.getOraclePlayer( player.getName() );
						
			conn = Oracle.dbc();
    		s = conn.prepareStatement ("SELECT player_quit FROM oracle_joins j WHERE player_id = ? AND player_quit IS NOT NULL ORDER BY player_quit DESC LIMIT 1;");
    		s.setInt(1, pluginPlayer.getId());
    		s.executeQuery();
    		rs = s.getResultSet();
    		
    		if(rs.first()){
	        	seen = new Date(rs.getLong("player_quit") * 1000);
    		}
    		
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	if(rs != null) try { rs.close(); } catch (SQLException e) {}
        	if(s != null) try { s.close(); } catch (SQLException e) {}
        	if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
		return seen;
	}
}