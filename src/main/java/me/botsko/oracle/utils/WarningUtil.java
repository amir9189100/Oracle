package me.botsko.oracle.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import me.botsko.oracle.Oracle;
import me.botsko.oracle.players.PlayerIdentification;
import me.botsko.oracle.players.PluginPlayer;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WarningUtil {
	
	/**
	 * Alert staff to a player with three or more warnings
	 * @param plugin
	 * @param username
	 */
	public static void alertStaffOnWarnLimit( OfflinePlayer player ){
        List<Warning> warnings = WarningUtil.getPlayerWarnings( player );
        if(warnings.size() >= 3){
        	for(Player pl: Bukkit.getServer().getOnlinePlayers()) {
        		if(pl.hasPermission("oracle.warn")){
        			pl.sendMessage( Oracle.messenger.playerMsg(player.getName() + " now has three warnings.") );
        		}
        	}
        }
	}
	
	/**
	 * 
	 * @param person
	 * @param account_name
	 * @throws Exception 
	 */
	public static void fileWarning( OfflinePlayer player, String reason, CommandSender staff ) throws Exception{
		Connection conn = null;
		PreparedStatement s = null;
		try {
			
		    // Insert/Get Player ID
		    PluginPlayer pluginPlayer = PlayerIdentification.getOraclePlayer( player.getName() );
		    if( pluginPlayer == null ){
		        throw new Exception("That player has not played on this server.");
		    }

		    int staff_id = 0;
            if( staff instanceof Player ){
                PluginPlayer staffPluginPlayer = PlayerIdentification.cacheOraclePlayer( (Player)staff );
                staff_id = staffPluginPlayer.getId();
            }
			
			conn = Oracle.dbc();

	        s = conn.prepareStatement("INSERT INTO oracle_warnings (player_id,reason,epoch,staff_player_id) VALUES (?,?,?,?)");
	        s.setInt(1, pluginPlayer.getId());
	        s.setString(2, reason);
	        s.setLong(3, System.currentTimeMillis() / 1000L);
	        s.setInt(4, staff_id);
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
	public static List<Warning> getPlayerWarnings( OfflinePlayer player ){
		ArrayList<Warning> warnings = new ArrayList<Warning>();
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			
			conn = Oracle.dbc();
    		s = conn.prepareStatement ("SELECT warning_id, epoch, reason, p.player, s.player as staff FROM oracle_warnings w " +
    				"LEFT JOIN oracle_players p ON p.player_id = w.player_id " + 
    				"LEFT JOIN oracle_players s ON s.player_id = w.staff_player_id " + 
    				"WHERE p.player = ? AND deleted = 0");
    		s.setString(1, player.getName());
    		s.executeQuery();
    		rs = s.getResultSet();

    		while(rs.next()){
    			warnings.add( new Warning(rs.getInt("warning_id"), rs.getLong("epoch"), rs.getString("player"), rs.getString("reason"), rs.getString("staff")) );
			}
            
		} catch (SQLException e){
            e.printStackTrace();
        } finally {
        	if(rs != null) try { rs.close(); } catch (SQLException e) {}
        	if(s != null) try { s.close(); } catch (SQLException e) {}
        	if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
		return warnings;
	}
	
	/**
	 * 
	 * @param person
	 * @param account_name
	 */
	public static void deleteWarning( int id ){
		Connection conn = null;
		PreparedStatement s = null;
		try {
			
			conn = Oracle.dbc();
	        s = conn.prepareStatement("UPDATE oracle_warnings SET deleted = 1 WHERE warning_id = ?");
	        s.setInt(1, id);
	        s.executeUpdate();
     
		} catch (SQLException e){
            e.printStackTrace();
        } finally {
        	if(s != null) try { s.close(); } catch (SQLException e) {}
        	if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
	}
}