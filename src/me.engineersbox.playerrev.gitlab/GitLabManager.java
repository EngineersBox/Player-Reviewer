package me.engineersbox.playerrev.gitlab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.engineersbox.playerrev.enums.GitLabStatusCodes;

public class GitLabManager {
	
	private static int sendRequestWithResponse(String url, String method, String token) throws IOException {
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		
		con.setRequestMethod(method);
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		con.setRequestProperty("PRIVATE-TOKEN", token);
		con.setDoOutput(true);
		
		int responseCode = con.getResponseCode();
		con.disconnect();
		return responseCode;
	}
	
	private static StringBuffer sendRequestWithStringResponse(String url, String method, String token) throws IOException {
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		
		con.setRequestMethod(method);
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		con.setRequestProperty("PRIVATE-TOKEN", token);
		con.setDoOutput(true);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		
		con.disconnect();
		return response;
	}
	
	public static boolean checkIssueExists(String title) throws IOException {
		String url = GitConfig.getGitAddress()
				+ "/api/v4/projects/"
				+ GitConfig.getProjectID() + "/"
				+ "issues?search=" + title.replaceAll("\\s", "%20");
		
		StringBuffer response = sendRequestWithStringResponse(url, "GET",  GitConfig.getAccesskey());

		return !response.toString().equals("[]");
	}
	
	public static boolean checkIssueExistsId(String id) throws IOException {
		String url = GitConfig.getGitAddress()
				+ "/api/v4/projects/"
				+ GitConfig.getProjectID() + "/"
				+ "issues/" + id;
		
		StringBuffer response = sendRequestWithStringResponse(url, "GET",  GitConfig.getAccesskey());

		return !response.toString().equals("{\"message\":\"404 Not found\"}");
	}
	
	public static String getIssueID(String title) throws IOException {
		String url = GitConfig.getGitAddress()
				+ "/api/v4/projects/"
				+ GitConfig.getProjectID() + "/"
				+ "issues?search=" + title.replaceAll("\\s", "%20");

		StringBuffer response = sendRequestWithStringResponse(url, "GET", GitConfig.getAccesskey());
		
		if (response.toString().equals("[]")) {
			return "";
		} else {
			return StringUtils.substringBetween(response.toString(), "\"iid\":", ",\"project_id\"");
		}
	}
	
	public static String getIssueDescription(String title) {
		try {
			String url = GitConfig.getGitAddress()
					+ "/api/v4/projects/"
					+ GitConfig.getProjectID() + "/"
					+ "issues?search=" + title.replaceAll("\\s", "%20");
			
			StringBuffer response = sendRequestWithStringResponse(url, "GET",  GitConfig.getAccesskey());
			
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
	
	public static String getUpdateTime(java.util.UUID id) {
		try {
			String url = GitConfig.getRenderLink().replaceAll("\\<UUID\\>", id.toString()) + GitConfig.getRenderQuery();
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
			
			con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
			con.setRequestMethod("GET");
			con.setDoOutput(true);
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			String updated_on = "";
			boolean isDone = false;
			
			while ((inputLine = in.readLine()) != null) {
				if (inputLine.contains("\"status\": \"Done\"")) {
					isDone = true;
				}
				if (inputLine.contains("sys_updated_on")) {
					updated_on = StringUtils.substringBetween(inputLine, "\"sys_updated_on\": \"", "\",");
				}
			}
			
			in.close();
			con.disconnect();
			if (isDone) {
				return updated_on;
			}
			return "";
		} catch (IOException e) {
			Bukkit.getLogger().info("[Player Reviewer] GitLab issue creator: could not access \"sys_updated_on\" field");
			return "";
		}
	}
	
	public static boolean compareDateTime(String date_one, String date_two) {
		if (date_two.equals("")) {
			return false;
		}
		LocalDateTime d_one = LocalDateTime.parse(date_one.replace(" ", "T") + ".000");
		LocalDateTime d_two = LocalDateTime.parse(date_two.replace(" ", "T") + ".000");
		return d_two.isAfter(d_one);
	}
	
	public static List<String> getRenderLinks(java.util.UUID id) throws IOException {
		String url = GitConfig.getRenderLink().replaceAll("\\<UUID\\>", id.toString().toString())
				+ "?" + GitConfig.getRenderQuery().replace("?", "");
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
		
		con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
		con.setRequestMethod("GET");
		con.setDoOutput(true);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		List<String> camList = new ArrayList<>();
		boolean isDone = false;
		int cCount = 1;
		
		while ((inputLine = in.readLine()) != null) {
			if (inputLine.contains("\"status\": \"Done\"")) {
				isDone = true;
			}
			if (isDone) {
				if (inputLine.contains("\"cam_" + cCount + "\"")) {
					String sub = StringUtils.substringBetween(inputLine, "\"cam_" + cCount + "\": \"", "\",");
					if (sub != null) { 
						camList.add(sub);
					}
					cCount++;
				}
			}
		}
		
		in.close();
		con.disconnect();
		
		return camList;
	}
	
	public static void addIssue(Player p, String title, String description) throws IOException {
		boolean response = checkIssueExists(title);
		if (!response) {
			String url = GitConfig.getGitAddress()
					+ "/api/v4/projects/"
					+ GitConfig.getProjectID() + "/"
					+ "issues?title=" + title.replaceAll("\\s", "%20")
					+ "&description=" + description
					+ "&labels=request";

			int responseCode = sendRequestWithResponse(url, "POST", GitConfig.getAccesskey());
			
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
			String url = GitConfig.getGitAddress()
					+ "/api/v4/projects/"
					+ GitConfig.getProjectID() + "/"
					+ "issues/" + getIssueID(title.replaceAll("\\s", "%20"))
					+ "?state_event=reopen&description=" + description;
			
			int responseCode = sendRequestWithResponse(url, "PUT", GitConfig.getAccesskey());
			
			Bukkit.getLogger().info(
					GitLabStatusCodes.valueOf("S" + responseCode) != null ?
					GitLabStatusCodes.valueOf("S" + responseCode).getResponse()
					: "[Player Reviewer] GitLab issue creator: unknown error");
		} else {
			Bukkit.getLogger().info("[Player Reviewer] GitLab issue creator: issue does not exit, cannot delete");
		}
	}
	
	private static void closeIssue(Player p, String title) throws IOException {
		boolean response = checkIssueExists(title);
		if (response) {
			String url = GitConfig.getGitAddress()
					+ "/api/v4/projects/"
					+ GitConfig.getProjectID() + "/"
					+ "issues/" + getIssueID(title.replaceAll("\\s", "%20"))
					+ "?state_event=close";
			
			int responseCode = sendRequestWithResponse(url, "PUT", GitConfig.getAccesskey());
			
			Bukkit.getLogger().info(
					GitLabStatusCodes.valueOf("S" + responseCode) != null ?
					GitLabStatusCodes.valueOf("S" + responseCode).getResponse()
					: "[Player Reviewer] GitLab issue creator: unknown error");
		} else {
			Bukkit.getLogger().info("[Player Reviewer] GitLab issue creator: issue does not exit, cannot delete");
		}
	}
	
	private static void closeIssueId(Player p, String id) throws IOException {
		boolean response = checkIssueExistsId(id);
		if (response) {
			String url = GitConfig.getGitAddress()
					+ "/api/v4/projects/"
					+ GitConfig.getProjectID() + "/"
					+ "issues/" + id
					+ "?state_event=close";
			
			int responseCode = sendRequestWithResponse(url, "PUT", GitConfig.getAccesskey());
			
			Bukkit.getLogger().info(
					GitLabStatusCodes.valueOf("S" + responseCode) != null ?
					GitLabStatusCodes.valueOf("S" + responseCode).getResponse()
					: "[Player Reviewer] GitLab issue creator: unknown error");
		} else {
			Bukkit.getLogger().info("[Player Reviewer] GitLab issue creator: issue does not exit, cannot delete");
		}
	}
	
	public static void removeIssue(Player p, String title) throws IOException {
		boolean response = checkIssueExists(title);
		if (response) {
			String url = GitConfig.getGitAddress()
					+ "/api/v4/projects/"
					+ GitConfig.getProjectID() + "/"
					+ "issues/" + getIssueID(title.replaceAll("\\s", "%20"));

			int responseCode = sendRequestWithResponse(url, "DELETE", GitConfig.getAccesskey());
			
			if (responseCode == 403) {
				closeIssue(p, title);
			} else {
				Bukkit.getLogger().info(
						GitLabStatusCodes.valueOf("S" + responseCode) != null ?
						GitLabStatusCodes.valueOf("S" + responseCode).getResponse()
						: "[Player Reviewer] GitLab issue creator: unknown error");
			}
		} else {
			Bukkit.getLogger().info("[Player Reviewer] GitLab issue creator: issue does not exist, cannot delete");
		}
	}
	
	public static void removeIssueById(Player p, String id) throws IOException {
		boolean response = checkIssueExistsId(id);
		if (response) {
			String url = GitConfig.getGitAddress()
					+ "/api/v4/projects/"
					+ GitConfig.getProjectID() + "/"
					+ "issues/" + id;
			
			int responseCode = sendRequestWithResponse(url, "DELETE", GitConfig.getAccesskey());
			
			if (responseCode == 403) {
				closeIssueId(p, id);
			} else {
				Bukkit.getLogger().info(
						GitLabStatusCodes.valueOf("S" + responseCode) != null ?
						GitLabStatusCodes.valueOf("S" + responseCode).getResponse()
						: "[Player Reviewer] GitLab issue creator: unknown error");
			}
		} else {
			Bukkit.getLogger().info("[Player Reviewer] GitLab issue creator: issue does not exist, cannot delete");
		}
	}
	
	public static void editIssue(Player p, String title, String desc) {
		try {
			String url = GitConfig.getGitAddress()
					+ "/api/v4/projects/"
					+ GitConfig.getProjectID() + "/"
					+ "issues/" + getIssueID(title.replaceAll("\\s", "%20"))
					+ "?description=" + desc;

			int responseCode = sendRequestWithResponse(url, "PUT", GitConfig.getAccesskey());
			
			Bukkit.getLogger().info(
					GitLabStatusCodes.valueOf("S" + responseCode) != null ?
					GitLabStatusCodes.valueOf("S" + responseCode).getResponse()
					: "[Player Reviewer] GitLab issue creator: unknown error");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void editIssueTitle(Player p, String title, String id) {
		try {
			String url = GitConfig.getGitAddress()
					+ "/api/v4/projects/"
					+ GitConfig.getProjectID() + "/"
					+ "issues/" + id
					+ "?title=" + title.replaceAll("\\s", "%20");
			
			int responseCode = sendRequestWithResponse(url, "POST", GitConfig.getAccesskey());
			
			Bukkit.getLogger().info(
					GitLabStatusCodes.valueOf("S" + responseCode) != null ?
					GitLabStatusCodes.valueOf("S" + responseCode).getResponse()
					: "[Player Reviewer] GitLab issue creator: unknown error");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}
