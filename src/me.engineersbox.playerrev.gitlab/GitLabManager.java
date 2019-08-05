package me.engineersbox.playerrev.gitlab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.engineersbox.playerrev.enums.GitLabStatusCodes;

public class GitLabManager {
	
	public static boolean checkIssueExists(String title) throws IOException {
		String geturl = GitConfig.getGitAddress()
				+ "/api/v4/projects/"
				+ GitConfig.getProjectID() + "/"
				+ "issues?search=" + title.replaceAll("\\s", "%20");
		URL obj = new URL(geturl);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		
		con.setRequestMethod("GET");
		con.setRequestProperty("PRIVATE-TOKEN", GitConfig.getAccesskey());
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		
		in.close();
		con.disconnect();

		return !response.toString().equals("[]");
	}
	
	private static String getIssueID(String title) throws IOException {
		String geturl = GitConfig.getGitAddress()
				+ "/api/v4/projects/"
				+ GitConfig.getProjectID() + "/"
				+ "issues?search=" + title.replaceAll("\\s", "%20");
		URL obj = new URL(geturl);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		
		con.setRequestMethod("GET");
		con.setRequestProperty("PRIVATE-TOKEN", GitConfig.getAccesskey());
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		
		in.close();
		con.disconnect();
		
		if (response.toString().equals("[]")) {
			return "";
		} else {
			return StringUtils.substringBetween(response.toString(), "\"iid\":", ",\"project_id\"");
		}
	}
	
	public static void addIssue(Player p, String title, String description) throws IOException {
		
		boolean response = checkIssueExists(title);
		if (!response) {
			String posturl = GitConfig.getGitAddress()
					+ "/api/v4/projects/"
					+ GitConfig.getProjectID() + "/"
					+ "issues?title=" + title.replaceAll("\\s", "%20")
					+ "&description=" + description
					+ "&labels=request";
			URL obj = new URL(posturl);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			con.setRequestMethod("POST");
			con.setRequestProperty("PRIVATE-TOKEN", GitConfig.getAccesskey());
			con.setDoOutput(true);

			int responseCode = con.getResponseCode();
			con.disconnect();
			
			Bukkit.getLogger().info(
					GitLabStatusCodes.valueOf("S" + responseCode) != null ?
					GitLabStatusCodes.valueOf("S" + responseCode).getResponse()
					: "[Player Reviewer] GitLab issue creator: unknown error");
		} else {
			Bukkit.getLogger().info("[Player Reviewer] GitLab issue creator: issue already exists, skipping creation");
		}
		
	}
	
	public static void removeIssue(Player p, String title) throws IOException {
		boolean response = checkIssueExists(title);
		if (response) {
			String delurl = GitConfig.getGitAddress()
					+ "/api/v4/projects/"
					+ GitConfig.getProjectID() + "/"
					+ "issues/" + getIssueID(title.replaceAll("\\s", "%20"));
			URL obj = new URL(delurl);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			con.setRequestMethod("DELETE");
			con.setRequestProperty("PRIVATE-TOKEN", GitConfig.getAccesskey());
			con.setDoOutput(true);

			int responseCode = con.getResponseCode();
			con.disconnect();
			
			Bukkit.getLogger().info(
					GitLabStatusCodes.valueOf("S" + responseCode) != null ?
					GitLabStatusCodes.valueOf("S" + responseCode).getResponse()
					: "[Player Reviewer] GitLab issue creator: unknown error");
		} else {
			Bukkit.getLogger().info("[Player Reviewer] GitLab issue creator: issue does not exit, cannot delete");
		}
	}
	
	public static void editIssue(String title, String desc) {
		try {
			String posturl = GitConfig.getGitAddress()
					+ "/api/v4/projects/"
					+ GitConfig.getProjectID() + "/"
					+ "issues/" + getIssueID(title.replaceAll("\\s", "%20"))
					+ "?description=" + desc.replace("false", "true");
			URL obj = new URL(posturl);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			con.setRequestMethod("PUT");
			con.setRequestProperty("PRIVATE-TOKEN", GitConfig.getAccesskey());
			con.setDoOutput(true);

			int responseCode = con.getResponseCode();
			con.disconnect();
			
			Bukkit.getLogger().info(
					GitLabStatusCodes.valueOf("S" + responseCode) != null ?
					GitLabStatusCodes.valueOf("S" + responseCode).getResponse()
					: "[Player Reviewer] GitLab issue creator: unknown error");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
