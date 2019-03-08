package me.engineersbox.playerrev.updater;

import org.bukkit.Bukkit;

public class Updaters {

	public static void checkVersion(SpigotUpdater updater) {
		
    	Bukkit.getLogger().info("[Player Reviewer] Checking for updates...");
    	try {
			if (Class.forName("org.spigotmc.SpigotConfig") != null) {
				if (updater.checkForUpdates()) {
					Bukkit.getLogger().info("[Player Reviewer] An update was found! New version: " + updater.getLatestVersion() + " Download: " + updater.getResourceURL());
				}
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			Bukkit.getLogger().warning("[Player Reviewer] Could not check for updates! Stacktrace:");
			e.printStackTrace();
		}
    	
    }
	
}
