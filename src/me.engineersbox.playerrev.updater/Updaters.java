package me.engineersbox.playerrev.updater;

import org.bukkit.Bukkit;

public class Updaters {

	public static void checkVersion(SpigotUpdater updater) {
		
    	Bukkit.getLogger().info("[Player Reviewer] Checking for updates...");
    	try {
			if (updater.checkForUpdates()) {
				Bukkit.getLogger().info("[Player Reviewer] An update was found! New version: " + updater.getLatestVersion() + " Download: " + updater.getResourceURL());
			} else {
				Bukkit.getLogger().info("[Player Reviewer] Plugin is up to date");
			}
		} catch (Exception e) {
			Bukkit.getLogger().warning("[Player Reviewer] Could not check for updates! Stacktrace:");
			e.printStackTrace();
		}
    	
    }
	
}
