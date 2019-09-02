package me.engineersbox.playerrev.gitlab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDateTime;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.github.intellectualsites.plotsquared.plot.object.PlotPlayer;

import me.engineersbox.playerrev.Main;
import me.engineersbox.playerrev.methodlib.JSONMessage;
import me.engineersbox.playerrev.methodlib.Lib;

public class GitLib {
	
	public static void renderQuery(Player p) {
		JSONMessage.create(Main.prefix)
		.then("Would you like to get a render of your build? ")
			.color(ChatColor.AQUA)
		.then(" [").color(ChatColor.GRAY)
		.then("YES")
			.color(ChatColor.GREEN)
			.tooltip(JSONMessage.create("Show the steps to create a new render of your build")
								.color(ChatColor.GOLD))
			.runCommand("/pr renderhelp")
		.then("][").color(ChatColor.GRAY)
		.then("NO")
			.color(ChatColor.RED)
			.tooltip(JSONMessage.create("Don't create a new render")
								.color(ChatColor.GOLD))
			.runCommand("/tellraw " + p.getName() + " [\"\",{\"text\":\"[\",\"color\":\"red\"},{\"text\":\"Player Reviewer\",\"color\":\"dark_aqua\"},{\"text\":\"]\",\"color\":\"red\"},{\"text\":\" Ignoring chunky render field\",\"color\":\"aqua\"}]")
		.then("]").color(ChatColor.GRAY)
		.send(p);
	}
	
	public static void issueInit(Player p, String coordsstring, String rankName) {
		String description = "";
		Main.now = LocalDateTime.now();
		PlotPlayer player = PlotPlayer.wrap(p);
		
		if (Main.usePlotLoc) {
			try {
				coordsstring = Lib.getCoordsString(Lib.playerOwnsPlot(player, player.getApplicablePlotArea().getPlot(player.getLocation())));
			} catch (Exception se) {
				coordsstring = Lib.getCoordsString(p.getLocation());
			}
		} else {
			coordsstring = Lib.getCoordsString(p.getLocation());
		}
		
		description += "%2A%2APlayer Name%2A%2A: " + p.getName() + "%3C%62%72%2F%3E";
		description += "%2A%2APlayer UUID%2A%2A: " + p.getUniqueId().toString() + "%3C%62%72%2F%3E";
		description += "%2A%2ADate Time%2A%2A: " + Main.dtf.format(Main.now) + "%3C%62%72%2F%3E";
		description += "%2A%2ABuild Coordinates%2A%2A: " + coordsstring + "%3C%62%72%2F%3E";
		description += "%2A%2ARank%2A%2A: " + rankName.toLowerCase() + "%3C%62%72%2F%3E";
		description += "%2A%2AChunky Render%2A%2A: " + false + "%3C%62%72%2F%3E";
		description += "%2A%2ABuild Warp%2A%2A: %60/tp " + Lib.getLoc(coordsstring).getWorld().getName() + " " + Lib.getLoc(coordsstring).getX() + " " + Lib.getLoc(coordsstring).getY() + " " + (Lib.getLoc(coordsstring).getZ() + 1) + "%60";
		description = description.replaceAll("\\s", "%20").replaceAll("\\.", "%2E").replaceAll("\\@", "%40").replaceAll("\\:", "%3A").replaceAll("\\-", "%2D");
		try {
			GitLabManager.addIssue(p, "Application for " + p.getName() + " %5B" + (GitConfig.getIssueCount(p.getUniqueId()) + 1) + "%5D", description);
			GitConfig.addIssueId(p.getUniqueId(), GitLabManager.getIssueID("Application for " + p.getName() + " %5B" + (GitConfig.getIssueCount(p.getUniqueId()) + 1)  + "%5D"));
		} catch (IOException se) {
			p.sendMessage(Main.prefix + ChatColor.RED + "An error occured while creating a GitLab issue, please contact an administrator");
			Bukkit.getLogger().info("[Player Reviewer] GitLab issue creator: unknown error");
		} catch (IllegalArgumentException se) {
			Bukkit.getLogger().info("[Player Reviewer] Maximum application count reached for player " + p.getName() + " with UUID " + p.getUniqueId());
			p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Maximum submission count reached!");
		}
	}
	
	private static String getPathNamespace() {
		try {
			String geturl = GitConfig.getGitAddress()
					+ "/api/v4/projects/"
					+ GitConfig.getProjectID();
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
				return StringUtils.substringBetween(response.toString(), "\"path_with_namespace\":", ",\"created_at\"").replaceAll("\"", "");
			}
		} catch (IOException e) {
			Bukkit.getLogger().info("[Player Reviewer] GitLab issue creator: issue with id \"" + GitConfig.getProjectID() + "\" does not exist");
			return "";
		}
	}
	
	public static String issueLink(String id) {
		return GitConfig.getGitAddress() + "/" + getPathNamespace() + "/issues/" + id;
	}

}
