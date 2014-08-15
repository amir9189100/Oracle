package me.botsko.oracle.players;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import me.botsko.elixr.TypeUtils;
import me.botsko.oracle.Oracle;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlayerIdentification {

    /**
     * Loads `oracle_players` ID for a real player into our cache.
     *
     * Runs during PlayerJoin events, so it will never be for a fake/null
     * player.
     *
     * @param player
     */
    public static PluginPlayer cacheOraclePlayer( final OfflinePlayer player ){

        // Lookup the player
        PluginPlayer pluginPlayer = getOraclePlayer( player );
        if( pluginPlayer != null ){
            pluginPlayer = comparePlayerToCache( player, pluginPlayer );
            Oracle.debug("Loaded player " + player.getName() + ", id: " + pluginPlayer.getId() + " into the cache.");
            Oracle.oraclePlayers.put( player.getUniqueId(), pluginPlayer );
            return pluginPlayer;
        }

        return pluginPlayer;

    }

    /**
     * Returns a `oracle_players` ID for the described player name. If
     * one cannot be found, returns 0.
     *
     * Used by the recorder in determining proper foreign key
     *
     * @param playerName
     * @return
     */
    public static PluginPlayer getOraclePlayer( String playerName ){

        Player player = Bukkit.getPlayer(playerName);

        if( player != null ) return getOraclePlayer( player );

        // Player not online, we need to go to cache
        PluginPlayer pluginPlayer = lookupByName( playerName );

        // Player found! Return the id
        if( pluginPlayer != null ) return pluginPlayer;

        // No player exists! We must create one
        return null;

    }

    /**
     * Returns a `oracle_players` ID for the described player object. If
     * one cannot be found, returns 0.
     *
     * Used by the recorder in determining proper foreign key
     *
     * @param playerName
     * @return
     */
    public static PluginPlayer getOraclePlayer( OfflinePlayer player ){

        if( player.getUniqueId() == null ){
            // If they have a name, we can attempt to find them that way
            if( player.getName() != null && !player.getName().trim().isEmpty() ){
                return getOraclePlayer( player.getName() );
            }
            // No name, no UUID, no service.
            return null;
        }

        PluginPlayer pluginPlayer = null;

        // Are they in the cache?
        pluginPlayer = Oracle.oraclePlayers.get( player.getUniqueId() );
        if( pluginPlayer != null ) return pluginPlayer;

        // Lookup by UUID
        pluginPlayer = lookupByUUID( player.getUniqueId() );
        if( pluginPlayer != null ) return pluginPlayer;

        // Still not found, try looking them up by name
        pluginPlayer = lookupByName( player.getName() );
        if( pluginPlayer != null ) return pluginPlayer;

        return null;

    }

    /**
     * Compares the known player to the cached data. If there's a difference
     * we need to handle it.
     *
     * If usernames are different: Update `oracle_players` with new name
     * (@todo track historical?)
     *
     * If UUID is different, log an error.
     *
     * @param player
     * @param pluginPlayer
     * @return
     */
    protected static PluginPlayer comparePlayerToCache( OfflinePlayer player, PluginPlayer pluginPlayer ){

        // Compare for username differences, update database
        if( !player.getName().equals( pluginPlayer.getName() ) ){
            pluginPlayer.setName( player.getName() );
            updatePlayer(pluginPlayer);
        }

        // Compare UUID
        if( !player.getUniqueId().equals( pluginPlayer.getUUID() ) ){
            Oracle.log("Player UUID for " +player.getName() + " does not match our cache! " +player.getUniqueId()+ " versus cache of " + pluginPlayer.getUUID());

            // Update anyway...
            pluginPlayer.setUUID( player.getUniqueId() );
            updatePlayer(pluginPlayer);

        }

        return pluginPlayer;

    }

    /**
     * Converts UUID to a string ready for use against database
     * @param player
     */
    protected static String uuidToDbString( UUID id ){
        return id.toString().replace("-", "");
    }

    /**
     * Converts UUID to a string ready for use against database
     * @param player
     */
    protected static UUID uuidFromDbString( String uuid ){
        // Positions need to be -2
        String completeUuid = uuid.substring(0, 8);
        completeUuid += "-" + uuid.substring(8,12);
        completeUuid += "-" + uuid.substring(12,16);
        completeUuid += "-" + uuid.substring(16,20);
        completeUuid += "-" + uuid.substring(20, uuid.length());
        completeUuid = completeUuid.toLowerCase();
        return UUID.fromString(completeUuid);
    }

    /**
     * Saves a real player's UUID and current Username to the `oracle_players`
     * table. At this stage, we're pretty sure the UUID and username do not
     * already exist.
     * @param player
     */
    public static PluginPlayer addPlayer( Player player ){

        PluginPlayer pluginPlayer = new PluginPlayer( 0, player.getUniqueId(), player.getName() );

        Connection conn = null;
        PreparedStatement s = null;
        ResultSet rs = null;
        try {

            conn = Oracle.dbc();
            s = conn.prepareStatement( "INSERT INTO oracle_players (player,player_uuid) VALUES (?,UNHEX(?))" , Statement.RETURN_GENERATED_KEYS);
            s.setString(1, player.getName() );
            s.setString(2, uuidToDbString( player.getUniqueId() ) );
            s.executeUpdate();

            rs = s.getGeneratedKeys();
            if (rs.next()) {
                pluginPlayer.setId(rs.getInt(1));
                Oracle.debug("Saved and loaded player " + player.getName() + " (" + player.getUniqueId() + ") into the cache.");
                Oracle.oraclePlayers.put( player.getUniqueId(), new PluginPlayer( rs.getInt(1), player.getUniqueId(), player.getName() ) );
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
        return pluginPlayer;
    }

    /**
     * Saves a player's UUID to the oracle_players table. We cache the current username
     * as well.
     */
    protected static void updatePlayer( PluginPlayer pluginPlayer ){
        Connection conn = null;
        PreparedStatement s = null;
        ResultSet rs = null;
        try {

            conn = Oracle.dbc();
            s = conn.prepareStatement( "UPDATE oracle_players SET player = ?, player_uuid = UNHEX(?) WHERE player_id = ?");
            s.setString(1, pluginPlayer.getName() );
            s.setString(2, uuidToDbString( pluginPlayer.getUUID() ) );
            s.setInt(3, pluginPlayer.getId() );
            s.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(rs != null) try { rs.close(); } catch (SQLException e) {}
            if(s != null) try { s.close(); } catch (SQLException e) {}
            if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
    }

    /**
     * Loads `oracle_players` ID for a player into our cache.
     */
    protected static PluginPlayer lookupByName( String playerName ){
        PluginPlayer pluginPlayer = null;
        Connection conn = null;
        PreparedStatement s = null;
        ResultSet rs = null;
        try {

            conn = Oracle.dbc();
            s = conn.prepareStatement( "SELECT player_id, player, HEX(player_uuid) FROM oracle_players WHERE player = ?" );
            s.setString(1, playerName);
            rs = s.executeQuery();

            if( rs.next() ){
                pluginPlayer = new PluginPlayer( rs.getInt(1), uuidFromDbString(rs.getString(3)), rs.getString(2) );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(rs != null) try { rs.close(); } catch (SQLException e) {}
            if(s != null) try { s.close(); } catch (SQLException e) {}
            if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
        return pluginPlayer;
    }

    /**
     * Loads `oracle_players` ID for a player into our cache.
     */
    protected static PluginPlayer lookupByUUID( UUID uuid ){
        PluginPlayer pluginPlayer = null;
        Connection conn = null;
        PreparedStatement s = null;
        ResultSet rs = null;
        try {

            conn = Oracle.dbc();
            s = conn.prepareStatement( "SELECT player_id, player, HEX(player_uuid) FROM oracle_players WHERE player_uuid = UNHEX(?)" );
            s.setString(1, uuidToDbString(uuid));
            rs = s.executeQuery();

            if( rs.next() ){
                pluginPlayer = new PluginPlayer( rs.getInt(1), uuidFromDbString(rs.getString(3)), rs.getString(2) );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if(rs != null) try { rs.close(); } catch (SQLException e) {}
            if(s != null) try { s.close(); } catch (SQLException e) {}
            if(conn != null) try { conn.close(); } catch (SQLException e) {}
        }
        return pluginPlayer;
    }

    /**
     * Build-load all online players into cache
     */
    public static void cacheOnlinePlayerPrimaryKeys(){
        
        String[] playerNames;
        playerNames = new String[ Bukkit.getServer().getOnlinePlayers().size() ];
        int i = 0;
        for( Player pl : Bukkit.getServer().getOnlinePlayers() ){
            playerNames[i] = pl.getName();
            i++;
        }

        Connection conn = null;
        PreparedStatement s = null;
        ResultSet rs = null;
        try {

            conn = Oracle.dbc();
            s = conn.prepareStatement( "SELECT player_id, player, HEX(player_uuid) FROM oracle_players WHERE player IN (?)" );
            s.setString(1, "'"+TypeUtils.join(playerNames, "','")+"'");
            rs = s.executeQuery();

            while( rs.next() ){
                PluginPlayer pluginPlayer = new PluginPlayer( rs.getInt(1), uuidFromDbString(rs.getString(3)), rs.getString(2) );
                Oracle.debug("Loaded player " + rs.getString(2) + ", id: " + rs.getInt(1) + " into the cache.");
                Oracle.oraclePlayers.put( UUID.fromString(rs.getString(2)), pluginPlayer );
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