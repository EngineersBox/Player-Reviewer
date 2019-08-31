package me.engineersbox.playerrev.gitlab;

import java.util.ArrayList;
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
	}

}
