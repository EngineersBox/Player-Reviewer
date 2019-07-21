package me.engineersbox.playerrev;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.intellectualsites.plotsquared.api.PlotAPI;

import org.bukkit.entity.Player;

import me.engineersbox.playerrev.InvConfig;
import me.engineersbox.playerrev.methodlib.DynamicEnum;
import me.engineersbox.playerrev.methodlib.MaxSizeHashMap;
import me.engineersbox.playerrev.mysql.MySQL;
import me.engineersbox.playerrev.mysql.SQLConfig;
import me.engineersbox.playerrev.updater.SpigotUpdater;
import me.engineersbox.playerrev.updater.Updaters;
import me.lucko.luckperms.api.LuckPermsApi;

class position {
	
    public double x = 0;
    public double y = 0;
    public double z = 0;

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}

class orientation {
	
    public double roll = 0;
    public double pitch = 0;
    public double yaw = 0;

    public double getRoll() {
        return roll;
    }

    public void setRoll(double roll) {
        this.roll = roll;
    }

    public double getPitch() {
        return pitch;
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
    }

    public double getYaw() {
        return yaw;
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
    }
}
class CameraObject {
	
    public String name = "camera 1";
    public position position;
    public orientation orientation;
    public String projectionMode = "PINHOLE";
    public float fov = 90.0f;
    public String dof = "Infinity";
    public float focalOffset = 2.0f;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public me.engineersbox.playerrev.position getPosition() {
        return position;
    }

    public void setPosition(me.engineersbox.playerrev.position position) {
        this.position = position;
    }

    public me.engineersbox.playerrev.orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(me.engineersbox.playerrev.orientation orientation) {
        this.orientation = orientation;
    }

    public String getProjectionMode() {
        return projectionMode;
    }

    public void setProjectionMode(String projectionMode) {
        this.projectionMode = projectionMode;
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    public String getDof() {
        return dof;
    }

    public void setDof(String dof) {
        this.dof = dof;
    }

    public float getFocalOffset() {
        return focalOffset;
    }

    public void setFocalOffset(float focalOffset) {
        this.focalOffset = focalOffset;
    }
}
class CoordsObject {
	
    public Location Position1 = null;
    public Location Position2 = null;

    public Location getPosition1() {
        return Position1;
    }

    public void setPosition1(Location position1) {
        Position1 = position1;
    }

    public Location getPosition2() {
        return Position2;
    }

    public void setPosition2(Location position2) {
        Position2 = position2;
    }
}

public class Main extends JavaPlugin implements Listener {
	
	public static FileConfiguration config;
	public static File cfile;
	
	//Globals
	public static String prefix = ChatColor.RED + "[" + ChatColor.DARK_AQUA + "Player Reviewer" + ChatColor.RED + "] ";
	public static void InfoHeader(Player p, String info) {
		p.sendMessage("");
    	p.sendMessage(ChatColor.DARK_GRAY + "----=<{" + ChatColor.RED + "  [" + ChatColor.DARK_AQUA + info + ChatColor.RED + "]  " + ChatColor.DARK_GRAY + "}>=----");
    	p.sendMessage("");
	}
	public static boolean useConfigRanks;
	public static boolean useRanksInApplication;
	public static String configRankString;
	public static DynamicEnum<String, List<String>> ranksEnum;
	public static boolean UseSQL;
	public static MySQL MySQL;
	static Connection c = null;
	public static PlotAPI plotapi;
	public static boolean usePlotLoc = false;
	public static LuckPermsApi LPapi;
	public static String rankPlugin;
	
	double magicChunkyNumber = 57.29577951308232;
	public static Map<String, CoordsObject> positions = new HashMap<String, CoordsObject>();
    public static MaxSizeHashMap<String, CameraObject> cameras;
    public static int camCount = 0;
	
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
        	usePlotLoc = SQLConfig.usePlotLoc();
        } else {
        	Bukkit.getLogger().log(null, "[PlayerReviewer] Could not find PlotSquared! Reverting To Player Positions");
            return;
        }
    	UseSQL = SQLConfig.SQLEnabled();
    	if (UseSQL == true) {
    		MySQL = new MySQL(SQLConfig.getHOSTNAME(), "", SQLConfig.getDATABASE(), SQLConfig.getUSER(), SQLConfig.getPASS());
        	try {
    			c = MySQL.openConnection();
    		} catch (SQLException | ClassNotFoundException e) {
    			e.printStackTrace();
    		}
    	}
    	
    	cameras = new MaxSizeHashMap<String, CameraObject>(SQLConfig.maxCamCount());
    	SQLConfig.InitRankConfig();
    	
        getCommand("pr").setExecutor(new Commands());
        getCommand("pr help").setExecutor(new Commands());
        getCommand("pr apply").setExecutor(new Commands());
        getCommand("pr validranks").setExecutor(new Commands());
        getCommand("pr rate").setExecutor(new Commands());
        getCommand("pr gotoplot").setExecutor(new Commands());
        getCommand("pr ratings").setExecutor(new Commands());
        getCommand("pr approval").setExecutor(new Commands());
        getCommand("pr removeapplication").setExecutor(new Commands());
        getCommand("pr version").setExecutor(new Commands());
        getCommand("pr pos1").setExecutor(new Commands());
        getCommand("pr pos2").setExecutor(new Commands());
        getCommand("pr cam").setExecutor(new Commands());
        getCommand("pr get").setExecutor(new Commands());
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
    
}