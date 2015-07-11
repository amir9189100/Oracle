/**
 * This file is part of Oracle, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Helion3 http://helion3.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.helion3.oracle.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.entity.player.User;

import com.helion3.oracle.Oracle;
import com.helion3.oracle.events.OracleFirstTimePlayerEvent;
import com.helion3.oracle.players.PlayerIdentification;
import com.helion3.oracle.players.PluginPlayer;

public class JoinUtil {

	/**
	 *
	 */
	protected static int lookupIp(String ip) {

		Connection conn = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {

			conn = Oracle.dbc();
    		s = conn.prepareStatement( "SELECT ip_id FROM oracle_ips WHERE ip = INET_ATON(?)" );
    		s.setString(1, ip);
    		rs = s.executeQuery();

    		if( rs.next() ){
    			return rs.getInt("ip_id");
    		} else {
    			return registerIp( ip );
    		}
		} catch (SQLException e){
		    e.printStackTrace();
        } finally {
        	if(rs != null) try { rs.close(); } catch (SQLException e) {}
        	if(s != null) try { s.close(); } catch (SQLException e) {}
        	if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
		return 0;
	}

	/**
	 * Saves a player name to the database, and adds the id to the cache hashmap
	 */
	protected static int registerIp(String ip) {

		Connection conn = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {

			conn = Oracle.dbc();
            s = conn.prepareStatement( "INSERT INTO oracle_ips (ip) VALUES (INET_ATON(?))" , Statement.RETURN_GENERATED_KEYS);
            s.setString(1, ip);
            s.executeUpdate();

            rs = s.getGeneratedKeys();
            if (rs.next()) {
            	return rs.getInt(1);
            } else {
                throw new SQLException("Insert statement failed - no generated key obtained.");
            }
		} catch (SQLException e) {
		    e.printStackTrace();
        } finally {
        	if(rs != null) try { rs.close(); } catch (SQLException e) {}
        	if(s != null) try { s.close(); } catch (SQLException e) {}
        	if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
		return 0;
	}

	/**
	 * Creates a join record for this player, and stores the player username/ip
	 * to the appropriate tables.
	 * @param person
	 * @param account_name
	 */
	public static void registerPlayerJoin(Player player, int online_count) {
		Connection conn = null;
		PreparedStatement s = null;
		try {

			final String ip = player.getAddress().getAddress().getHostAddress().toString();

			// Insert/Get IP ID
			int ip_id = lookupIp( ip );

			// Insert/Get Player ID
			PluginPlayer pluginPlayer = PlayerIdentification.cacheOraclePlayer( player );
			if( pluginPlayer == null ){

				// Throw event as this is a new player
				OracleFirstTimePlayerEvent event = new OracleFirstTimePlayerEvent( player );
				Bukkit.getServer().getPluginManager().callEvent( event );

				// Create a new player!
				pluginPlayer = PlayerIdentification.addPlayer( player );

			}

			conn = Oracle.dbc();
	        s = conn.prepareStatement("INSERT INTO oracle_joins (server_id,player_count,player_id,player_join,ip_id) VALUES (?,?,?,?,?)");
	        s.setInt(1, ServerUtil.lookupServer());
	        s.setInt(2, online_count);
	        s.setInt(3, pluginPlayer.getId());
	        s.setLong(4, System.currentTimeMillis() / 1000L);
	        s.setInt(5, ip_id);
	        s.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
        	if(s != null) try { s.close(); } catch (SQLException e) {}
        	if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
	}

	/**
	 * Updates a current play session IP if received separately from the join event.
	 * (Primarily for BungeeCord use)
	 * @param person
	 * @param account_name
	 * @throws Exception
	 */
	public static void setPlayerSessionIp(Player player, String ip) throws Exception {
		Connection conn = null;
		PreparedStatement s = null;
		try {

			// Insert/Get IP ID
			int ip_id = lookupIp( ip );

			// Insert/Get Player ID
			PluginPlayer pluginPlayer = PlayerIdentification.cacheOraclePlayer(player);
			if( pluginPlayer == null ){
				throw new Exception("Could not find player");
			}

			conn = Oracle.dbc();
	        s = conn.prepareStatement("UPDATE oracle_joins SET ip_id = ? WHERE player_quit IS NULL AND player_id = ?");
	        s.setInt(1, ip_id);
	        s.setInt(2, pluginPlayer.getId());
	        s.executeUpdate();
        } catch (SQLException e){
            e.printStackTrace();
        } finally {
        	if(s != null) try { s.close(); } catch (SQLException e) {}
        	if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
	}

	/**
	 * End a play session for the player
	 * @param person
	 * @param account_name
	 * @throws Exception
	 */
	public static void registerPlayerQuit(Player player) throws Exception{
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet trs = null;
		PreparedStatement pstmt1 = null;
		try {

			// Insert/Get Player ID
		    PluginPlayer pluginPlayer = PlayerIdentification.cacheOraclePlayer(player);
            if( pluginPlayer == null ){
                throw new Exception("Could not find player");
            }

			conn = Oracle.dbc();

			// Set the quit date for the players join session
			pstmt = conn.prepareStatement("UPDATE oracle_joins SET player_quit = ? WHERE player_quit IS NULL AND player_id = ?");
			pstmt.setLong(1, System.currentTimeMillis() / 1000L);
			pstmt.setInt(2, pluginPlayer.getId());
			pstmt.executeUpdate();

			// Update playtime
			pstmt = conn.prepareStatement("UPDATE oracle_joins SET playtime = (player_quit - player_join) WHERE player_id = ? AND playtime IS NULL");
			pstmt.setInt(1, pluginPlayer.getId());
			pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	if(pstmt1 != null) try { pstmt1.close(); } catch (SQLException e) {}
        	if(trs != null) try { trs.close(); } catch (SQLException e) {}
        	if(pstmt != null) try { pstmt.close(); } catch (SQLException e) {}
        	if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }

		// Remove id from cache
		if( Oracle.oraclePlayers.containsKey(player) ){
			Oracle.oraclePlayers.remove(player);
		}
	}

	/**
	 *
	 * @param person
	 * @param account_name
	 */
	public static void forceDateForOfflinePlayers(String users){
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet trs = null;
		try {

			conn = Oracle.dbc();

			// Ensure we ignore online players
			if(!users.isEmpty()){
				users = " AND player_id NOT IN ("+users+")";
			}

			// Log as having quit
	        s = conn.prepareStatement( "UPDATE oracle_joins SET player_quit = ? WHERE server_id = ? AND player_quit IS NULL"+users );
	        s.setLong(1, System.currentTimeMillis() / 1000L);
	        s.setInt(2, ServerUtil.lookupServer());
    		s.executeUpdate();

    		// Update playtime
			s = conn.prepareStatement("UPDATE oracle_joins SET playtime = (player_quit - player_join) WHERE server_id = ? AND player_join < player_quit AND playtime IS NULL AND player_quit IS NOT NULL"+users);
			s.setInt(1, ServerUtil.lookupServer());
			s.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	if(trs != null) try { trs.close(); } catch (SQLException e) {}
        	if(s != null) try { s.close(); } catch (SQLException e) {}
        	if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
	}

	/**
	 *
	 * @param person
	 * @param account_name
	 */
	public static void forceDateForAllPlayers(){
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet trs = null;
		try {

			conn = Oracle.dbc();

			// Log as having quit
	        s = conn.prepareStatement( "UPDATE oracle_joins SET player_quit = ? WHERE server_id = ? AND player_quit IS NULL" );
	        s.setLong(1, System.currentTimeMillis() / 1000L);
	        s.setInt(2, ServerUtil.lookupServer());
    		s.executeUpdate();

    		// Update playtime
			s = conn.prepareStatement("UPDATE oracle_joins SET playtime = (player_quit - player_join) WHERE server_id = ? AND player_join < player_quit AND playtime IS NULL AND player_quit IS NOT NULL");
			s.setInt(1, ServerUtil.lookupServer());
			s.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	if(trs != null) try { trs.close(); } catch (SQLException e) {}
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
	public static List<String> getPlayerAlts(User player) throws Exception{
		List<String> accounts = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {

			// Insert/Get Player ID
		    PluginPlayer pluginPlayer = PlayerIdentification.getOraclePlayer( player.getName() );
            if( pluginPlayer == null ){
                throw new Exception("Could not find player");
            }

			conn = Oracle.dbc();

			// Pull a list of all unique IPs this player has used
    		s = conn.prepareStatement (""
    		        + "SELECT DISTINCT p.player FROM oracle_joins j "
    		        + "JOIN "
    		            + "(SELECT DISTINCT i.ip_id FROM oracle_joins j "
    		            + "LEFT JOIN oracle_ips i ON i.ip_id = j.ip_id "
    		            + "WHERE j.player_id = ? "
    		            + "AND j.ip_id IS NOT NULL "
    		            + "AND i.ip_id IS NOT NULL) "
    		            + "A ON A.ip_id = j.ip_id "
	                + "LEFT JOIN oracle_players p ON p.player_id = j.player_id "
	                + "LEFT JOIN oracle_ips i ON j.ip_id = i.ip_id "
	                + "WHERE A.ip_id IS NOT NULL "
	                + "AND p.player_id != ? "
	                + "ORDER BY p.player ");
    		s.setInt(1, pluginPlayer.getId());
    		s.setInt(2, pluginPlayer.getId());
    		s.executeQuery();
    		rs = s.getResultSet();

    		while(rs.next()){
	    		accounts.add( rs.getString("player") );
    		}
		} catch (SQLException e) {
            e.printStackTrace();
        } finally {
        	if(rs != null) try { rs.close(); } catch (SQLException e) {}
        	if(s != null) try { s.close(); } catch (SQLException e) {}
        	if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
		return accounts;
	}
}