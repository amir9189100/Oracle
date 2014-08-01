package me.botsko.oracle.listeners;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import me.botsko.oracle.Oracle;
import me.botsko.oracle.events.OracleFirstTimePlayerEvent;
import me.botsko.oracle.utils.Alt;
import me.botsko.oracle.utils.BanUtil;
import me.botsko.oracle.utils.JoinUtil;
import me.botsko.oracle.utils.Playtime;
import me.botsko.oracle.utils.PlaytimeUtil;
import me.botsko.oracle.utils.WarningUtil;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class OraclePlayerListener implements Listener {
	
	/**
	 * 
	 */
	protected Oracle plugin;
	
	
	/**
	 * 
	 * @param plugin
	 */
	public OraclePlayerListener( Oracle plugin ){
		this.plugin = plugin;
	}
	
	
	/**
	 * 
	 * @param event
	 */
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {

        Player player = event.getPlayer();
        String cmd = event.getMessage();

        if( !plugin.getConfig().getBoolean("oracle.log-command-use-to-console") ) return;
        
    	int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();
        Oracle.log( "[Cmd] " + player.getName() + " " + cmd + " @" + player.getWorld().getName() + " " + x + " " + y + " " + z);
        
    }
	
	
	/**
	 * 
	 * @param event
	 */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        
        final Player player = event.getPlayer();
        final String username = player.getName();
        
        // Track joins
        if( plugin.getConfig().getBoolean("oracle.joins.enabled") ){

	        // Run insert record in async thread
        	plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable(){
    			public void run(){
    				
    				JoinUtil.registerPlayerJoin( player, plugin.getServer().getOnlinePlayers().size() );
    				
    				// Cache playtime hour count so we can detect when it's increased
    				Playtime playtime;
                    try {
                        playtime = PlaytimeUtil.getPlaytime(player);
                        Oracle.playtimeHours.put(player,playtime.getHours());
                    } catch ( Exception e ) {
                        return;
                    }
    			}
        	});
	        
	        // Determine if we're using bungeecord as a proxy
	        if( plugin.getConfig().getBoolean("oracle.joins.use-bungeecord") ){
		        // Pass the information from bungee so we properly track the ip
		        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
		            public void run() {
		                try {
		                	
		                    ByteArrayOutputStream b = new ByteArrayOutputStream();
		                    DataOutputStream out = new DataOutputStream(b);
		
		                    try {
		                        out.writeUTF("IP");
		                    } catch (IOException e){
		                    }
		
		                    plugin.getServer().getPlayerExact( username ).sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
		
		                } catch (Exception exception) {
		                    exception.printStackTrace();
		                }
		            }
		        }, 30L);
	        }
        }
        
        // Track warnings
        if( plugin.getConfig().getBoolean("oracle.warnings.enabled") ){
        	 plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
		            public void run() {
		            	WarningUtil.alertStaffOnWarnLimit( player );
		            }
        	 });
        }
    }
    
    
    /**
     * 
     * @param event
     */
    @SuppressWarnings("unchecked")
	@EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerFirstJoin(final OracleFirstTimePlayerEvent event){
    	
    	if( !plugin.getConfig().getBoolean("oracle.joins.enabled") ) return;
    	
    	final Player player = event.getPlayer();
    	
    	// Give them a guide book
    	if( plugin.getConfig().getBoolean("oracle.guidebook.enabled") ){
	        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
	        BookMeta meta = (BookMeta) book.getItemMeta();
	        meta.setTitle( plugin.getConfig().getString("oracle.guidebook.title") );
	        meta.setAuthor( plugin.getConfig().getString("oracle.guidebook.author") );
	        List<String> pages = (List<String>) plugin.getConfig().getList("oracle.guidebook.contents");
	        meta.setPages( pages );
	        book.setItemMeta(meta);
			player.getInventory().addItem( book );
    	}
    	
    	// Check for alt accounts in async thread
    	plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable(){
			public void run(){

		    	List<Alt> alt_accts;
				try {
					
					alt_accts = JoinUtil.getPlayerAlts( player );
					
					if( alt_accts.isEmpty() ) return;
					
					String alts_list = "";
					int i = 1;
					for(Alt alt : alt_accts){
						alts_list += alt.username + (i == alt_accts.size() ? "" : ", ");
						i++;
					}
					for(Player pl: plugin.getServer().getOnlinePlayers()) {
			    		if(pl.hasPermission("oracle.alerts.alt")){
			    			pl.sendMessage( Oracle.messenger.playerMsg( player.getName() + "'s alts: " + alts_list) );
			    		}
			    	}
					
				} catch (Exception e) {
				}
			}
    	});
    }
    
    
    /**
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(final PlayerLoginEvent event){

    	if( !plugin.getConfig().getBoolean("oracle.bans.enabled") ) return;
    	
    	final Player player = event.getPlayer();
    	
    	// sync can't kick this way, not a huge need atm
//    	plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable(){
//			public void run(){
		    	try {
		    		final String ip = event.getAddress().getHostAddress().toString();
					BanUtil.playerMayJoin( player, ip );
				} catch (Exception e){
					event.setKickMessage( "Banned. " + e.getMessage() );
					event.setResult( Result.KICK_OTHER );
					Oracle.log( "Rejecting player login due to ban. For: " + player.getName() );
				}
//			}
//    	});
    }
    
    
    /**
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(final PlayerQuitEvent event){
    	if( !plugin.getConfig().getBoolean("oracle.joins.enabled") ) return;
    	
    	plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable(){
			public void run(){
		        try {
					JoinUtil.registerPlayerQuit( event.getPlayer() );
				} catch (Exception e) {
				}
			}
    	});
    }

    
    /**
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerChat(AsyncPlayerChatEvent event){
		
		Player player = event.getPlayer();
		
		if( !plugin.getConfig().getBoolean("oracle.kick-minechat") ) return;
		
		if( event.getMessage().matches("connected.*MineChat") ){
			player.kickPlayer( "MineChat is not allowed... sorry" );
		}
	}
}