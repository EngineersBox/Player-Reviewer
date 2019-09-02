package me.engineersbox.playerrev.gitlab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import me.engineersbox.playerrev.AbstractFile;
import me.engineersbox.playerrev.Main;

public class GitConfig extends AbstractFile {

	public GitConfig(Main main) {
		super(main, "applications.yml");
	}
	
	public static boolean useGitLab() {
		return config.getBoolean("GitLab-Config.Use-GitLab");
	}
	
	public static String getAccesskey() {
		return config.getString("GitLab-Config.Logins.Access-Token");
	}
	
	public static String getGitAddress() {
		return config.getString("GitLab-Config.Logins.Address");
	}
	
	public static int getProjectID() {
		return config.getInt("GitLab-Config.Logins.Project-ID");
	}
	
	public static String getRenderLink() {
		return config.getString("GitLab-Config.Logins.Render-Link");
	}
	
	public static String getRenderQuery() {
		return config.getString("GitLab-Config.Logins.Render-Link-Queries");
	}
	
	public static Map<UUID, String[]> getInProgressApps() {
		List<String> ipApps = config.getStringList("GitLab-Config.In-Progress-Apps");
		Map<UUID, String[]> apps = new HashMap<UUID, String[]>();
		ipApps.forEach(val -> {
			String[] vals = val.split(",");
			apps.put(UUID.fromString(vals[0]), new String[] {vals[1], vals[2]});
		});
		return apps;
	}
	
	public static void setInProgressApps(Map<UUID, String[]> apps) {
		List<String> ipApps = new ArrayList<>();
		apps.forEach((UUID id, String[] date) -> ipApps.add(id.toString() + "," + date[0] + "," + date[1]));
		config.set("GitLab-Config.In-Progress-Apps", ipApps);
		saveConfig();
	}
	
	public static int maxAppCount() {
		return config.getInt("Application-Settings.Max-Application-Count");
	}
	
	public static int getIssueCount(UUID player_id) {
		if (config.get("Player-Applications." + player_id) != null) {
			String ids = config.getString("Player-Applications." + player_id);
			if (ids.contains(",")) {
				String[] existing = config.getString("Player-Applications." + player_id).split(",");
				return existing.length;
			}
			return 1;
		}
		return 0;
	}
	
	public static List<String> getIssuesList(UUID player_id) {
		if (config.get("Player-Applications." + player_id) != null) {
			String ids = config.getString("Player-Applications." + player_id);
			if (ids.contains(",")) {
				String[] existing = config.getString("Player-Applications." + player_id).split(",");
				return Arrays.asList(existing);
			}
			return Arrays.asList(new String[]{ids});
		}
		return new ArrayList<String>();
	}
	
	public static void addIssueId(UUID player_id, String id) throws IllegalArgumentException {
		String existing = "";
		if (config.get("Player-Applications." + player_id) != null) {
			existing = config.getString("Player-Applications." + player_id) + ",";
			if (existing.split(",").length > maxAppCount()) {
				throw new IllegalArgumentException();
			}
		}
		config.set("Player-Applications." + player_id, existing + id);
		saveConfig();
	}
	
	public static void removeIssueId(UUID player_id, String id) throws IllegalArgumentException {
		if (config.get("Player-Applications." + player_id) != null) {
			List<String> existing = getIssuesList(player_id);
			if (existing.size() == 0) {
				throw new IllegalArgumentException();
			}
			String val = "";
			for (String s : existing) {
				if (!s.equals(id)) {
					val += s;
					if (existing.indexOf(s) != existing.size() - 1) {
						val += ",";
					}
				}
			}
			config.set("Player-Applications." + player_id, val);
			saveConfig();
		}
	}
	
	public static void removeAllIssues(UUID player_id) {
		if (config.get("Player-Applications." + player_id) != null) {
			config.set("Player-Applications." + player_id, null);
			saveConfig();
		}
	}

}
