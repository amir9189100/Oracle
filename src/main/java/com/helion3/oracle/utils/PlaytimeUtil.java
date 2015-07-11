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
import java.text.ParseException;
import java.util.Date;
import java.util.LinkedHashMap;

import org.spongepowered.api.entity.player.User;

import com.helion3.oracle.Oracle;
import com.helion3.oracle.players.PlayerIdentification;
import com.helion3.oracle.players.PluginPlayer;

public class PlaytimeUtil {

	/**
	 *
	 * @param person
	 * @param account_name
	 * @throws Exception
	 * @throws ParseException
	 */
	public static Playtime getPlaytime(User player) throws Exception {
		Playtime playtime = null;
		Connection conn = null;
		PreparedStatement s = null;
		try {

			// Insert/Get Player ID
		    PluginPlayer pluginPlayer = PlayerIdentification.getOraclePlayer( player.getName() );
		    if( pluginPlayer == null ){
		        throw new Exception("Player has never played on this server.");
		    }

			conn = Oracle.dbc();

			s = conn.prepareStatement ("SELECT SUM(playtime) as playtime FROM oracle_joins WHERE player_id = ?");
			s.setInt(1, pluginPlayer.getId());
			s.executeQuery();
			ResultSet rs = s.getResultSet();

			rs.first();
			int before_current = rs.getInt(1);

			// We also need to pull any incomplete join and calc up-to-the-minute playtime
			s = conn.prepareStatement ("SELECT player_join FROM oracle_joins WHERE player_id = ? AND player_quit IS NULL");
			s.setInt(1, pluginPlayer.getId());
			s.executeQuery();
			rs = s.getResultSet();

			long session_hours = 0;
			try {
				if(rs.first()){
			    	Date joined = new Date(rs.getLong("player_join") * 1000);
			    	Date today = new Date();
			    	session_hours = today.getTime() - joined.getTime();
			    	session_hours = session_hours / 1000;
				}
			}
			catch ( SQLException e ) {
				e.printStackTrace();
			}

			playtime = new Playtime( (int) (before_current + session_hours) );

		} catch (SQLException e){
            e.printStackTrace();
        } finally {
        	if(s != null) try { s.close(); } catch (SQLException e) {}
        	if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
		return playtime;
	}

	/**
	 *
	 * @param person
	 * @param account_name
	 * @throws Exception
	 */
	public static LinkedHashMap<Playtime,String> getPlayerPlaytimeHistory(User player) throws Exception{
		Connection conn = null;
		PreparedStatement s = null;
		LinkedHashMap<Playtime,String> playdates = new LinkedHashMap<Playtime, String>();
		try {

		    // Get Player ID
            PluginPlayer pluginPlayer = PlayerIdentification.getOraclePlayer( player.getName() );
            if( pluginPlayer == null ){
                throw new Exception("Player has never played on this server.");
            }

			conn = Oracle.dbc();
    		s = conn.prepareStatement(""
    		        + "SELECT DATE_FORMAT(player_join,'%Y-%m-%d') as playdate, "
    		        + "SUM(playtime) as playtime "
    		        + "FROM oracle_joins "
    		        + "WHERE player_id = ? "
    		        + "GROUP BY DATE_FORMAT(player_join,'%Y-%m-%d') "
    		        + "ORDER BY player_join DESC LIMIT 7");
    		s.setInt(1,pluginPlayer.getId());
    		s.executeQuery();
    		ResultSet rs = s.getResultSet();

    		while(rs.next()){
    		    playdates.put( new Playtime(rs.getInt("playtime")), rs.getString("playdate") );
			}
		} catch (SQLException e){
            e.printStackTrace();
        } finally {
        	if(s != null) try { s.close(); } catch (SQLException e) {}
        	if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
		return playdates;
	}
}
