package me.engineersbox.playerrev;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.gitlab4j.api.GitLabApi;

import com.github.intellectualsites.plotsquared.api.PlotAPI;

import me.engineersbox.playerrev.chunky.CameraObject;
import me.engineersbox.playerrev.chunky.CoordsObject;
import me.engineersbox.playerrev.chunky.JSONParameter;
import me.engineersbox.playerrev.enums.Status;
import me.engineersbox.playerrev.gitlab.GitConfig;
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
	public static GitLabApi gitlabAPI;
	static Connection c = null;
	public static boolean UseSQL;
	public static boolean usePlotLoc = false;
	public static String rankPlugin;
	public static boolean atConfirm = false;
	public static Map<UUID, Status> appStatus = new HashMap<UUID, Status>();
	
	public static Map<String, CoordsObject> positions = new HashMap<String, CoordsObject>();
	public static Map<UUID, MaxSizeHashMap<String, CameraObject>> cameras = new HashMap<UUID, MaxSizeHashMap<String, CameraObject>>();
    public static Map<UUID, JSONParameter> paramMap = new HashMap<UUID, JSONParameter>();
    public static Map<UUID, String> chunkList = new HashMap<UUID, String>();
	
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
		} else if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
			rankPlugin = "lp";
		}
		
		SpigotUpdater updater = new SpigotUpdater(this, 65535);
    	Updaters.checkVersion(updater);
    	
    	new InvConfig(this);
    	
    	Bukkit.getServer().getPluginManager().registerEvents(this, this);
    	PluginManager manager = Bukkit.getServer().getPluginManager();
    	final Plugin plotsquared = manager.getPlugin("PlotSquared");
    	
    	if(plotsquared != null && plotsquared.isEnabled()) {
    		Bukkit.getLogger().info("[PlayerReviewer] Found plugin PlotSquared! Plot locations enabled");
    		Bukkit.getLogger().info("[PlayerReviewer] sbpschunky Enabled!");
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
    	
    	if (GitConfig.useToken()) {
			gitlabAPI = new GitLabApi(GitConfig.getGitAddress(), GitConfig.getAccesskey());
		} else {
			gitlabAPI = new GitLabApi(GitConfig.getGitAddress(), GitConfig.getGitUser(), GitConfig.getGitPass());
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
    }
    
    @Override
    public void onDisable() {
    	Bukkit.getLogger().info("[PlayerReviewer] sbpschunky Disabled!");
    	AbstractFile.saveConfig();
    	if (UseSQL == true) {
    		try {
    			MySQL.closeConnection();
    		} catch (SQLException e) {
    			e.printStackTrace();
    		}
    	}
 
    }
    
    public List<String> addToListIfMatched(List<String> list, String valueToAdd, String toMatch) {
        if (valueToAdd.toLowerCase().startsWith(toMatch.toLowerCase())) {
            list.add(valueToAdd);
        }
        return list;
    }

    
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
    	if (sender instanceof Player) {
    		Player p = (Player) sender;
    		if (!p.hasPermission("pr.use")) {
    			return null;
    		}
    		
    		List<String> comp = new ArrayList<String>();
    		if (cmd.getName().equals("pr")) {
    			if (args.length == 0) {
    				comp = addToListIfMatched(comp, "help", "");
        			comp = addToListIfMatched(comp, "apphelp", "");
        			comp = addToListIfMatched(comp, "renderhelp", "");
        			comp = addToListIfMatched(comp, "apply", "");
        			comp = addToListIfMatched(comp, "validranks", "");
        			comp = addToListIfMatched(comp, "rate", "");
        			comp = addToListIfMatched(comp, "gotoplot", "");
        			comp = addToListIfMatched(comp, "ratings", "");
        			comp = addToListIfMatched(comp, "approval", "");
        			comp = addToListIfMatched(comp, "removeapplication", "");
        			comp = addToListIfMatched(comp, "version", "");
        			comp = addToListIfMatched(comp, "status", "");
        			comp = addToListIfMatched(comp, "pos1", "");
        			comp = addToListIfMatched(comp, "pos2", "");
        			comp = addToListIfMatched(comp, "cam", "");
        			comp = addToListIfMatched(comp, "chunkysettings", "");
        			comp = addToListIfMatched(comp, "setparam", "");
        			comp = addToListIfMatched(comp, "removeparam", "");
        			comp = addToListIfMatched(comp, "viewparams", "");
        			comp = addToListIfMatched(comp, "clearparams", "");
    			} else if (args.length == 1) {
    				comp = addToListIfMatched(comp, "help", args[0]);
        			comp = addToListIfMatched(comp, "apphelp", args[0]);
        			comp = addToListIfMatched(comp, "renderhelp", args[0]);
        			comp = addToListIfMatched(comp, "apply", args[0]);
        			comp = addToListIfMatched(comp, "validranks", args[0]);
        			comp = addToListIfMatched(comp, "rate", args[0]);
        			comp = addToListIfMatched(comp, "gotoplot", args[0]);
        			comp = addToListIfMatched(comp, "ratings", args[0]);
        			comp = addToListIfMatched(comp, "approval", args[0]);
        			comp = addToListIfMatched(comp, "removeapplication", args[0]);
        			comp = addToListIfMatched(comp, "version", args[0]);
        			comp = addToListIfMatched(comp, "status", args[0]);
        			comp = addToListIfMatched(comp, "pos1", args[0]);
        			comp = addToListIfMatched(comp, "pos2", args[0]);
        			comp = addToListIfMatched(comp, "cam", args[0]);
        			comp = addToListIfMatched(comp, "chunkysettings", args[0]);
        			comp = addToListIfMatched(comp, "setparam", args[0]);
        			comp = addToListIfMatched(comp, "removeparam", args[0]);
        			comp = addToListIfMatched(comp, "viewparams", args[0]);
        			comp = addToListIfMatched(comp, "clearparams", args[0]);
    			}
    		}
    		return comp;
    	}
		return null;
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