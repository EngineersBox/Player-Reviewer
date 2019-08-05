package me.engineersbox.playerrev.gitlab;

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

}
