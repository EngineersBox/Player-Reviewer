package me.engineersbox.playerrev;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.intellectualsites.plotsquared.api.PlotAPI;

import me.engineersbox.playerrev.chunky.CameraObject;
import me.engineersbox.playerrev.chunky.CoordsObject;
import me.engineersbox.playerrev.chunky.JSONParameter;
import me.engineersbox.playerrev.enums.Status;
import me.engineersbox.playerrev.gitlab.GitLabManager;
import me.engineersbox.playerrev.methodlib.DynamicEnum;
import me.engineersbox.playerrev.methodlib.MaxSizeHashMap;
import me.engineersbox.playerrev.mysql.Config;
import me.engineersbox.playerrev.mysql.MySQL;
import me.engineersbox.playerrev.updater.SpigotUpdater;
import me.engineersbox.playerrev.updater.Updaters;
import me.lucko.luckperms.api.LuckPermsApi;

public class Main extends JavaPlugin implements Listener {
	
	public static FileConfiguration config;
	public static File cfile;
	
	//Globals
	public static String prefix = ChatColor.RED + "[" + ChatColor.DARK_AQUA + "Player Reviewer" + ChatColor.RED + "] ";
	public static void InfoHeader(Player p, String info) {
    	p.sendMessage(ChatColor.DARK_GRAY + "----=<{" + ChatColor.RED + "  [" + ChatColor.DARK_AQUA + info + ChatColor.RED + "]  " + ChatColor.DARK_GRAY + "}>=----");
	}
	public static boolean useConfigRanks;
	public static boolean useRanksInApplication;
	public static String configRankString;
	public static DynamicEnum<String, List<String>> ranksEnum;
	public static MySQL MySQL;
	public static PlotAPI plotapi;
	public static LuckPermsApi LPapi;
	static Connection c = null;
	public static boolean UseSQL;
	public static boolean usePlotLoc = false;
	public static String rankPlugin;
	public static boolean atConfirm = false;
	public static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	public static LocalDateTime now = null;
	public static Map<UUID, Status> appStatus = new HashMap<UUID, Status>();
	public static Map<String, CoordsObject> positions = new HashMap<String, CoordsObject>();
	public static Map<UUID, MaxSizeHashMap<String, CameraObject>> cameras = new HashMap<UUID, MaxSizeHashMap<String, CameraObject>>();
    public static Map<UUID, JSONParameter> paramMap = new HashMap<UUID, JSONParameter>();
    public static Map<UUID, String> chunkList = new HashMap<UUID, String>();
    public static Map<UUID, String[]> renderChecks = new HashMap<UUID, String[]>();
    public static ScheduledExecutorService ses = null;
	
    public void onEnable() {
    	
    	if (!getDataFolder().exists()) {
    		getDataFolder().mkdirs();
    	}
    	
    	try {
    		RegisteredServiceProvider<LuckPermsApi> provider = Bukkit.getServicesManager().getRegistration(LuckPermsApi.class);
			if (provider != null) {
			    LPapi = provider.getProvider();
			}
			
			RegisteredServiceProvider<PlotAPI> provider2 = Bukkit.getServicesManager().getRegistration(PlotAPI.class);
			if (provider2 != null) {
				plotapi = new PlotAPI();
			}
			
    	} catch (NoClassDefFoundError e) {
    		Bukkit.getLogger().warning("[PlayerReviewer] No provider for LuckPermsApi or PlotAPI");
    	}
    	
		if (Bukkit.getPluginManager().getPlugin("PermissionsEx") != null) {
			rankPlugin = "pex";
			Bukkit.getLogger().info("[PlayerReviewer] Registered provider for PermissionsEx");
		} else if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
			rankPlugin = "lp";
			Bukkit.getLogger().info("[PlayerReviewer] Registered provider for LuckPerms");
		}
		
		SpigotUpdater updater = new SpigotUpdater(this, 65535);
    	Updaters.checkVersion(updater);
    	
    	new InvConfig(this);
    	
    	Bukkit.getServer().getPluginManager().registerEvents(this, this);
    	PluginManager manager = Bukkit.getServer().getPluginManager();
    	final Plugin plotsquared = manager.getPlugin("PlotSquared");
    	
    	if(plotsquared != null && plotsquared.isEnabled()) {
    		Bukkit.getLogger().info("[PlayerReviewer] Found plugin PlotSquared! Plot locations enabled");
        	usePlotLoc = Config.usePlotLoc();
        } else {
        	Bukkit.getLogger().log(null, "[PlayerReviewer] Could not find PlotSquared! Reverting To Player Positions");
            return;
        }
    	
    	UseSQL = Config.SQLEnabled();
    	if (UseSQL == true) {
    		MySQL = new MySQL(Config.getHOSTNAME(), "", Config.getDATABASE(), Config.getUSER(), Config.getPASS());
        	try {
    			c = MySQL.openConnection();
    		} catch (SQLException | ClassNotFoundException e) {
    			e.printStackTrace();
    		}
    	}
    	
    	Config.InitRankConfig();
    	
        getCommand("pr").setExecutor(new Commands());
        getCommand("pr help").setExecutor(new Commands());
        getCommand("pr apphelp").setExecutor(new Commands());
        getCommand("pr renderhelp").setExecutor(new Commands());
        getCommand("pr apply").setExecutor(new Commands());
        getCommand("pr validranks").setExecutor(new Commands());
        getCommand("pr rate").setExecutor(new Commands());
        getCommand("pr gotoplot").setExecutor(new Commands());
        getCommand("pr ratings").setExecutor(new Commands());
        getCommand("pr approval").setExecutor(new Commands());
        getCommand("pr removeapplication").setExecutor(new Commands());
        getCommand("pr version").setExecutor(new Commands());
        getCommand("pr status").setExecutor(new Commands());
        getCommand("pr pos1").setExecutor(new Commands());
        getCommand("pr pos2").setExecutor(new Commands());
        getCommand("pr cam").setExecutor(new Commands());
        getCommand("pr chunkysettings").setExecutor(new Commands());
        getCommand("pr setparam").setExecutor(new Commands());
        getCommand("pr removeparam").setExecutor(new Commands());
        getCommand("pr viewparams").setExecutor(new Commands());
        getCommand("pr clearparams").setExecutor(new Commands());
        
        ses = Executors.newSingleThreadScheduledExecutor();
        Bukkit.getLogger().info("[PlayerReviewer] Initialised new Chunky render finish thread");
        
        ses.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
            	if (!renderChecks.isEmpty()) {
                	for (Map.Entry<UUID, String[]> entry : renderChecks.entrySet()) {
                		
                		Player p = Bukkit.getPlayer(entry.getKey());
            			String updated_time = GitLabManager.getUpdateTime(p);
            			
            			if (updated_time.equals(entry.getValue()[1])) {
            				List<String> renders = new ArrayList<>();
            				String renderString = "";
            				now = LocalDateTime.now();
            				
            				try {
								renders = GitLabManager.getRenderLinks(p);
								int count = 1;
								for (String cRender : renders) {
									renderString += "%2D%20Cam_" + count + "%3A%20%" + cRender + "%3C%62%72%2F%3E";
								}
								
								String description = GitLabManager.getIssueDescription("Application%20for%20" + p.getName())
	            						.replaceAll("\\*\\*Date Time\\*\\*: [0-9][0-9]\\/[0-9][0-9]\\/[0-9][0-9][0-9][0-9] [0-9][0-9]:[0-9][0-9]:[0-9][0-9]\\\\u003cbr\\/\\\\u003e", "%2A%2ADate Time%2A%2A: " + dtf.format(now) + "%3C%62%72%2F%3E")
	            						.replaceAll("", "%2A%2AChunky Render%2A%2A: " + renderString)
	            						.replaceAll("\\*", "%2A")
	            						.replaceAll("\\\\u003cbr\\/\\\\u003e", "%3C%62%72%2F%3E")
	            						.replaceAll("\\s", "%20")
	            						.replaceAll("\\.", "%2E")
	            						.replaceAll("\\@", "%40")
	            						.replaceAll("\\:", "%3A")
	            						.replaceAll("\\-", "%2D")
	            						.replaceAll("`", "%60");
	            				
	                			GitLabManager.editIssue("Application%20for%20" + p.getName(), description);
							} catch (IOException e) {
								Bukkit.getLogger().info("[PlayerReviewer] GitLab issue creator: could not access chunky render exported JSON for UUID: " + p.getUniqueId().toString());
							}
                		}
                	}
                }
            }
        }, 0, 15, TimeUnit.MINUTES);
    }
    
    @Override
    public void onDisable() {
    	AbstractFile.saveConfig();
    	if (UseSQL == true) {
    		try {
    			MySQL.closeConnection();
    			Bukkit.getLogger().info("[PlayerReviewer] Closed SQL connection");
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
    	}
    	Bukkit.getLogger().info("[PlayerReviewer] Destroyed Chunky render finish thread");
    	ses.shutdown();
    	Bukkit.getLogger().info("[PlayerReviewer] sbpschunky Disabled!");
    }
    
    
    
    public void playerLogin(PlayerLoginEvent e) {
    	Player p = e.getPlayer();
    	Status status = Main.appStatus.get(p.getUniqueId());
    	if (status != null) {
    		switch(status) {
			
				case APPROVED:
					p.sendMessage(Main.prefix + ChatColor.AQUA + "Your application was " + ChatColor.GREEN + "approved");
					break;
					
				case DENIED:
					p.sendMessage(Main.prefix + ChatColor.AQUA + "Your application was " + ChatColor.RED + "denied");
					break;
					
				default:
					break;
		
    		}
    	}
    }
    
}