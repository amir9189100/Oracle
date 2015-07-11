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
import java.util.Date;

import org.spongepowered.api.entity.player.User;

import com.helion3.oracle.Oracle;
import com.helion3.oracle.players.PlayerIdentification;
import com.helion3.oracle.players.PluginPlayer;

public class SeenUtil {

	/**
	 *
	 * @param person
	 * @param account_name
	 * @throws Exception
	 */
	public static Date getPlayerFirstSeen(User player) throws Exception{
		Date joined = null;
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {

			// Insert/Get Player ID
		    PluginPlayer pluginPlayer = PlayerIdentification.getOraclePlayer( player.getName() );
		    if( pluginPlayer == null ){
		        throw new Exception("Player has never played on this server.");
		    }

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
     * @throws Exception
     */
    public static Date getPlayerLastJoin(User player) throws Exception{
        Date seen = null;
        Connection conn = null;
        PreparedStatement s = null;
        ResultSet rs = null;
        try {

            // Insert/Get Player ID
            PluginPlayer pluginPlayer = PlayerIdentification.getOraclePlayer( player.getName() );
            if( pluginPlayer == null ){
                throw new Exception("Player has never played on this server.");
            }

            conn = Oracle.dbc();
            s = conn.prepareStatement ("SELECT player_join FROM oracle_joins j WHERE player_id = ? ORDER BY player_join DESC LIMIT 1;");
            s.setInt(1, pluginPlayer.getId());
            s.executeQuery();
            rs = s.getResultSet();

            if(rs.first()){
                seen = new Date(rs.getLong("player_join") * 1000);
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

	/**
	 *
	 * @param person
	 * @param account_name
	 * @throws Exception
	 */
	public static Date getPlayerLastSeen(User player) throws Exception{
		Date seen = null;
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {

			// Insert/Get Player ID
		    PluginPlayer pluginPlayer = PlayerIdentification.getOraclePlayer( player.getName() );
		    if( pluginPlayer == null ){
                throw new Exception("Player has never played on this server.");
            }

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