package me.botsko.oracle.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.botsko.oracle.Oracle;

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
