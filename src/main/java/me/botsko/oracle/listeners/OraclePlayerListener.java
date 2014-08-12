package me.botsko.oracle.listeners;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import me.botsko.elixr.TypeUtils;
import me.botsko.oracle.Oracle;
import me.botsko.oracle.events.OracleFirstTimePlayerEvent;
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

        if( !Oracle.config.getBoolean("oracle.log-command-use-to-console") ) return;
        
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
        if( Oracle.config.getBoolean("oracle.joins.enabled") ){

	        // Run insert record in async thread
            new Thread(new Runnable(){
    			public void run(){
    				
    				JoinUtil.registerPlayerJoin( player, plugin.getServer().getOnlinePlayers().size() );
    				
    				// Cache playtime hour count so we can detect when it's increased
    				Playtime playtime;
                    try {
                        playtime = PlaytimeUtil.getPlaytime(player);
                        Oracle.playtimeHours.put(player,playtime.getHours());
                    } catch ( Exception e ){
                        e.printStackTrace();
                        return;
                    }
    			}
        	}).start();
	        
	        // Determine if we're using bungeecord as a proxy
	        if( Oracle.config.getBoolean("oracle.joins.use-bungeecord") ){
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
        if( Oracle.config.getBoolean("oracle.warnings.enabled") ){
             new Thread(new Runnable(){
                public void run(){
                    WarningUtil.alertStaffOnWarnLimit( player );
		        }
        	 }).start();
        }
    }
    
    /**
     * 
     * @param event
     */
    @SuppressWarnings("unchecked")
	@EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerFirstJoin(final OracleFirstTimePlayerEvent event){
    	
    	if( !Oracle.config.getBoolean("oracle.joins.enabled") ) return;
    	
    	final Player player = event.getPlayer();
    	
    	// Give them a guide book
    	if( Oracle.config.getBoolean("oracle.guidebook.enabled") ){
	        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
	        BookMeta meta = (BookMeta) book.getItemMeta();
	        meta.setTitle( Oracle.config.getString("oracle.guidebook.title") );
	        meta.setAuthor( Oracle.config.getString("oracle.guidebook.author") );
	        List<String> pages = (List<String>) Oracle.config.getList("oracle.guidebook.contents");
	        meta.setPages( pages );
	        book.setItemMeta(meta);
			player.getInventory().addItem( book );
    	}
    	
    	// Check for alt accounts in async thread
    	new Thread(new Runnable(){
			public void run(){
				try {
					
					List<String> alts = JoinUtil.getPlayerAlts( player );
					
					if( alts.isEmpty() ) return;
					
					String altList = TypeUtils.join( alts, ", " );
					for(Player pl: plugin.getServer().getOnlinePlayers()){
			    		if(pl.hasPermission("oracle.alerts.alt")){
			    			pl.sendMessage( Oracle.messenger.playerMsg( player.getName() + "'s alts: " + altList) );
			    		}
			    	}
					
				} catch (Exception e){
				    e.printStackTrace();
				}
			}
    	}).start();
    }
    
    /**
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLogin(final PlayerLoginEvent event){

    	if( !Oracle.config.getBoolean("oracle.bans.enabled") ) return;
    	
    	final Player player = event.getPlayer();

    	try {
    		final String ip = event.getAddress().getHostAddress().toString();
			BanUtil.playerMayJoin( player, ip );
		} catch (Exception e){
			event.setKickMessage( "Banned. " + e.getMessage() );
			event.setResult( Result.KICK_OTHER );
			Oracle.log( "Rejecting player login due to ban. For: " + player.getName() );
		}
    }
    
    /**
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(final PlayerQuitEvent event){
    	if( !Oracle.config.getBoolean("oracle.joins.enabled") ) return;
    	
    	new Thread(new Runnable(){
			public void run(){
		        try {
					JoinUtil.registerPlayerQuit( event.getPlayer() );
				} catch (Exception e){
				    e.printStackTrace();
				}
			}
    	}).start();
    }
    
    /**
	 * 
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerChat(AsyncPlayerChatEvent event){
		
		Player player = event.getPlayer();
		
		if( !Oracle.config.getBoolean("oracle.kick-minechat") ) return;
		
		if( event.getMessage().matches("connected.*MineChat") ){
			player.kickPlayer( "MineChat is not allowed... sorry" );
		}
	}
}