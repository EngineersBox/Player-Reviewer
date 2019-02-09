package me.engineersbox.playerrev;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

import me.engineersbox.playerrev.InvConfig;
import me.engineersbox.playerrev.mysql.MySQL;
import me.engineersbox.playerrev.mysql.SQLConfig;

public class Main extends JavaPlugin implements Listener {
	
	public static FileConfiguration config;
	public static File cfile;
	
	//Globals
	public static String prefix = ChatColor.RED + "[" + ChatColor.DARK_AQUA + "Player Reviewer" + ChatColor.RED + "] ";
	public static void InfoHeader(Player p, String info) {
		p.sendMessage("");
    	p.sendMessage(ChatColor.DARK_GRAY + "----={<" + ChatColor.RED + "  [" + ChatColor.DARK_AQUA + info + ChatColor.RED + "]  " + ChatColor.DARK_GRAY + "}>=----");
    	p.sendMessage("");
	}
	MySQL MySQL;
	static Connection c = null;
	
    public void onEnable() {
    	
    	if (!getDataFolder().exists()) {
    		getDataFolder().mkdirs();
    	}
    	
    	new InvConfig(this);
    	Bukkit.getServer().getPluginManager().registerEvents(this, this);
    	MySQL = new MySQL(SQLConfig.getHOSTNAME(), "", SQLConfig.getDATABASE(), SQLConfig.getUSER(), SQLConfig.getPASS());
    	
    	try {
			c = MySQL.openConnection();
		} catch (ClassNotFoundException e) {
			Bukkit.getLogger().warning(e.getMessage());
			e.printStackTrace();
		} catch (SQLException e) {
			Bukkit.getLogger().warning(e.getMessage());
			e.printStackTrace();
		}
        
        getCommand("pr").setExecutor(new Commands());
        getCommand("pr help").setExecutor(new Commands());
        getCommand("pr apply").setExecutor(new Commands());
        getCommand("pr validranks").setExecutor(new Commands());
        getCommand("pr rate").setExecutor(new Commands());
        getCommand("pr viewratings").setExecutor(new Commands());
        getCommand("pr approval").setExecutor(new Commands());
        getCommand("pr removeapplication").setExecutor(new Commands());
        getCommand("pr version").setExecutor(new Commands());
    }
    
    @Override
    public void onDisable() {
    	AbstractFile.saveConfig();
    	try {
			MySQL.closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
}