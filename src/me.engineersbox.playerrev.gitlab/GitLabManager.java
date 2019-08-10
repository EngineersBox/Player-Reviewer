package me.engineersbox.playerrev.gitlab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.engineersbox.playerrev.Main;
import me.engineersbox.playerrev.enums.GitLabStatusCodes;
import me.engineersbox.playerrev.mysql.Config;

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
	
	public static String getIssueDescription(String title) {
		try {
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
				return StringUtils.substringBetween(response.toString(), "\"description\":", ",\"state\"");
			}
		} catch (IOException e) {
			Bukkit.getLogger().info("[Player Reviewer] GitLab issue creator: issue with title \"" + title + "\" does not exist");
			return "";
		}
	}
	
	public static String getUpdateTime(Player p) {
		try {
			String geturl = GitConfig.getRenderLink().replaceAll("\\<UUID\\>", p.getUniqueId().toString())
					+ GitConfig.getRenderQuery();
			URL obj = new URL(geturl);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			
			con.setRequestMethod("GET");
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			String updated_on = null;
			
			while ((inputLine = in.readLine()) != null) {
				if (inputLine.contains("sys_updated_on")) {
					updated_on = StringUtils.substringBetween("\"sys_updated_on\": \"", "\",");
				}
			}
			
			return updated_on;
		} catch (IOException e) {
			Bukkit.getLogger().info("[Player Reviewer] GitLab issue creator: could not access \"sys_updated_on\" field");
			return "";
		}
	}
	
	public static List<String> getRenderLinks(Player p) throws IOException {
		String geturl = GitConfig.getRenderLink().replaceAll("\\<UUID\\>", p.getUniqueId().toString())
				+ GitConfig.getRenderQuery();
		URL obj = new URL(geturl);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		
		con.setRequestMethod("GET");
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		
		List<String> camList = new ArrayList<>();
		String created_on = "";
		String updated_on = "";
		
		while ((inputLine = in.readLine()) != null) {
			if (inputLine.contains("sys_updated_on")) {
				updated_on = StringUtils.substringBetween("\"sys_updated_on\": \"", "\",");
			} else if (inputLine.contains("sys_created_on")) {
				created_on = StringUtils.substringBetween("\"sys_created_on\": \"", "\",");
				continue;
			}
			if (created_on.equals(updated_on) && (!created_on.equals("") && !updated_on.equals(""))) {
				int[] camCount = IntStream.range(1, Config.maxCamCount()).toArray();
				for (int c : camCount) {
					if (inputLine.contains("\"cam_" + c + "\"")) {
						camList.add(StringUtils.substringBetween("\"cam_" + c + "\": \"", "\","));
					}
				}
			} else {
				Main.renderChecks.put(p.getUniqueId(), new String[]{updated_on, created_on});
				break;
			}
		}
		
		in.close();
		con.disconnect();
		
		return camList;
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
			openIssue(p, title, description);
		}
		
	}
	
	private static void openIssue(Player p, String title, String description) throws IOException {
		boolean response = checkIssueExists(title);
		if (response) {
			String posturl = GitConfig.getGitAddress()
					+ "/api/v4/projects/"
					+ GitConfig.getProjectID() + "/"
					+ "issues/" + getIssueID(title.replaceAll("\\s", "%20"))
					+ "?state_event=reopen&description=" + description;
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
		} else {
			Bukkit.getLogger().info("[Player Reviewer] GitLab issue creator: issue does not exit, cannot delete");
		}
	}
	
	private static void closeIssue(Player p, String title) throws IOException {
		String posturl = GitConfig.getGitAddress()
				+ "/api/v4/projects/"
				+ GitConfig.getProjectID() + "/"
				+ "issues/" + getIssueID(title.replaceAll("\\s", "%20"))
				+ "?state_event=close";
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
			
			if (responseCode == 403) {
				closeIssue(p, title);
			} else {
				Bukkit.getLogger().info(
						GitLabStatusCodes.valueOf("S" + responseCode) != null ?
						GitLabStatusCodes.valueOf("S" + responseCode).getResponse()
						: "[Player Reviewer] GitLab issue creator: unknown error");
			}
		} else {
			Bukkit.getLogger().info("[Player Reviewer] GitLab issue creator: issue does not exit, cannot delete");
		}
	}
	
	public static void editIssue(Player p, String title, String desc) {
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
