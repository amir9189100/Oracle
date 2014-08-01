package me.botsko.oracle.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.botsko.oracle.Oracle;

public class ServerUtil {
	
	
	/**
	 * 
	 */
	public static int lookupServer(){
		return Oracle.oracleServer;
	}
	
	
	/**
	 * 
	 */
	public static int lookupServer( String server ){
		
		// Look at cache first
		if( Oracle.oracleServer > 0 ){
			return Oracle.oracleServer;
		}

		Connection conn = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {

			conn = Oracle.dbc();
    		s = conn.prepareStatement( "SELECT server_id FROM oracle_servers WHERE server = ?" );
    		s.setString(1, server);
    		rs = s.executeQuery();

    		if( rs.next() ){
    			Oracle.oracleServer = rs.getInt("server_id");
    			return rs.getInt("server_id");
    		} else {
    			return registerServer( server );
    		}
		} catch (SQLException e) {
		    e.printStackTrace();
//        	handleDatabaseException( e );
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
	protected static int registerServer( String server ){
		
		Connection conn = null;
		PreparedStatement s = null;
		ResultSet rs = null;
		try {

			conn = Oracle.dbc();
            s = conn.prepareStatement( "INSERT INTO oracle_servers (server) VALUES (?)" , Statement.RETURN_GENERATED_KEYS);
            s.setString(1, server);
            s.executeUpdate();
            
            rs = s.getGeneratedKeys();
            if (rs.next()) {
            	Oracle.oracleServer = rs.getInt(1);
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
}