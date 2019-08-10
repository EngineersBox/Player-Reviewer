package me.engineersbox.playerrev;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.github.intellectualsites.plotsquared.plot.object.PlotPlayer;
import com.google.gson.Gson;

import me.engineersbox.playerrev.chunky.CameraObject;
import me.engineersbox.playerrev.chunky.CoordsObject;
import me.engineersbox.playerrev.chunky.JSONParameter;
import me.engineersbox.playerrev.chunky.OrientationChunky;
import me.engineersbox.playerrev.chunky.PositionChunky;
import me.engineersbox.playerrev.enums.RankEnum;
import me.engineersbox.playerrev.enums.Status;
import me.engineersbox.playerrev.exceptions.ChunkyParameterException;
import me.engineersbox.playerrev.exceptions.FieldValueException;
import me.engineersbox.playerrev.exceptions.HashMapSizeOverflow;
import me.engineersbox.playerrev.exceptions.InvalidGroupException;
import me.engineersbox.playerrev.exceptions.PlotInheritanceException;
import me.engineersbox.playerrev.gitlab.GitConfig;
import me.engineersbox.playerrev.gitlab.GitLabManager;
import me.engineersbox.playerrev.methodlib.GroupPlugins;
import me.engineersbox.playerrev.methodlib.HoverText;
import me.engineersbox.playerrev.methodlib.JSONMessage;
import me.engineersbox.playerrev.methodlib.Lib;
import me.engineersbox.playerrev.methodlib.MaxSizeHashMap;
import me.engineersbox.playerrev.mysql.Config;
import me.engineersbox.playerrev.mysql.SQLLink;

public class Commands implements CommandExecutor, TabCompleter {
	
	public void renderHelp(Player p, String cmdPrefix) {
		Main.InfoHeader(p, "New Render Help");
		p.sendMessage(ChatColor.GRAY + "1. " + ChatColor.AQUA + "Select the corner chunks of your build with " + ChatColor.GOLD + "/" + cmdPrefix +" pos1" + ChatColor.AQUA + " and " + ChatColor.GOLD + "/"+ cmdPrefix + " pos2");
		p.sendMessage(ChatColor.GRAY + "2. " + ChatColor.AQUA + "Register up to " + ChatColor.GREEN + Config.maxCamCount() + ChatColor.AQUA + " camera positons for the screenshot generator with " + ChatColor.GOLD + "/" + cmdPrefix + " cam1 ... /" + cmdPrefix + " cam4");
		p.sendMessage(ChatColor.GRAY + "3. " + ChatColor.AQUA + "Set any custom chunky parameters (using JSON formatting) with " + ChatColor.GOLD + "/" + cmdPrefix + " set <json parameters>");
		p.sendMessage(ChatColor.GRAY + "-  " + ChatColor.AQUA + "If you want to clear all parameters, use " + ChatColor.GOLD + "/" + cmdPrefix + " clear");
		p.sendMessage(ChatColor.GRAY + "4. " + ChatColor.AQUA + "Once you are happy with the screenshot steup, use the command " + ChatColor.GOLD + "/" + cmdPrefix + " request" + ChatColor.AQUA + " to submit a request for a render");
		p.sendMessage(ChatColor.DARK_GRAY + "----[" + ChatColor.RED + "Additional Info" + ChatColor.DARK_GRAY + "]----");
		p.sendMessage(ChatColor.GRAY + "- " + ChatColor.AQUA + "You can check on your render requests with " + ChatColor.GOLD + "/" + cmdPrefix + " getrequests" + ChatColor.AQUA + ". Click on the messages with the 'done' status to direct you to the image of the render");
		p.sendMessage(ChatColor.GRAY + "- " + ChatColor.AQUA + "If you want to clear all parameters, use " + ChatColor.GOLD + "/" + cmdPrefix + " clear");
		Main.InfoHeader(p, "New Render Help");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof Player) {
			
			Player p = (Player) sender;
			String tHash = p.getUniqueId().toString();
			boolean useChunky = Config.useChunky();
			String rankName = "Ranks Disabled";
			String coordsstring = null;
			
			if ((cmd.getName().equalsIgnoreCase("pr")) && (p.hasPermission("pr.use"))) {
					
				if (args.length == 0) {
					
					p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Use: " + ChatColor.ITALIC + "/pr help" + ChatColor.RESET + ChatColor.LIGHT_PURPLE + " To View All Commands");
					
				} else if (args.length > 0) {
					
					//pr apply <rank>
					if (args[0].equalsIgnoreCase("apply")) {
						
						if (p.hasPermission("pr.apply")) {
							if (args.length <= 2) {
								if (Main.useRanksInApplication) {
									rankName = args[1];
								}
								
								if ((Main.useConfigRanks) && (!Main.useRanksInApplication)) {
									
									try {
										if (Main.UseSQL) {
											if (Main.useRanksInApplication) {
												if (Config.useChunky() && !Config.useExternalRenders()) {
													SQLLink.newApp(p, p.getName(), rankName.toLowerCase(),  Lib.playerJsonParams(p));
													p.sendMessage(Main.prefix + ChatColor.AQUA + "Application Submitted!");
												} else {
													SQLLink.newApp(p, p.getName(), rankName.toLowerCase(),  null);
													p.sendMessage(Main.prefix + ChatColor.AQUA + "Application Submitted!");
													if (Config.useExternalRenders()) {
														JSONMessage.create(Main.prefix)
																	.then("Would you like to get a render of your build? ")
																		.color(ChatColor.AQUA)
																	.then("[").color(ChatColor.GRAY)
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
												}
											} else {
												if (Config.useChunky() && !Config.useExternalRenders()) {
													SQLLink.newApp(p, p.getName(), null,  Lib.playerJsonParams(p));
													p.sendMessage(Main.prefix + ChatColor.AQUA + "Application Submitted!");
												} else {
													SQLLink.newApp(p, p.getName(), null,  null);
													p.sendMessage(Main.prefix + ChatColor.AQUA + "Application Submitted!");
													if (Config.useExternalRenders()) {
														JSONMessage.create(Main.prefix)
																	.then("Would you like to get a render of your build?")
																		.color(ChatColor.DARK_AQUA)
																	.then("[").color(ChatColor.GRAY)
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
												}
											}
										} else {
											if (Main.useRanksInApplication) {
												InvConfig.newApp(p, p.getName(), rankName.toString().toLowerCase());
												p.sendMessage(Main.prefix + ChatColor.AQUA + "Application Submitted!");
											} else {
												InvConfig.newApp(p, p.getName(), null);
												p.sendMessage(Main.prefix + ChatColor.AQUA + "Application Submitted!");
											}
										}
										
										if (GitConfig.useGitLab()) {
											String description = "";
											Main.now = LocalDateTime.now();
											PlotPlayer player = PlotPlayer.wrap(p);
											
											if (Main.usePlotLoc) {
												try {
													coordsstring = Lib.getCoordsString(Lib.playerOwnsPlot(player, player.getApplicablePlotArea().getPlot(player.getLocation())));
												} catch (Exception e) {
													coordsstring = Lib.getCoordsString(p.getLocation());
												}
											} else {
												coordsstring = Lib.getCoordsString(p.getLocation());
											}
											
											description += "%2A%2APlayer Name%2A%2A: " + p.getName() + "%3C%62%72%2F%3E";
											description += "%2A%2APlayer UUID%2A%2A: " + p.getUniqueId() + "%3C%62%72%2F%3E";
											description += "%2A%2ADate Time%2A%2A: " + Main.dtf.format(Main.now) + "%3C%62%72%2F%3E";
											description += "%2A%2ABuild Coordinates%2A%2A: " + coordsstring + "%3C%62%72%2F%3E";
											description += "%2A%2ARank%2A%2A: " + rankName.toLowerCase() + "%3C%62%72%2F%3E";
											description += "%2A%2AChunky Render%2A%2A: " + false + "%3C%62%72%2F%3E";
											description += "%2A%2ABuild Warp%2A%2A: %60/tp @p " + Lib.getLoc(coordsstring).getX() + " " + Lib.getLoc(coordsstring).getY() + " " + (Lib.getLoc(coordsstring).getZ() + 1) + "%60";
											description = description.replaceAll("\\s", "%20").replaceAll("\\.", "%2E").replaceAll("\\@", "%40").replaceAll("\\:", "%3A").replaceAll("\\-", "%2D");
											try {
												GitLabManager.addIssue(p, "Application for " + p.getName(), description);
											} catch (IOException e) {
												p.sendMessage(Main.prefix + ChatColor.RED + "An error occured while creating a GitLab issue, please contact an administrator");
												Bukkit.getLogger().info("[Player Reviewer] GitLab issue creator: unknown error");
											}
										}
										
										Main.appStatus.put(p.getUniqueId(), Status.AWAITING_REVIEW);
										
										
									} catch (SQLException | FieldValueException e) {
										p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Application For " + p.getDisplayName() + " Already Exists!");
									} catch (PlotInheritanceException e) {
										p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Plot Is Not Owned By Player: " + p.getDisplayName());
									} catch (ChunkyParameterException e) {
										if (e.toString().equalsIgnoreCase("me.engineersbox.playerrev.exceptions.ChunkyParameterException: camera")) {
											p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "No camera instance registered!");
											p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Register it with " + ChatColor.ITALIC + "/pr cam");
										} else if (e.toString().equalsIgnoreCase("me.engineersbox.playerrev.exceptions.ChunkyParameterException: position")) {
											p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "No build location registered!");
											p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Assign location with " + ChatColor.ITALIC + " /pr pos1 " + ChatColor.RESET + ChatColor.LIGHT_PURPLE + " and " + ChatColor.ITALIC + "/pr pos2");
										}
									}
								
								} else if ((Main.ranksEnum.isValid(rankName, Arrays.asList(rankName.toUpperCase()))) && (Main.useConfigRanks) && (Main.useRanksInApplication)) {
									
									try {
										if (Main.UseSQL) {
											if (Main.useRanksInApplication) {
												SQLLink.newApp(p, p.getName(), rankName.toLowerCase(),  Lib.playerJsonParams(p));
											} else {
												SQLLink.newApp(p, p.getName(), null, Lib.playerJsonParams(p));
											}
										} else {
											if (Main.useRanksInApplication) {
												InvConfig.newApp(p, p.getName(), rankName.toLowerCase());
											} else {
												InvConfig.newApp(p, p.getName(), null);
											}
										}
										
										p.sendMessage(Main.prefix + ChatColor.AQUA + "Application Submitted!");
										
										if (GitConfig.useGitLab()) {
											String description = "";
											Main.now = LocalDateTime.now();
											PlotPlayer player = PlotPlayer.wrap(p);
											
											if (Main.usePlotLoc) {
												try {
													coordsstring = Lib.getCoordsString(Lib.playerOwnsPlot(player, player.getApplicablePlotArea().getPlot(player.getLocation())));
												} catch (Exception e) {
													coordsstring = Lib.getCoordsString(p.getLocation());
												}
											} else {
												coordsstring = Lib.getCoordsString(p.getLocation());
											}
											
											description += "%2A%2APlayer Name%2A%2A: " + p.getName() + "%3C%62%72%2F%3E";
											description += "%2A%2APlayer UUID%2A%2A: " + p.getUniqueId() + "%3C%62%72%2F%3E";
											description += "%2A%2ADate Time%2A%2A: " + Main.dtf.format(Main.now) + "%3C%62%72%2F%3E";
											description += "%2A%2ABuild Coordinates%2A%2A: " + coordsstring + "%3C%62%72%2F%3E";
											description += "%2A%2ARank%2A%2A: " + rankName.toLowerCase() + "%3C%62%72%2F%3E";
											description += "%2A%2AChunky Render%2A%2A: " + false + "%3C%62%72%2F%3E";
											description += "%2A%2ABuild Warp%2A%2A: %60/tp @p " + Lib.getLoc(coordsstring).getX() + " " + Lib.getLoc(coordsstring).getY() + " " + (Lib.getLoc(coordsstring).getZ() + 1) + "%60";
											description = description.replaceAll("\\s", "%20").replaceAll("\\.", "%2E").replaceAll("\\@", "%40").replaceAll("\\:", "%3A").replaceAll("\\-", "%2D");
											try {
												GitLabManager.addIssue(p, "Application for " + p.getName(), description);
											} catch (IOException e) {
												p.sendMessage(Main.prefix + ChatColor.RED + "An error occured while creating a GitLab issue, please contact an administrator");
												Bukkit.getLogger().info("[Player Reviewer] GitLab issue creator: unknown error");
											}
										}
										
										Main.appStatus.put(p.getUniqueId(), Status.AWAITING_REVIEW);
										
									} catch (SQLException | FieldValueException e) {
										Bukkit.getLogger().info(e.toString());
										p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Application For " + p.getDisplayName() + " Already Exists!");
									} catch (PlotInheritanceException e) {
										p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Plot Is Not Owned By Player: " + p.getDisplayName());
									} catch (ChunkyParameterException e) {
										if (e.toString().equalsIgnoreCase("me.engineersbox.playerrev.exceptions.ChunkyParameterException: camera")) {
											p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "No camera instance registered!");
											p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Register it with " + ChatColor.ITALIC + "/pr cam");
										} else if (e.toString().equalsIgnoreCase("me.engineersbox.playerrev.exceptions.ChunkyParameterException: position")) {
											p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "No build location registered!");
											p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Assign location with " + ChatColor.ITALIC + " /pr pos1 " + ChatColor.RESET + ChatColor.LIGHT_PURPLE + " and " + ChatColor.ITALIC + "/pr pos2");
										}
									}
								
								} else {
									if (Main.useRanksInApplication) {
										p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Invalid Rank!");
										p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "View Valid Ranks With: " + ChatColor.ITALIC + "/pr validranks");
									}
									
								}
								
							} else {
								
								p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Invalid Syntax!");
								p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Usage: " + ChatColor.ITALIC + "/pr apply [<rank>]");
								
							}
						} else {
							p.sendMessage(Main.prefix + ChatColor.RED + "You do not have permission");
						}
						
					} else if(args[0].equalsIgnoreCase("help")) {
						
						if (p.hasPermission("pr.help")) {
							Main.InfoHeader(p, "Player Reviewer");
							try {
								Class.forName("net.md_5.bungee.api.chat.TextComponent");
								p.sendMessage(ChatColor.AQUA + "Hover over a command to see a description");
								HoverText.HoverMessage(p, "&0> &2/pr validranks", Arrays.asList("&6Description:", "&cDisplays All Ranks That Can Be Applied For"));
								HoverText.HoverMessage(p, "&0> &2/pr apply <rank>", Arrays.asList("&6Description:", "&cSubmit An Application For A Rank"));
								HoverText.HoverMessage(p, "&0> &2/pr removeapplication <name>", Arrays.asList("&6Description:", "&cRemove An Open Application"));
								HoverText.HoverMessage(p, "&0> &2/pr approval <player> <approve/deny>", Arrays.asList("&6Description:", "&cApprove Or Deny An Application"));
								HoverText.HoverMessage(p, "&0> &2/pr ratings <player>", Arrays.asList("&6Description:", "&cSubmit An Application For A Rank"));
								HoverText.HoverMessage(p, "&0> &2/pr rate <player> <criteria>", Arrays.asList("&6Description:", "&cSubmit A Rating To A Player's Open Application"));
								HoverText.HoverMessage(p, "&0> &2/pr gotoplot <player>", Arrays.asList("&6Description:", "&cTeleports To Player's Open Application Plot"));
								HoverText.HoverMessage(p, "&0> &2/pr version", Arrays.asList("&6Description:", "&cDisplays The Plugin Version And Author"));
								HoverText.HoverMessage(p, "&0> &2/pr help", Arrays.asList("&6Description:", "&cOpens This Menu"));
							} catch (ClassNotFoundException e) {
								
								p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "/pr validranks" + ChatColor.WHITE + " :: " + ChatColor.GOLD +  "Displays All Ranks That Can Be Applied For");
								p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "/pr apply <rank>" + ChatColor.WHITE + " :: " + ChatColor.GOLD + "Submit An Application For A Rank");
								p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "/pr removeapplication <name>" + ChatColor.WHITE + " :: " + ChatColor.GOLD +  "Remove An Open Application");
								p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "/pr approval <player> <approve/deny>" + ChatColor.WHITE + " :: " + ChatColor.GOLD +  "Approve Or Deny An Application");
								p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "/pr ratings <player>" + ChatColor.WHITE + " :: " + ChatColor.GOLD +  "Submit An Application For A Rank");
								p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "/pr rate <player> <criteria>" + ChatColor.WHITE + " :: " + ChatColor.GOLD +  "Submit A Rating To A Player's Open Application");
								p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "/pr gotoplot <player>" + ChatColor.WHITE + " :: " + ChatColor.GOLD +  "Teleports To Player's Open Application Plot");
								p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "/pr version" + ChatColor.WHITE + " :: " + ChatColor.GOLD +  "Displays The Plugin Version And Author");
								p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "/pr help" + ChatColor.WHITE + " :: " + ChatColor.GOLD +  "Opens This Menu");
							}
							Main.InfoHeader(p, "Player Reviewer");
						} else {
							p.sendMessage(Main.prefix + ChatColor.RED + "You do not have permission");
						}
						
					} else if (args[0].equalsIgnoreCase("renderhelp")) {
						
						if (p.hasPermission("pr.renderhelp") && Config.useExternalRenders()) {
							renderHelp(p, Config.externalRenderPrefix());
							try {
								if (GitConfig.useGitLab() && GitLabManager.checkIssueExists("Application%20for%20" + p.getName())) {
									String description = "";
									PlotPlayer player = PlotPlayer.wrap(p);
									List<String> renders = new ArrayList<>();
									String renderString = "%3C%62%72%2F%3E";
									renders = GitLabManager.getRenderLinks(p);
									
									if (renders.size() > 0) {
										int count = 1;
										for (String cRender : renders) {
											renderString += "%2D%20Cam_" + count + "%3A%20%" + cRender + "%3C%62%72%2F%3E";
										}
									} else {
										renderString = "rendering...%3C%62%72%2F%3E";
									}
									
									if (Main.usePlotLoc) {
										try {
											coordsstring = Lib.getCoordsString(Lib.playerOwnsPlot(player, player.getApplicablePlotArea().getPlot(player.getLocation())));
										} catch (Exception e) {
											coordsstring = Lib.getCoordsString(p.getLocation());
										}
									} else {
										coordsstring = Lib.getCoordsString(p.getLocation());
									}
									
									description += "%2A%2APlayer Name%2A%2A: " + p.getName() + "%3C%62%72%2F%3E";
									description += "%2A%2APlayer UUID%2A%2A: " + p.getUniqueId() + "%3C%62%72%2F%3E";
									description += "%2A%2ADate Time%2A%2A: " + Main.dtf.format(Main.now) + "%3C%62%72%2F%3E";
									description += "%2A%2ABuild Coordinates%2A%2A: " + coordsstring + "%3C%62%72%2F%3E";
									description += "%2A%2ARank%2A%2A: " + rankName.toLowerCase() + "%3C%62%72%2F%3E";
									description += "%2A%2AChunky Render%2A%2A: " + renderString;
									description += "%2A%2ABuild Warp%2A%2A: %60/tp @p " + Lib.getLoc(coordsstring).getX() + " " + Lib.getLoc(coordsstring).getY() + " " + (Lib.getLoc(coordsstring).getZ() + 1) + "%60";
									description = description.replaceAll("\\s", "%20").replaceAll("\\.", "%2E").replaceAll("\\@", "%40").replaceAll("\\:", "%3A").replaceAll("\\-", "%2D");
									
									GitLabManager.editIssue(p, "Application for " + p.getName(), description);
								}
							} catch (IOException e) {
								p.sendMessage(Main.prefix + ChatColor.RED + "An error occured while creating a GitLab issue, please contact an administrator");
								Bukkit.getLogger().info("[Player Reviewer] GitLab issue creator: unknown error");
							}
						} else {
							p.sendMessage(Main.prefix + ChatColor.RED + "You do not have permission");
						}
						
					} else if (args[0].equalsIgnoreCase("apphelp")) {
						
						if (p.hasPermission("pr.apphelp")) {
							Main.InfoHeader(p, "Application Submission Help");
							if (Main.useRanksInApplication) {
								if (useChunky) {
									p.sendMessage(ChatColor.GRAY + "1. " + ChatColor.AQUA + "Select the corner chunks of your build with " + ChatColor.GOLD + "/pr pos1" + ChatColor.AQUA + " and " + ChatColor.GOLD + " /pr pos2");
									p.sendMessage(ChatColor.GRAY + "2. " + ChatColor.AQUA + "Register up to " + ChatColor.GREEN + Config.maxCamCount() + ChatColor.AQUA + " camera positons for the screenshot generator with" + ChatColor.GOLD + "/pr cam");
									p.sendMessage(ChatColor.GRAY + "3. " + ChatColor.AQUA + "Set any custom chunky parameters (using JSON formatting) with " + ChatColor.GOLD + "/pr setparam <json parameters>");
									p.sendMessage(ChatColor.GRAY + "-  " + ChatColor.AQUA + "If you want to remove a parameter, use " + ChatColor.GOLD + "/pr removeparam <parameter name>");
									p.sendMessage(ChatColor.GRAY + "-  " + ChatColor.AQUA + "If you want to clear all parameters, use " + ChatColor.GOLD + "/pr clearparams");
									p.sendMessage(ChatColor.GRAY + "4. " + ChatColor.AQUA + "Once you are happy with the screenshot steup, use the command " + ChatColor.GOLD + "/pr apply <rank>" + ChatColor.AQUA + " to submit athe application");
								} else {
									p.sendMessage(ChatColor.GRAY + "1. " + ChatColor.AQUA + "Stand near your build and use the command " + ChatColor.GOLD + "/pr apply <rank>");
								}
							} else {
								if (useChunky) {
									p.sendMessage(ChatColor.GRAY + "1. " + ChatColor.AQUA + "Select the corner chunks of your build with " + ChatColor.GOLD + "/pr pos1" + ChatColor.AQUA + " and " + ChatColor.GOLD + "/pr pos2");
									p.sendMessage(ChatColor.GRAY + "2. " + ChatColor.AQUA + "Register up to " + ChatColor.GREEN + Config.maxCamCount() + ChatColor.AQUA + " camera positons for the screenshot generator with" + ChatColor.GOLD + "/pr cam");
									p.sendMessage(ChatColor.GRAY + "3. " + ChatColor.AQUA + "Set any custom chunky parameters (using JSON formatting) with " + ChatColor.GOLD + "/pr setparam <json parameters>");
									p.sendMessage(ChatColor.GRAY + "   " + ChatColor.AQUA + "If you want to remove a parameter, use " + ChatColor.GOLD + "/pr removeparam <parameter name>");
									p.sendMessage(ChatColor.GRAY + "   " + ChatColor.AQUA + "If you want to clear all parameters, use " + ChatColor.GOLD + "/pr clearparams");
									p.sendMessage(ChatColor.GRAY + "4. " + ChatColor.AQUA + "Once you are happy with the screenshot steup, use the command " + ChatColor.GOLD + "/pr apply" + ChatColor.AQUA + " to submit athe application");
								} else {
									p.sendMessage(ChatColor.GRAY + "1. " + ChatColor.AQUA + "Stand near your build and use the command " + ChatColor.GOLD + "/pr apply");
								}
							}
							Main.InfoHeader(p, "Application Submission Help");
						} else {
							p.sendMessage(Main.prefix + ChatColor.RED + "You do not have permission");
						}
						
					} else if ((args[0].equalsIgnoreCase("validranks")) | (args[0].equalsIgnoreCase("vr")) | (args[0].equalsIgnoreCase("ranks"))) {
						
						if (p.hasPermission("pr.validranks")) {
							Main.InfoHeader(p, "Player Reviewer Valid Ranks");
							if (!Main.useConfigRanks) {
								for (RankEnum re : RankEnum.values()) {
		        		    		String[] split;
		        		    		String normname = null;
		        		    		if (re.toString().contains("_")) {
		        		    			split = re.toString().split("_");
		        		    			normname = Lib.capFirstLetter(split[0]) + " " + Lib.capFirstLetter(split[1]);
		        		    			p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + normname + ChatColor.WHITE + " :: " + ChatColor.RED + re);
		        		    		} else {
		        		    			normname = Lib.capFirstLetter(re.toString());
		        		    			p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + normname + ChatColor.WHITE + " :: " + ChatColor.RED + re);
		        		    		}
		        		    		
		        		    	}
							} else {
								String[] rankSplit = Main.configRankString.split(",");
								for (String re : rankSplit) {
									String cString;
									if ((re.contains("[")) && (re.contains("]"))) {
										cString = re.substring(0, re.indexOf("["));
									} else {
										cString = re;
									}
		        		    		String[] split;
		        		    		String normname = null;
		        		    		if (cString.contains("_")) {
		        		    			split = cString.split("_");
		        		    			normname = Lib.capFirstLetter(split[0]) + " " + Lib.capFirstLetter(split[1]);
		        		    			p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + normname + ChatColor.WHITE + " :: " + ChatColor.RED + cString.toUpperCase());
		        		    		} else {
		        		    			normname = Lib.capFirstLetter(cString);
		        		    			p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + normname + ChatColor.WHITE + " :: " + ChatColor.RED + cString.toUpperCase());
		        		    		}
		        		    		
		        		    	}
							}
	        		    	Main.InfoHeader(p, "Player Reviewer Valid Ranks");
						} else {
							p.sendMessage(Main.prefix + ChatColor.RED + "You do not have permission");
						}
						
					//pr rate <player> <atmosphere> <originality> <terrain> <structure> <layout>
					} else if (args[0].equalsIgnoreCase("rate")) {
						
						if (p.hasPermission("pr.rate")) {
							if (Config.inGameRating()) {
								if (args.length == Config.getCriteria().size() + 2) {
									
									boolean successFlag = true;
									
									try {
										
										List<Integer> criteria = new ArrayList<Integer>();
										for (int i = 2; i < args.length; i++) {
											criteria.add(Lib.returnInRange(args[i]));
										}
										
										if (Main.UseSQL == true) {
											SQLLink.ratePlayer(p.getDisplayName(), args[1], criteria);
											p.sendMessage(Main.prefix + ChatColor.AQUA + "Rating For " + args[1] + "'s Application Submitted!");
										} else {
											InvConfig.ratePlayer(p.getDisplayName(), args[1], criteria);
											p.sendMessage(Main.prefix + ChatColor.AQUA + "Rating For " + args[1] + "'s Application Submitted!");
										}
										successFlag = false;
										
									} catch (NumberFormatException e) {
										
										p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Invalid Rating Value!");
										p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Values Must Be Integers, Not" +  e.toString().substring(e.toString().lastIndexOf(":") + 1));
										successFlag = false;
										
									} catch (SQLException | FieldValueException se) {
										
										p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Application For " + args[1] + " Does not Exist!");
										successFlag = false;
										
									}
									
									if (successFlag) {
										
										p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Invalid Rating Value!");
										p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Valid Values Are 0 - 100 Inclusive");
										
									}
									
								} else {
									
									p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Invalid Syntax!");
									p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Usage: " + ChatColor.ITALIC + "/rv rate <player> <criteria 1> <criteria 2> ...");
									
								}
							} else {
								p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Application rating in-game is disabled, enable it in the config");
							}
							
						} else {
							p.sendMessage(Main.prefix + ChatColor.RED + "You do not have permission");
						}
						
					} else if ((args[0].equalsIgnoreCase("gotoplot")) | (args[0].equalsIgnoreCase("plot"))) {
						
						if (p.hasPermission("pr.gotoplot")) {
							try {
								if (args[1] != null) {
									if (Main.UseSQL == true) {
										p.teleport(SQLLink.getPlotLocation(args[1]));
									} else {
										p.teleport(InvConfig.getPlotLocation(args[1]));
									}
								} else {
									p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Invalid syntax!");
								}
							} catch (SQLException | FieldValueException se) {
								p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Application Does Not Exist!");
							}
						} else {
							p.sendMessage(Main.prefix + ChatColor.RED + "You do not have permission");
						}
						
					//pr ratings <player>
					} else if (args[0].equalsIgnoreCase("ratings")) {
						
						if (Config.inGameRating()) {
							if ((args.length == 2) && ((args[1].equalsIgnoreCase(p.getDisplayName())) | (p.hasPermission("pr.ratings")))) {
								
								try {

									if (Main.UseSQL == true) {
										ArrayList<List<String>> Ratings = SQLLink.getRatingValues(args[1]);
										String appRank = Ratings.get(0).get(0);
										float cTotal = 0;
										
										p.sendMessage("");
										p.sendMessage(ChatColor.DARK_GRAY + "----={<" + ChatColor.RED + "  [" + ChatColor.DARK_AQUA + args[1] + " Ratings" + ChatColor.RED + "]  " + ChatColor.DARK_GRAY + "}>=----");
										p.sendMessage(ChatColor.LIGHT_PURPLE + "Format: <name> :: <criteria 1> <criteria 2> ...");
										p.sendMessage("");
										for (String ra : Ratings.get(1)) {
											String[] ratingValues = ra.split("-");
											p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + ratingValues[0] + " " + ChatColor.WHITE +  ":: " +  ChatColor.RED + ratingValues[1] + " " + ratingValues[2] + " " + ratingValues[3] + " " + ratingValues[4] + " " + ratingValues[5]);
										}
										p.sendMessage("");
										p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "Averages " + ChatColor.WHITE + ":: " + ChatColor.RED + Ratings.get(0).get(1) + " " + Ratings.get(0).get(2) + " " + Ratings.get(0).get(3) + " " + Ratings.get(0).get(4) + " " + Ratings.get(0).get(5));
										for (int i = 1; i < 6; i++) {
											cTotal += Float.parseFloat(Ratings.get(0).get(i));
										}
										p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "Total Points " + ChatColor.WHITE + ":: " + ChatColor.RED + Float.toString(cTotal) + "/500");
										p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "Rank Applied For " + ChatColor.WHITE + ":: " + ChatColor.RED + appRank.substring(0, 1).toUpperCase() + appRank.substring(1));
										p.sendMessage(ChatColor.DARK_GRAY + "----={<" + ChatColor.RED + "  [" + ChatColor.DARK_AQUA + args[1] + " Ratings" + ChatColor.RED + "]  " + ChatColor.DARK_GRAY + "}>=----");
									} else {
										ArrayList<List<String>> Ratings = InvConfig.getRatings(args[1]);
										String appRank = Ratings.get(1).get(5);
										float cTotal = 0;
										
										p.sendMessage("");
										p.sendMessage(ChatColor.DARK_GRAY + "----={<" + ChatColor.RED + "  [" + ChatColor.DARK_AQUA + args[1] + " Ratings" + ChatColor.RED + "]  " + ChatColor.DARK_GRAY + "}>=----");
										p.sendMessage(ChatColor.LIGHT_PURPLE + "Format: <name> :: <criteria 1> <criteria 2> ...");
										p.sendMessage("");
										for (String ra : Ratings.get(0)) {
											String[] ratingValues = ra.split("-");
											p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + ratingValues[0] + " " + ChatColor.WHITE +  ":: " +  ChatColor.RED + ratingValues[1] + " " + ratingValues[2] + " " + ratingValues[3] + " " + ratingValues[4] + " " + ratingValues[5]);
										}
										p.sendMessage("");
										p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "Averages " + ChatColor.WHITE + ":: " + ChatColor.RED + Ratings.get(1).get(0) + " " + Ratings.get(1).get(1) + " " + Ratings.get(1).get(2) + " " + Ratings.get(1).get(3) + " " + Ratings.get(1).get(4));
										for (int i = 0; i < 5; i++) {
											cTotal += Float.parseFloat(Ratings.get(1).get(i));
										}
										p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "Total Points " + ChatColor.WHITE + ":: " + ChatColor.RED + Float.toString(cTotal) + "/500");
										p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "Rank Applied For " + ChatColor.WHITE + ":: " + ChatColor.RED + appRank);
										p.sendMessage(ChatColor.DARK_GRAY + "----={<" + ChatColor.RED + "  [" + ChatColor.DARK_AQUA + args[1] + " Ratings" + ChatColor.RED + "]  " + ChatColor.DARK_GRAY + "}>=----");
									}
									
									
								} catch (SQLException | FieldValueException e) {
									p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Application For " + args[1] + " Does Not Exist!");
								}
								
							} else {
								
								p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Invalid Syntax!");
								p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Usage: " + ChatColor.ITALIC + "/pr viewratings <name>");
								
							}
						} else {
							p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Application rating in-game is disabled, enable it in the config");
						}
						
					//pr approval <name> <approve/deny>	
					} else if (args[0].equalsIgnoreCase("approval")) {
						
						if (p.hasPermission("pr.approval")) {
							if (args.length == 3) {
								try {
									if (args[2].equalsIgnoreCase("approve")) {
										if (Main.UseSQL == true) {
											if (Main.useRanksInApplication) {
												String rank = SQLLink.getAppRank(args[1]);
												if (Main.rankPlugin.equalsIgnoreCase("pex")) {
													GroupPlugins.pexAssignGroup(Bukkit.getPlayer(args[1]), rank);
												} else if (Main.rankPlugin.equalsIgnoreCase("lp")) {
													GroupPlugins.lpAssignGroup(Bukkit.getPlayer(args[1]), rank);
												}
											}
											SQLLink.removeApp(args[1]);
										} else {
											if (Main.useRanksInApplication) {
												String rank = InvConfig.getAppRank(args[1]);
												if (Main.rankPlugin.equalsIgnoreCase("pex")) {
													GroupPlugins.pexAssignGroup(Bukkit.getPlayer(args[1]), rank);
												} else if (Main.rankPlugin.equalsIgnoreCase("lp")) {
													GroupPlugins.lpAssignGroup(Bukkit.getPlayer(args[1]), rank);
												}
											}
											InvConfig.removeApp(args[1]);
										}
										
										if (GitConfig.useGitLab()) {
											GitLabManager.removeIssue(p, "Application%20for%20" + p.getName());
										}
										
										if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[1]))) {
											Bukkit.getPlayer(args[1]).sendMessage(Main.prefix + ChatColor.GOLD + "Your application was " + ChatColor.GREEN + "approved");
											Main.appStatus.remove(Bukkit.getPlayer(args[1]).getUniqueId());
										} else {
											Main.appStatus.put(Bukkit.getPlayer(args[1]).getUniqueId(), Status.APPROVED);
										}
										
									} else if (args[2].equalsIgnoreCase("deny")) {
										
										if (Main.UseSQL == true) {
											SQLLink.removeApp(args[1]);
										} else {
											InvConfig.removeApp(args[1]);
										}
										
										if (GitConfig.useGitLab()) {
											GitLabManager.removeIssue(p, "Application%20for%20" + p.getName());
										}
										
										if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[1]))) {
											Bukkit.getPlayer(args[1]).sendMessage(Main.prefix + ChatColor.GOLD + "Your application was " + ChatColor.DARK_RED + "denied");
											Main.appStatus.remove(Bukkit.getPlayer(args[1]).getUniqueId());
										} else {
											Main.appStatus.put(Bukkit.getPlayer(args[1]).getUniqueId(), Status.DENIED);
										}
										
									} else {
										
										p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Invalid Command Syntax!");
										p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Usage: " + ChatColor.ITALIC + "/pr approval <name> <approve/deny>");
										
									}
									
								} catch (SQLException e) {
									p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Invalid Player Name: " + args[1]);
								} catch (InvalidGroupException e) {
									p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Invalid Rank In Application!");
								} catch (FieldValueException | ClassNotFoundException e) {
									p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Application For " + args[1] + " Does Not Exist!");
								} catch (IOException e) {
									Bukkit.getLogger().info("[Player Reviewer] GitLab issue creator: unknown error");
								}
								
							} else {
								
								p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Invalid Syntax!");
								p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Usage: " + ChatColor.ITALIC + "/pr approval <name> <approve/deny>	");
								
							}
						} else {
							p.sendMessage(Main.prefix + ChatColor.RED + "You do not have permission");
						}
						
					//pr removeapplication <name>
					} else if ((args[0].equalsIgnoreCase("removeapplication")) | (args[0].equalsIgnoreCase("ra")) | (args[0].equalsIgnoreCase("remapp"))) {
						
						if (p.hasPermission("pr.removeapplication")) {
							if (args.length == 2) {
								
								try {
									 if (Main.UseSQL == true) {
										 SQLLink.removeApp(args[1]);
									 } else {
										 InvConfig.removeApp(args[1]);
									 }
									 if (GitConfig.useGitLab()) {
											GitLabManager.removeIssue(p, "Application%20for%20" + p.getName());
										}
									 p.sendMessage(Main.prefix + ChatColor.AQUA + "Application Removed!");
									
								} catch (SQLException | ClassNotFoundException | FieldValueException e) {
									p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Application For " + args[1] + " Does Not Exist!");
								} catch (IOException e) {
									Bukkit.getLogger().info("[Player Reviewer] GitLab issue creator: unknown error");
								}
								
							} else {
								
								p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Invalid Syntax!");
								p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Usage: " + ChatColor.ITALIC + "/pr removeapplication <name>");
								
							}
						} else {
							p.sendMessage(Main.prefix + ChatColor.RED + "You do not have permission");
						}
						
						
					} else if (args[0].equalsIgnoreCase("version")) {
        				
						if (p.hasPermission("pr.version")) {
							String version = Bukkit.getServer().getPluginManager().getPlugin("PlayerReviewer").getDescription().getVersion();
	            			
	            			Main.InfoHeader(p, "Player Reviewer Version Info");
	            			p.sendMessage("");
	        		    	p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "Version Number " + ChatColor.WHITE + ":: " + ChatColor.RED + version);
	        		    	p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "Author " + ChatColor.WHITE + ":: " + ChatColor.RED + "EngineersBox");
	        		    	p.sendMessage("");
	        		    	Main.InfoHeader(p, "Player Reviewer Version Info");
						} else {
							p.sendMessage(Main.prefix + ChatColor.RED + "You do not have permission");
						}
						
					} else if (args[0].equalsIgnoreCase("status")) {
						
						if (p.hasPermission("pr.status")) {
							Status status = Main.appStatus.get(p.getUniqueId());
							
							if (status != null) {
								switch(Main.appStatus.get(p.getUniqueId())) {
								
								case APPROVED:
									p.sendMessage(Main.prefix + ChatColor.AQUA + "Application status: " + ChatColor.GREEN + "approved");
									break;
									
								case DENIED:
									p.sendMessage(Main.prefix + ChatColor.AQUA + "Application status: " + ChatColor.RED + "denied");
									break;
									
								case AWAITING_REVIEW:
									p.sendMessage(Main.prefix + ChatColor.AQUA + "Application status: " + ChatColor.RED + "awaitng review");
									break;
									
								default:
									p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Application for " + p.getName() + " does not exist");
									break;
							
								}
							} else {
								p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Application for " + p.getName() + " does not exist");
							}
						} else {
							p.sendMessage(Main.prefix + ChatColor.RED + "You do not have permission");
						}
						
					} else if (args[0].equalsIgnoreCase("pos1")) {
						
						if (p.hasPermission("pr.pos1")) {
							if (useChunky) {
								if (Main.positions.get(tHash) != null) {
				                	
				                    CoordsObject tpos = Main.positions.get(tHash);
				                    tpos.setPosition1(p.getLocation());
				                    Main.positions.put(tHash,tpos);
				                    Main.chunkList.put(p.getUniqueId(), getChunks(tHash));
				                    
				                } else {
				                	
				                    CoordsObject tpos = new CoordsObject();
				                    tpos.setPosition1(p.getLocation());
				                    Main.positions.put(tHash, tpos);
				                }
				                
				                p.sendMessage(Main.prefix + ChatColor.AQUA + "Position 1 registered");
							} else {
								p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Chunky usage not enabled, enable it in the config");
							}
						} else {
							p.sendMessage(Main.prefix + ChatColor.RED + "You do not have permission");
						}
						
		            } else if (args[0].equalsIgnoreCase("pos2")) {
		            	
		            	if (p.hasPermission("pr.pos2")) {
		            		if (useChunky) {
				                if (Main.positions.get(tHash) != null) {
				                	
				                    CoordsObject tpos = Main.positions.get(tHash);
				                    tpos.setPosition2(p.getLocation());
				                    Main.positions.put(tHash, tpos);
				                    Main.chunkList.put(p.getUniqueId(), getChunks(tHash));
				                    
				                } else {
				                	
				                    CoordsObject tpos = new CoordsObject();
				                    tpos.setPosition2(p.getLocation());
				                    Main.positions.put(tHash, tpos);
				                    
				                }
				                
				                p.sendMessage(Main.prefix + ChatColor.AQUA + "Position 2 registered");
			            	} else {
			            		p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Chunky usage not enabled, enable it in the config");
			            	}
		            	} else {
		            		p.sendMessage(Main.prefix + ChatColor.RED + "You do not have permission");
		            	}
		            	
		            } else if (args[0].equalsIgnoreCase("cam")) {
		            	
		            	if (p.hasPermission("pr.cam")) {
		            		if (Config.useChunky()) {
			            		try {
				            		
				            		MaxSizeHashMap<String, CameraObject> cams = Main.cameras.get(p.getUniqueId());
				            		
				            		if (cams != null) {
				            			int camCount = cams.size();
				            			
				            			if (camCount < cams.maxSize()) {
						            		camCount += 1;
						            		
						            		if (cams.get("camera_" + camCount) != null) {
							                	
							                    CameraObject cam = cams.get("camera_" + camCount);
							                    cam.orientation.setPitch(p.getLocation().getPitch());
							                    cam.orientation.setYaw(p.getLocation().getYaw());
							                    cam.position.setX(p.getLocation().getX());
							                    cam.position.setY(p.getLocation().getY());
							                    cam.position.setZ(p.getLocation().getZ());
							                    
												cams.put("camera_" + camCount, cam);
												Main.cameras.put(p.getUniqueId(), cams);
												p.sendMessage(Main.prefix + ChatColor.AQUA + "Camera " + camCount + " registered");
							                    
							                } else {
							                	
							                    CameraObject cam = new CameraObject("camera_" + camCount);
							                    cam.orientation = new OrientationChunky();
							                    cam.orientation.setPitch(p.getLocation().getPitch());
							                    cam.orientation.setYaw(p.getLocation().getYaw());
							                    cam.position = new PositionChunky();
							                    cam.position.setX(p.getLocation().getX());
							                    cam.position.setY(p.getLocation().getY());
							                    cam.position.setZ(p.getLocation().getZ());
							                    
							                    cams.put("camera_" + camCount, cam);
												Main.cameras.put(p.getUniqueId(), cams);
												p.sendMessage(Main.prefix + ChatColor.AQUA + "Camera " + camCount + " registered");
							                    
							                }
							              
						            	} else {
						            		p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Max camera count " + Config.maxCamCount() + " reached!");
						            	}
				            			
				            		} else {
				            			
				            			cams = new MaxSizeHashMap<String, CameraObject>(Config.maxCamCount());
				            			
				            			CameraObject cam = new CameraObject("camera_1");
					                    cam.orientation = new OrientationChunky();
					                    cam.orientation.setPitch(Math.toRadians(p.getLocation().getPitch()) * (-Math.PI - (Math.PI/2)));
					                    cam.orientation.setYaw(Math.toRadians(p.getLocation().getYaw()) );
					                    cam.position = new PositionChunky();
					                    cam.position.setX(p.getLocation().getX());
					                    cam.position.setY(p.getLocation().getY());
					                    cam.position.setZ(p.getLocation().getZ());
					                    
					                    cams.put("camera_1", cam);
										Main.cameras.put(p.getUniqueId(), cams);
										p.sendMessage(Main.prefix + ChatColor.AQUA + "Camera 1 registered");
				            			
				            		}
				            		
				            	} catch (HashMapSizeOverflow e) {
				            		p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Max camera count " + Config.maxCamCount() + " reached!");
				            	}
			            	} else {
			            		p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Chunky usage not enabled, enable it in the config");
			            	}
		            	} else {
		            		p.sendMessage(Main.prefix + ChatColor.RED + "You do not have permission");
		            	}
		            	
		            } else if ((args[0].equalsIgnoreCase("chunkysettings")) || (args[0].equalsIgnoreCase("cs"))) {
		            	if (p.hasPermission("pr.chunkysettings")) {
		            		if (useChunky) {
			            		String pos1 = ChatColor.DARK_RED + "Not registered ";
			            		String pos2 = ChatColor.DARK_RED + "Not registered ";
			            		String chunks = ChatColor.DARK_RED + "Not registered ";
			            		List<String> cams = new ArrayList<String>();
			            		cams.add(ChatColor.DARK_RED + "Not registered");
			            		CoordsObject pos = Main.positions.get(tHash);
			            		
			            		if (pos != null) {
			            			if (pos.getPosition1() != null) {
			            				pos1 = pos.getPosition1().toString();
			            			}
			            			
			            			if (pos.getPosition2() != null) {
				            			pos2 = pos.getPosition2().toString();
				            		}
			            			
			            			if (getChunks(tHash) != null) {
				            			chunks = getChunks(tHash).toString();
				            		}
			            		}
			            		
			            		if (Main.cameras.get(p.getUniqueId()) != null) {
			            			cams.clear();
			            			
			            			MaxSizeHashMap<String, CameraObject> cCams = Main.cameras.get(p.getUniqueId());
			            			for (int i = 1; i <= cCams.maxSize(); i++) {
				            			if (cCams.get("camera_" + i) != null) {
				            				cams.add(ChatColor.DARK_GRAY + "- " + ChatColor.AQUA + "Camera " + i + ":" + ChatColor.GREEN + " Registered");
				            			} else {
				            				cams.add(ChatColor.DARK_GRAY + "- " + ChatColor.AQUA + "Camera " + i + ":" + ChatColor.DARK_RED + " Not registered");
				            			}
				            		}
			            		}
			            		
		            			p.sendMessage(Main.prefix + ChatColor.GOLD + "Pos 1: " + ChatColor.AQUA + pos1);
				                p.sendMessage(Main.prefix + ChatColor.GOLD + "Pos 2: " + ChatColor.AQUA + pos2);
				                p.sendMessage(Main.prefix + ChatColor.GOLD + "Chunks: " + ChatColor.AQUA + chunks);
				                if (cams.size() == 1) {
				                	p.sendMessage(Main.prefix + ChatColor.GOLD + "Cameras: " + cams.get(0));
				                } else {
				                	p.sendMessage(Main.prefix + ChatColor.GOLD + "Cameras:");
				                	for (String cam : cams) {
					                	p.sendMessage(cam);
					                }
				                }
			            	} else {
			            		p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Chunky usage not enabled, enable it in the config");
			            	}
		            	} else {
		            		p.sendMessage(Main.prefix + ChatColor.RED + "You do not have permission");
		            	}
		            	
		            } else if ((args[0].equalsIgnoreCase("setparam")) || (args[0].equalsIgnoreCase("pr"))) {
						if (p.hasPermission("pr.setparam")) {
							if (useChunky) {
								if (args.length >= 3) {
									
									List<String> tempArgs = Arrays.asList(args);
									Gson gson = new Gson();
									String param = gson.toJson(tempArgs.get(1).replace(":", ""));
									param = param.substring(1, param.length() - 1);
									String value = tempArgs.get(2);
									
									if (args.length > 3) {
										for (String arg : tempArgs.subList(3, tempArgs.size())) {
											value += " " + arg;
										}
									}
									
									value = gson.toJson(value);
									value = value.substring(1, value.length() - 1);
									
									JSONParameter jParam;
									String playerMsg = "Parameter ";
									
									if (Main.paramMap.containsKey(p.getUniqueId())) {
										jParam = Main.paramMap.get(p.getUniqueId());
										if (jParam.getParams().contains(param)) {
											jParam.replaceValue(param, value);
											playerMsg += "changed";
										} else {
											jParam.addParamValue(param, value);
											playerMsg += "added";
										}
									} else {
										jParam = new JSONParameter();
										jParam.addParamValue(param, value);
										playerMsg += "added";
									}
									
									Main.paramMap.put(p.getUniqueId(), jParam);
									p.sendMessage(Main.prefix + ChatColor.AQUA + playerMsg);
									
								} else {
									p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Not enough arguments!");
								}
			            	} else {
			            		p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Chunky usage not enabled, enable it in the config");
			            	}
						} else {
							p.sendMessage(Main.prefix + ChatColor.RED + "You do not have permission");
						}
		            	
					} else if ((args[0].equalsIgnoreCase("removeparam")) || (args[0].equalsIgnoreCase("rp"))) {
						if (p.hasPermission("pr.removeparam")) {
							if (useChunky) {
								JSONParameter jParam;
								if (Main.paramMap.containsKey(p.getUniqueId())) {
									jParam = Main.paramMap.get(p.getUniqueId());
									if (jParam.getParams().contains(args[1])) {
										int paramIndex = jParam.getParams().indexOf(args[1]);
										jParam.getParams().remove(paramIndex);
										jParam.getValues().remove(paramIndex);
										p.sendMessage(Main.prefix + ChatColor.AQUA + "Parameter removed");
									} else {
										p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Parameter \"" + args[1] + "\" not set");
									}
								} else {
									p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "No parameters applied");
								}
							} else {
								p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Chunky usage not enabled, enable it in the config");
							}
						} else {
							p.sendMessage(Main.prefix + ChatColor.RED + "You do not have permission");
						}
						
						
					} else if ((args[0].equalsIgnoreCase("viewparams")) || (args[0].equalsIgnoreCase("vp"))) {
						if (p.hasPermission("pr.viewparams")) {
							if (useChunky) {
								JSONParameter jParam = Main.paramMap.get(p.getUniqueId());
								Gson gson = new Gson();
								
								if (args.length == 2) {
									if (jParam != null) {
										if ((args[1].equalsIgnoreCase("raw")) && (p.hasPermission("pr.raw"))) {
											p.sendMessage("");
											Main.InfoHeader(p, "Render Service Parameters");
											p.sendMessage(ChatColor.GRAY + jParam.toString());
											Main.InfoHeader(p, "Render Service Parameters");
											p.sendMessage("");
										} else if ((args[1].equalsIgnoreCase("list")) && (p.hasPermission("pr.list"))) {
			
											int bracketCount = 0;
											String spaces = "";
											String[] jString = gson.toJson(jParam.toString()).replaceAll("[,\"\\\\\"]", "").split("\\s");
											List<String> pMsg = new ArrayList<String>();
											
											for (int i = 0; i < jString.length; i++) {
												if (jString[i].contains("{")) {
													bracketCount += 1;
												} else if (jString[i].contains("}")) {
													bracketCount -= 1;
												}
												
												String tempSpaces = "";
												for (int j = 0; j < bracketCount; j++) {
													tempSpaces += "  ";
												}
												spaces = tempSpaces;
												
												if (jString[i].endsWith(":")) {
													if (jString[i + 1].equals("{")) {
														pMsg.add(ChatColor.DARK_GRAY + spaces + "- " + ChatColor.GREEN + jString[i].replace(":", ""));
													} else {
														pMsg.add(ChatColor.DARK_GRAY + spaces + "- " + ChatColor.GREEN + jString[i].replace(":", "") + ChatColor.WHITE + " :: " + ChatColor.RED + jString[i + 1]);
													}
												}
											}
											
											p.sendMessage("");
											Main.InfoHeader(p, "Render Service Parameters");
											for (String msg : pMsg) {
												p.sendMessage(msg);
											}
											Main.InfoHeader(p, "Render Service Parameters");
											p.sendMessage("");
										} else {
											p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Invalid display type");
											p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Usage:" + ChatColor.ITALIC + "/pr viewparams <raw/list>");
										}
									} else {
										p.sendMessage(Main.prefix + ChatColor.AQUA + "No parameters set");
									}
								} else {
									p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Invalid display type");
									p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Usage:" + ChatColor.ITALIC + "/pr viewparams <raw/list>");
								}
							} else {
								p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Chunky usage not enabled, enable it in the config");
							}
						} else {
							p.sendMessage(Main.prefix + ChatColor.RED + "You do not have permission");
						}
				
					} else if ((args[0].equalsIgnoreCase("clearparams")) || (args[0].equalsIgnoreCase("cp"))) {
						if ((p.hasPermission("pr.clearparams"))) {
							if (useChunky) {
								if (args.length == 1) {
									p.sendMessage(Main.prefix + ChatColor.BLUE + "Are you sure you want to clear render parameters?");
									p.sendMessage(Main.prefix + ChatColor.GREEN + "CONFIRM" + ChatColor.BLUE + " clearing paramters with /pr clearparams confirm");
									p.sendMessage(Main.prefix + ChatColor.RED + "DENY" + ChatColor.BLUE + " clearing paramters with /pr clearparams deny");
									Main.atConfirm = true;
								} else if ((args.length == 2) && (Main.atConfirm == true)) {
									if (args[1].equalsIgnoreCase("confirm")) {
										Main.paramMap.remove(p.getUniqueId());
										p.sendMessage(Main.prefix + ChatColor.AQUA + "Parameters cleared");
										Main.atConfirm = false;
									} else if (args[1].equalsIgnoreCase("deny")) {
										p.sendMessage(Main.prefix + ChatColor.AQUA + "Parameters not cleared");
										Main.atConfirm = false;
									} else {
										p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Invalid syntax");
										p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Usage:" + ChatColor.ITALIC + "/pr clearparams <confirm/deny>");
									}
								} else {
									p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Invalid syntax");
									p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Usage:" + ChatColor.ITALIC + "/pr clearparams");
								}
							} else {
								p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Chunky usage not enabled, enable it in the config");
							}
						} else {
							p.sendMessage(Main.prefix + ChatColor.RED + "You do not have permission");
						}
		
					} else {
							
						p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "Invalid Command!");
						p.sendMessage(Main.prefix + ChatColor.LIGHT_PURPLE + "View Valid Commands With: " + ChatColor.ITALIC + "/pr help");
							
					}
					
				}
				
				return true;
				
			}
			
			return true;
			
		}
		
		return true;
		
	}
	
	public String getChunks(String tHash) {
		
		CoordsObject pos = Main.positions.get(tHash);
		if ((pos != null) && (pos.getPosition1() != null) && (pos.getPosition2() != null)) {
			
			ArrayList<Object> chunkList = new ArrayList<Object>();
	        Location location1 = Main.positions.get(tHash).getPosition1();
	        Location location2 = Main.positions.get(tHash).getPosition2();
	        
	        int xMin; int xMax;
	        if(location1.getChunk().getX() > location2.getChunk().getX()) {
	            xMin = location2.getChunk().getX();
	            xMax = Main.positions.get(tHash).getPosition1().getChunk().getX();
	        }else {
	            xMin = location1.getChunk().getX();
	            xMax = location2.getChunk().getX();
	        }

	        int zMin; int zMax;
	        if(location1.getChunk().getZ() > location2.getChunk().getZ()) {
	            zMin = location2.getChunk().getZ();
	            zMax = location1.getChunk().getZ();
	        }else {
	            zMin = location1.getChunk().getZ();
	            zMax = location2.getChunk().getZ();
	        }

	        for (int x = xMin; x <= xMax; x++) {
	            for (int z = zMin; z <= zMax; z++) {
	                // chunk
	                int[] aChunk = new int[2];
	                aChunk[0] = x;
	                aChunk[1] = z;
	                chunkList.add(aChunk);
	            }
	        }
	        
	        Gson gson = new Gson();
	        String jsonStr = gson.toJson(chunkList);

	        return jsonStr;
		} else {
			return null;
		}
    }
	
	public List<String> addToListIfMatched(List<String> list, String valueToAdd, String toMatch) {
        if (valueToAdd.toLowerCase().startsWith(toMatch.toLowerCase())) {
            list.add(valueToAdd);
        }
        return list;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		Player p = (Player) sender;
		if (!p.hasPermission("pr.use")) {
			return null;
		}
		
		List<String> comp = new ArrayList<String>();
		if (cmd.getName().equalsIgnoreCase("pr")) {
			if (args.length == 0) {
				comp = addToListIfMatched(comp, "help", "");
    			comp = addToListIfMatched(comp, "apphelp", "");
    			comp = addToListIfMatched(comp, "renderhelp", "");
    			comp = addToListIfMatched(comp, "apply", "");
    			comp = addToListIfMatched(comp, "validranks", "");
    			comp = addToListIfMatched(comp, "rate", "");
    			comp = addToListIfMatched(comp, "gotoplot", "");
    			comp = addToListIfMatched(comp, "ratings", "");
    			comp = addToListIfMatched(comp, "approval", "");
    			comp = addToListIfMatched(comp, "removeapplication", "");
    			comp = addToListIfMatched(comp, "version", "");
    			comp = addToListIfMatched(comp, "status", "");
    			comp = addToListIfMatched(comp, "pos1", "");
    			comp = addToListIfMatched(comp, "pos2", "");
    			comp = addToListIfMatched(comp, "cam", "");
    			comp = addToListIfMatched(comp, "chunkysettings", "");
    			comp = addToListIfMatched(comp, "setparam", "");
    			comp = addToListIfMatched(comp, "removeparam", "");
    			comp = addToListIfMatched(comp, "viewparams", "");
    			comp = addToListIfMatched(comp, "clearparams", "");
			} else if (args.length == 1) {
				comp = addToListIfMatched(comp, "help", args[0]);
    			comp = addToListIfMatched(comp, "apphelp", args[0]);
    			comp = addToListIfMatched(comp, "renderhelp", args[0]);
    			comp = addToListIfMatched(comp, "apply", args[0]);
    			comp = addToListIfMatched(comp, "validranks", args[0]);
    			comp = addToListIfMatched(comp, "rate", args[0]);
    			comp = addToListIfMatched(comp, "gotoplot", args[0]);
    			comp = addToListIfMatched(comp, "ratings", args[0]);
    			comp = addToListIfMatched(comp, "approval", args[0]);
    			comp = addToListIfMatched(comp, "removeapplication", args[0]);
    			comp = addToListIfMatched(comp, "version", args[0]);
    			comp = addToListIfMatched(comp, "status", args[0]);
    			comp = addToListIfMatched(comp, "pos1", args[0]);
    			comp = addToListIfMatched(comp, "pos2", args[0]);
    			comp = addToListIfMatched(comp, "cam", args[0]);
    			comp = addToListIfMatched(comp, "chunkysettings", args[0]);
    			comp = addToListIfMatched(comp, "setparam", args[0]);
    			comp = addToListIfMatched(comp, "removeparam", args[0]);
    			comp = addToListIfMatched(comp, "viewparams", args[0]);
    			comp = addToListIfMatched(comp, "clearparams", args[0]);
			} else if (args.length >= 2) {
				if (args[0].equalsIgnoreCase("removeapplication") || args[0].equalsIgnoreCase("ra") || args[0].equalsIgnoreCase("gotoplot") || args[0].equalsIgnoreCase("rate")) {
					Set<OfflinePlayer> playerList = new HashSet<>();
					playerList.addAll((Collection<? extends OfflinePlayer>) Bukkit.getOnlinePlayers());
					playerList.addAll(Arrays.asList(Bukkit.getOfflinePlayers()));
					for (OfflinePlayer op : playerList) {
						comp = addToListIfMatched(comp, op.getName(), args[1]);
					}
				} else if (args[0].equalsIgnoreCase("approval")) {
					comp = addToListIfMatched(comp, "approve", args[1]);
					comp = addToListIfMatched(comp, "deny", args[1]);
				} else if (args[0].equalsIgnoreCase("viewparams")) {
					comp = addToListIfMatched(comp, "raw", args[1]);
					comp = addToListIfMatched(comp, "list", args[1]);
				} else if (args[0].equalsIgnoreCase("clearparams")) {
					comp = addToListIfMatched(comp, "confirm", args[1]);
					comp = addToListIfMatched(comp, "deny", args[1]);
				}
			}
		}
		return comp;
    }
	
}

