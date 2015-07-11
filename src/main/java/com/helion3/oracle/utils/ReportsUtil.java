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

import com.helion3.oracle.Oracle;

public class ReportsUtil {

	/**
	 * 
	 * @return
	 */
	public static int getPlayerJoinCount(){
		int total = 0;
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {
			
			conn = Oracle.dbc();
			s = conn.prepareStatement ("SELECT COUNT( DISTINCT(player_id) ) FROM `oracle_joins`");
			s.executeQuery();
			rs = s.getResultSet();
			
			if(rs.first()){
				total = rs.getInt(1);
			}
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	    	if(rs != null) try { rs.close(); } catch (SQLException e) {}
	    	if(s != null) try { s.close(); } catch (SQLException e) {}
	    	if(conn != null) try { conn.close(); } catch (SQLException e) {}
	    }
		return total;
	}
	
	/**
	 * 
	 * @return
	 */
	public static int getPlayerJoinTodayCount(){
//		int total = 0;
//		Connection conn = null;
//		PreparedStatement s = null;
//		ResultSet rs = null;
//		try {
//			
//			conn = Oracle.dbc();
//			s = conn.prepareStatement ("SELECT COUNT( DISTINCT(player_id) ) FROM `oracle_joins` WHERE DATE_FORMAT(player_join,'%Y-%m-%d') = DATE_FORMAT(NOW(),'%Y-%m-%d')");
//			s.executeQuery();
//			rs = s.getResultSet();
//			
//			if(rs.first()){
//				total = rs.getInt(1);
//			}
//	        
//	    } catch (SQLException e) {
//	        e.printStackTrace();
//	    } finally {
//	    	if(rs != null) try { rs.close(); } catch (SQLException e) {}
//	    	if(s != null) try { s.close(); } catch (SQLException e) {}
//	    	if(conn != null) try { conn.close(); } catch (SQLException e) {}
//	    }
//		return total;
		return 0;
	}
}
