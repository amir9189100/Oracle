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
package com.helion3.oracle;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.apache.tomcat.jdbc.pool.DataSource;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.config.DefaultConfig;
import org.spongepowered.api.text.Texts;

import com.google.inject.Inject;
import com.helion3.oracle.commands.OracleCommands;
import com.helion3.oracle.commands.WarnCommands;
import com.helion3.oracle.listeners.OraclePlayerListener;
import com.helion3.oracle.players.PlayerIdentification;
import com.helion3.oracle.players.PluginPlayer;
import com.helion3.oracle.tasks.PlaytimeMonitor;
import com.helion3.oracle.utils.AnnouncementUtil;
import com.helion3.oracle.utils.BungeeCord;
import com.helion3.oracle.utils.JoinUtil;
import com.helion3.oracle.utils.ServerUtil;

@Plugin(id = "Oracle", name = "Oracle", version = "2.0")
final public class Oracle {
    private static Configuration config;
    private static Game game;
    private static Logger logger;
    private static DataSource pool = new DataSource();

    private int last_announcement = 0;
    public static HashMap<UUID,PluginPlayer> oraclePlayers = new HashMap<UUID,PluginPlayer>();
    public static HashMap<Player,Integer> playtimeHours = new HashMap<Player,Integer>();
    public static int oracleServer = 0;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private File defaultConfig;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    /**
     * Performs bootstrapping of Prism resources/objects.
     *
     * @param event Server started
     */
    @Subscribe
    public void onServerStart(ServerStartedEvent event) {
        // Game reference
        game = event.getGame();

        // Load configuration file
        config = new Configuration(defaultConfig, configManager);

        // init db
        pool = initDbPool();
        Connection test_conn = dbc();
        if( pool == null || test_conn == null ){
            String[] dbDisabled = new String[3];
            dbDisabled[0] = "Oracle will disable itself because it couldn't connect to a database.";
            dbDisabled[1] = "If you're using MySQL, check your config. Be sure MySQL is running.";
            dbDisabled[2] = "For help - try http://discover-prism.com/wiki/view/troubleshooting/";
            // @todo disable plugin
        }
        if(test_conn != null){
            try {
                test_conn.close();
            } catch (SQLException e) {
                logDbError( e );
            }
        }


        // Setup databases
        setupDatabase();

        // Cache server id
        ServerUtil.lookupServer(getConfig().getNode("server-alias").getString());

        // Cache online players on reload
        PlayerIdentification.cacheOnlinePlayerPrimaryKeys();

        // Create join records for all currently online players
        for( Player pl : getGame().getServer().getOnlinePlayers() ){
            JoinUtil.registerPlayerJoin( pl, getGame().getServer().getOnlinePlayers().size() );
        }

        // Register tasks
        catchUncaughtDisconnects();
        runAnnouncements();
        runPlaytimeMonitor();

        logger.info("Oracle is listening. Don't worry about the vase.");
    }

    /**
     * Returns the plugin configuration
     * @return Configuration
     */
    public static Configuration getConfig() {
        return config;
    }

    /**
     * Returns the current game
     * @return Game
     */
    public static Game getGame() {
        return game;
    }

    /**
     * Injects the Logger instance for this plugin
     * @param log Logger
     */
    @Inject
    private void setLogger(Logger log) {
        logger = log;
    }

    /**
     * Returns the Logger instance for this plugin.
     * @return Logger instance
     */
    public static Logger getLogger() {
        return logger;
    }

	/**
	 *
	 * @return
	 */
	public DataSource initDbPool(){

		DataSource pool = null;
		String dns = "jdbc:mysql://" +
		        getConfig().getNode("db", "hostname").getString() + ":" +
		        getConfig().getNode("db", "port").getString() + "/" +
		        getConfig().getNode("db", "name").getString();
		pool = new DataSource();
		pool.setDriverClassName("com.mysql.jdbc.Driver");
		pool.setUrl(dns);
	    pool.setUsername(getConfig().getNode("db", "user").getString());
	    pool.setPassword(getConfig().getNode("db", "pass").getString());
//		pool.setMaxActive( config.getInt("oracle.database.max-pool-connections") );
//		pool.setMaxIdle( config.getInt("oracle.database.max-pool-connections") );
//	    pool.setMaxWait( config.getInt("oracle.database.max-wait") );
	    pool.setRemoveAbandoned(true);
		pool.setRemoveAbandonedTimeout(60);
		pool.setTestOnBorrow(true);
		pool.setValidationQuery("/* ping */SELECT 1");
		pool.setValidationInterval(30000);

		return pool;
	}

	/**
	 * Attempt to rebuild the pool, useful for reloads and failed database
	 * connections being restored
	 */
	public void rebuildPool() {
		// Close pool connections when plugin disables
		if (pool != null) {
			pool.close();
		}
		pool = initDbPool();
	}

	/**
	 *
	 * @return
	 */
	public static DataSource getPool(){
		return pool;
	}

	/**
	 *
	 * @return
	 * @throws SQLException
	 */
	public static Connection dbc() {
		Connection con = null;
		try {
			con = pool.getConnection();
		} catch (SQLException e) {
			System.out.print("Database connection failed. " + e.getMessage());
			if (!e.getMessage().contains("Pool empty")) {
				e.printStackTrace();
			}
		}
		return con;
	}

	/**
	 *
	 */
	protected void setupDatabase() {

		try {
			final Connection conn = dbc();
			if (conn == null)
				return;

			String query = "CREATE TABLE IF NOT EXISTS `oracle_announcements` (" +
					"`announcement_id` int(11) NOT NULL AUTO_INCREMENT," +
					"`announcement` varchar(255) NOT NULL," +
					"`type` varchar(16) NOT NULL," +
					"`is_active` tinyint(1) NOT NULL," +
					"PRIMARY KEY (`announcement_id`)" +
					") ENGINE=InnoDB  DEFAULT CHARSET=latin1;";
			Statement st = conn.createStatement();
			st.executeUpdate(query);

			query = "CREATE TABLE IF NOT EXISTS `oracle_bans` (" +
					"`ban_id` int(11) NOT NULL AUTO_INCREMENT," +
					"`player_id` int(11) unsigned DEFAULT NULL," +
					"`ip_id` int(10) unsigned DEFAULT NULL," +
					"`staff_player_id` int(11) unsigned NOT NULL," +
					"`reason` varchar(255) NOT NULL," +
					"`epoch` int(11) unsigned NOT NULL," +
					"`unbanned` tinyint(1) NOT NULL DEFAULT '0'," +
					"PRIMARY KEY (`ban_id`)," +
					"KEY `ip_id` (`ip_id`)," +
					"KEY `player_id` (`player_id`)" +
					") ENGINE=InnoDB  DEFAULT CHARSET=latin1;";
			st.executeUpdate(query);

			query = "CREATE TABLE IF NOT EXISTS `oracle_ips` (" +
					"`ip_id` int(10) unsigned NOT NULL AUTO_INCREMENT," +
					"`ip` int(10) unsigned NOT NULL," +
					"PRIMARY KEY (`ip_id`)," +
					"KEY `ip` (`ip`)" +
					") ENGINE=InnoDB  DEFAULT CHARSET=latin1;";
			st.executeUpdate(query);

			query = "CREATE TABLE IF NOT EXISTS `oracle_joins` (" +
					"`join_id` int(11) unsigned NOT NULL AUTO_INCREMENT," +
					"`server_id` int(10) unsigned NOT NULL," +
					"`player_count` smallint(4) unsigned NOT NULL," +
					"`player_id` int(10) unsigned NOT NULL," +
					"`player_join` int(11) NOT NULL," +
					"`player_quit` int(11) unsigned DEFAULT NULL," +
					"`playtime` int(11) unsigned DEFAULT NULL," +
					"`ip_id` int(10) unsigned NOT NULL," +
					"PRIMARY KEY (`join_id`)," +
					"KEY `player_id` (`player_id`)," +
					"KEY `ip_id` (`ip_id`)," +
					"KEY `server_id` (`server_id`)," +
					"KEY `playtime` (`playtime`)," +
					"KEY `player_quit` (`player_quit`)" +
					") ENGINE=InnoDB  DEFAULT CHARSET=latin1;";
			st.executeUpdate(query);

			query = "CREATE TABLE IF NOT EXISTS `oracle_players` (" +
					"`player_id` int(10) unsigned NOT NULL AUTO_INCREMENT," +
					"`player` varchar(16) NOT NULL," +
					"`player_uuid` binary(16) NOT NULL," +
					"PRIMARY KEY (`player_id`)," +
					"KEY `player` (`player`)" +
					") ENGINE=InnoDB  DEFAULT CHARSET=latin1;";
			st.executeUpdate(query);

			query = "CREATE TABLE IF NOT EXISTS `oracle_servers` (" +
					"`server_id` int(10) unsigned NOT NULL AUTO_INCREMENT," +
					"`server` varchar(16) NOT NULL," +
					"PRIMARY KEY (`server_id`)" +
					") ENGINE=InnoDB  DEFAULT CHARSET=latin1;";
			st.executeUpdate(query);

			query = "CREATE TABLE IF NOT EXISTS `oracle_unbans` (" +
					"`unban_id` int(11) NOT NULL AUTO_INCREMENT," +
					"`player_id` int(11) unsigned DEFAULT NULL," +
					"`ip_id` int(10) unsigned DEFAULT NULL," +
					"`staff_player_id` int(11) unsigned NOT NULL," +
					"`epoch` int(11) unsigned NOT NULL," +
					"PRIMARY KEY (`unban_id`)" +
					") ENGINE=InnoDB  DEFAULT CHARSET=latin1;";
			st.executeUpdate(query);

			query = "CREATE TABLE IF NOT EXISTS `oracle_warnings` (" +
					"`warning_id` int(11) NOT NULL AUTO_INCREMENT," +
					"`player_id` int(11) unsigned NOT NULL," +
					"`reason` text NOT NULL," +
					"`staff_player_id` int(11) unsigned NOT NULL," +
					"`epoch` int(11) unsigned NOT NULL," +
					"`deleted` tinyint(1) NOT NULL DEFAULT '0'," +
					"PRIMARY KEY (`warning_id`)" +
					") ENGINE=InnoDB  DEFAULT CHARSET=latin1;";
			st.executeUpdate(query);


			st.close();
			conn.close();

		} catch (SQLException e) {
			getLogger().error("Database connection error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * If a user disconnects in an unknown way that is never caught by onPlayerQuit,
	 * this will force close all records except for players currently online.
	 */
	public void catchUncaughtDisconnects(){
		if (getConfig().getNode("joins", "enabled").getBoolean() ){
		    getGame().getScheduler().getTaskBuilder()
    		    .async()
    		    .delay(1200L)
    		    .interval(1200L)
    		    .execute(new Runnable(){
                    @Override
                    public void run(){
                        String on_users = "";
                        for(Player pl: getGame().getServer().getOnlinePlayers()) {

                            PluginPlayer pluginPlayer = PlayerIdentification.getOraclePlayer(pl);
                            if( pluginPlayer == null ) continue;

                            on_users += ""+pluginPlayer.getId()+",";
                        }
                        if(!on_users.isEmpty()){
                            on_users = on_users.substring(0, on_users.length()-1);
                        }
                        JoinUtil.forceDateForOfflinePlayers( on_users );
                    }
                }).submit(this);
		}
	}

	/**
	 * If a user disconnects in an unknown way that is never caught by onPlayerQuit,
	 * this will force close all records except for players currently online.
	 */
	public void runAnnouncements(){
        getGame().getScheduler().getTaskBuilder()
            .async()
            .delay(6000L)
            .interval(6000L)
            .execute(new Runnable(){
                @Override
                public void run(){
                    // Pull all items matching this name
                    List<String> announces = AnnouncementUtil.getActiveAnnouncements();
                    if(!announces.isEmpty()){

                        if(last_announcement >= announces.size()){
                            last_announcement = 0;
                        }

                        String msg = announces.get(last_announcement);
                        for(Player pl : game.getServer().getOnlinePlayers()) {
                            pl.sendMessage(Texts.of(msg)); // @todo colorize
                        }
                        logger.info( msg );

                        last_announcement++;
                    }
                }
            }).submit(this);
	}

	/**
	 * If a user disconnects in an unknown way that is never caught by onPlayerQuit,
	 * this will force close all records except for players currently online.
	 */
	public void runPlaytimeMonitor(){
		if (getConfig().getNode("joins", "enabled").getBoolean()) {
            getGame().getScheduler().getTaskBuilder()
                .async()
                .delay(12000L)
                .interval(12000L)
                .execute(new PlaytimeMonitor()).submit(this);
		}
	}

	/**
     * Partial username matching
     * @param Name
     * @return
     */
    public static String expandName(String Name) {
        int m = 0;
        String Result = "";
        for ( Player p : game.getServer().getOnlinePlayers() ) {
            String str = p.getName();
            if (str.matches("(?i).*" + Name + ".*")) {
                m++;
                Result = str;
                if(m==2) {
                    return null;
                }
            }
            if (str.equalsIgnoreCase(Name))
                return str;
        }
        if (m == 1)
            return Result;
        if (m > 1) {
            return Name;
        }
        if (m < 1) {
            return Name;
        }
        return Name;
    }

	/**
	 *
	 */
	public void logDbError( SQLException e ){
		logger.error("Database connection error: " + e.getMessage());
		e.printStackTrace();
	}

	@Subscribe
    public void onServerStop(ServerStoppingEvent event) {
	    // Force offline date for everyone
        if (getConfig().getNode("joins", "enabled").getBoolean()) {
            JoinUtil.forceDateForAllPlayers();
        }

        // Close pool connections when plugin disables
        if (pool != null) {
            pool.close();
        }
	}
}