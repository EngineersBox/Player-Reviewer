package me.engineersbox.playerrev;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.engineersbox.playerrev.exceptions.FieldValueException;
import me.engineersbox.playerrev.exceptions.InvalidGroupException;
import me.engineersbox.playerrev.methodlib.GroupPlugins;
import me.engineersbox.playerrev.methodlib.Lib;
import me.engineersbox.playerrev.mysql.SQLLink;

public class Commands implements CommandExecutor {
	
	public enum RankEnum {
		GUEST("GUEST"),
		SQUIRE("SQUIRE"),
		KNIGHT("KNIGHT"),
		BARON("BARON"),
		BUILDER("BUILDER"),
		HEAD_BUILDER("HEAD_BUILDER"),
		SENIOR_BUILDER("SENIOR_BUILDER");
		
	    private String[] aliases;

	    private static Map<String, RankEnum> rankenum = new HashMap<>();
	    static {
	        for (RankEnum re : RankEnum.values()) {
	            for (String alias : re.aliases) {
	            	rankenum.put(alias, re);
	            }
	        }
	    }

	    private RankEnum(String ... aliases) {
	        this.aliases = aliases;
	    }

	    public static RankEnum valueOfByAlias(String alias) {
	    	RankEnum re = rankenum.get(alias);
	        if (re == null) {
	            throw new IllegalArgumentException("No enum alias " + RankEnum.class.getCanonicalName() + "." + alias);
	        }
	        return re;
	    }
	    
	    public static boolean isValid(String alias) {
	    	RankEnum re = rankenum.get(alias);
	    	if (re == null) {
	    		return false;
	    	} else {
	    		return true;
	    	}
	    }
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof Player) {
			
			Player p = (Player) sender;
			
			if ((cmd.getName().equalsIgnoreCase("pr")) && (p.hasPermission("pr.use"))) {
					
				if (args.length == 0) {
					
					p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Use: " + ChatColor.ITALIC + "/pr help" + ChatColor.RESET + ChatColor.DARK_PURPLE + " To View All Commands");
					
				} else if (args.length > 0) {
					
					//pr apply <rank>
					if ((args[0].equalsIgnoreCase("apply")) && (p.hasPermission("pr.apply"))) {
						
						if (args.length == 2) {
							
							if (RankEnum.isValid(args[1].toUpperCase()) == true) {
								
								try {
									if (Main.UseSQL == true) {
										SQLLink.newApp(p, p.getDisplayName(), args[1].toString().toLowerCase());
										p.sendMessage(Main.prefix + ChatColor.AQUA + "Application Submitted!");
									} else {
										InvConfig.newApp(p, p.getDisplayName(), args[1].toString().toLowerCase());
										p.sendMessage(Main.prefix + ChatColor.AQUA + "Application Submitted!");
									}
									
								} catch (SQLException | FieldValueException e) {
									p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Application For " + p.getDisplayName() + " Already Exists!");
								}
								
							} else {
								
								p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Invalid Rank!");
								p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "View Valid Ranks With: " + ChatColor.ITALIC + "/pr validranks");
								
							}
							return true;
							
						} else {
							
							p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Invalid Syntax!");
							p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Usage: " + ChatColor.ITALIC + "/pr apply <rank>");
							
						}
					
					} else if((args[0].equalsIgnoreCase("help")) && (p.hasPermission("pr.help"))) {
						
						Main.InfoHeader(p, "Player Reviewer");
						p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "/rv validranks " + ChatColor.WHITE + ":: " + ChatColor.RED + "Displays All Ranks That Can Be Applied For");
						p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "/rv apply <rank> " + ChatColor.WHITE + ":: " + ChatColor.RED + "Submit An Application For A Rank");
						p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "/rv removeapplication <name> " + ChatColor.WHITE + ":: " + ChatColor.RED + "Remove An Open Application");
						p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "/rv approval <player> <approve/deny> " + ChatColor.WHITE + ":: " + ChatColor.RED + "Approve Or Deny An Application");
						p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "/rv ratings <player> " + ChatColor.WHITE + ":: " + ChatColor.RED + "View The Ratings Of A Player's Application");
						p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "/rv rate <player> <atmosphere> <originality> <skill> " + ChatColor.WHITE + ":: " + ChatColor.RED + "Submit A Rating To A Player's Open Application");
		            	p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "/pr version " + ChatColor.WHITE + ":: " + ChatColor.RED + "Displays The Plugin Version And Author");
		            	p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "/rv help " + ChatColor.WHITE + ":: " + ChatColor.RED + "Opens This Menu");
		            	Main.InfoHeader(p, "Player Reviewer");
						
					} else if (((args[0].equalsIgnoreCase("validranks")) | (args[0].equalsIgnoreCase("vr")) | (args[0].equalsIgnoreCase("ranks"))) && (p.hasPermission("pr.validranks"))) {
						
						Main.InfoHeader(p, "Player Reviewer Valid Ranks");
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
        		    	Main.InfoHeader(p, "Player Reviewer Valid Ranks");
        		    	return true;
        		    	
					//pr rate <player> <atmosphere> <originality> <terrain> <structure> <layout>
					} else if ((args[0].equalsIgnoreCase("rate")) && (args.length > 1) && (p.hasPermission("pr.rate"))) {
						
						if (args.length == 7) {
							
							boolean successFlag = true;
							
							try {
								
								if (Main.UseSQL == true) {
									SQLLink.ratePlayer(p.getDisplayName(), args[1], Lib.returnInRange(args[2]), Lib.returnInRange(args[3]), Lib.returnInRange(args[4]), Lib.returnInRange(args[5]), Lib.returnInRange(args[6]));
									p.sendMessage(Main.prefix + ChatColor.AQUA + "Rating For " + args[1] + "'s Application Submitted!");
								} else {
									InvConfig.ratePlayer(p.getDisplayName(), args[1], Lib.returnInRange(args[2]), Lib.returnInRange(args[3]), Lib.returnInRange(args[4]), Lib.returnInRange(args[5]), Lib.returnInRange(args[6]));
									p.sendMessage(Main.prefix + ChatColor.AQUA + "Rating For " + args[1] + "'s Application Submitted!");
								}
								successFlag = false;
								
							} catch (NumberFormatException e) {
								
								p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Invalid Rating Value!");
								p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Values Must Be Integers, Not" +  e.toString().substring(e.toString().lastIndexOf(":") + 1));
								successFlag = false;
								
							} catch (SQLException | FieldValueException se) {
								
								p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Application For " + args[1] + " Does not Exist!");
								successFlag = false;
								
							}
							
							if (successFlag) {
								
								p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Invalid Rating Value!");
								p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Valid Values Are 0 - 100 Inclusive");
								
							}
							return true;
							
						} else {
							
							p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Invalid Syntax!");
							p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Usage: " + ChatColor.ITALIC + "/pr rate <player> <atmosphere> <originality> <skill>");
							
						}
						
					
					} else if (((args[0].equalsIgnoreCase("gotoplot")) | (args[0].equalsIgnoreCase("plot"))) && (p.hasPermission("pr.gotoplot"))) {
						
						try {
							
							if (Main.UseSQL == true) {
								p.teleport(SQLLink.getPlotLocation(args[1]));
							} else {
								p.teleport(InvConfig.getPlotLocation(args[1]));
							}
							
						} catch (SQLException | FieldValueException se) {
							p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Application Does Not Exist!");
						}
						
					//pr ratings <player>
					} else if (args[0].equalsIgnoreCase("ratings")) {
						
						if ((args.length == 2) && ((args[1].equalsIgnoreCase(p.getDisplayName())) | (p.hasPermission("pr.ratings")))) {
							
							try {

								if (Main.UseSQL == true) {
									ArrayList<List<String>> Ratings = SQLLink.getRatingValues(args[1]);
									String appRank = Ratings.get(0).get(0);
									
									p.sendMessage("");
									p.sendMessage(ChatColor.DARK_GRAY + "----={<" + ChatColor.RED + "  [" + ChatColor.DARK_AQUA + args[1] + " Ratings" + ChatColor.RED + "]  " + ChatColor.DARK_GRAY + "}>=----");
									p.sendMessage(ChatColor.DARK_PURPLE + "Format: <name> :: <Atmosphere> <Originality> <Terrain> <Structure> <Layout>");
									p.sendMessage("");
									for (String ra : Ratings.get(1)) {
										String[] ratingValues = ra.split("-");
										p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + ratingValues[0] + " " + ChatColor.WHITE +  ":: " +  ChatColor.RED + ratingValues[1] + " " + ratingValues[2] + " " + ratingValues[3] + " " + ratingValues[4] + " " + ratingValues[5]);
									}
									p.sendMessage("");
									p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "Averages " + ChatColor.WHITE + ":: " + ChatColor.RED + Ratings.get(0).get(1) + " " + Ratings.get(0).get(2) + " " + Ratings.get(0).get(3) + " " + Ratings.get(0).get(4) + " " + Ratings.get(0).get(5));
									p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "Total Ratings " + ChatColor.WHITE + ":: " + ChatColor.RED + Ratings.get(0).get(6));
									p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "Rank Applied For " + ChatColor.WHITE + ":: " + ChatColor.RED + appRank.substring(0, 1).toUpperCase() + appRank.substring(1));
									p.sendMessage(ChatColor.DARK_GRAY + "----={<" + ChatColor.RED + "  [" + ChatColor.DARK_AQUA + args[1] + " Ratings" + ChatColor.RED + "]  " + ChatColor.DARK_GRAY + "}>=----");
								} else {
									ArrayList<List<String>> Ratings = InvConfig.getRatings(args[1]);
									String appRank = Ratings.get(1).get(4);
									
									p.sendMessage("");
									p.sendMessage(ChatColor.DARK_GRAY + "----={<" + ChatColor.RED + "  [" + ChatColor.DARK_AQUA + args[1] + " Ratings" + ChatColor.RED + "]  " + ChatColor.DARK_GRAY + "}>=----");
									p.sendMessage(ChatColor.DARK_PURPLE + "Format: <name> :: <Atmosphere> <Originality> <Terrain> <Structure> <Layout>");
									p.sendMessage("");
									for (String ra : Ratings.get(0)) {
										String[] ratingValues = ra.split("-");
										p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + ratingValues[0] + " " + ChatColor.WHITE +  ":: " +  ChatColor.RED + ratingValues[1] + " " + ratingValues[2] + " " + ratingValues[3] + " " + ratingValues[4] + " " + ratingValues[5]);
									}
									p.sendMessage("");
									p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "Averages " + ChatColor.WHITE + ":: " + ChatColor.RED + Ratings.get(1).get(0) + " " + Ratings.get(1).get(1) + " " + Ratings.get(1).get(2) + " " + Ratings.get(1).get(3) + " " + Ratings.get(1).get(4));
									p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "Total Ratings " + ChatColor.WHITE + ":: " + ChatColor.RED + Ratings.get(1).get(3));
									p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "Rank Applied For " + ChatColor.WHITE + ":: " + ChatColor.RED + appRank);
									p.sendMessage(ChatColor.DARK_GRAY + "----={<" + ChatColor.RED + "  [" + ChatColor.DARK_AQUA + args[1] + " Ratings" + ChatColor.RED + "]  " + ChatColor.DARK_GRAY + "}>=----");
								}
								
								return true;
								
							} catch (SQLException | FieldValueException e) {
								p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Application For " + args[1] + " Does Not Exist!");
							}
							
						} else {
							
							p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Invalid Syntax!");
							p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Usage: " + ChatColor.ITALIC + "/pr viewratings <name>");
							
						}
						
					//pr approval <name> <approve/deny>	
					} else if ((args[0].equalsIgnoreCase("approval")) && (args.length > 1) && (p.hasPermission("pr.approval"))) {
						
						if (args.length == 3) {
							
							try {

								if (args[2].equalsIgnoreCase("approve")) {
									if (Main.UseSQL == true) {
										String rank = SQLLink.getAppRank(args[1]);
										if (Main.rankPlugin.equalsIgnoreCase("pex")) {
											GroupPlugins.pexAssignGroup(p, rank);
										} else if (Main.rankPlugin.equalsIgnoreCase("lp")) {
											GroupPlugins.lpAssignGroup(p, rank);
										}
										SQLLink.removeApp(args[1]);
									} else {
										String rank = InvConfig.getAppRank(args[1]);
										if (Main.rankPlugin.equalsIgnoreCase("pex")) {
											GroupPlugins.pexAssignGroup(p, rank);
										} else if (Main.rankPlugin.equalsIgnoreCase("lp")) {
											GroupPlugins.lpAssignGroup(p, rank);
										}
										InvConfig.removeApp(args[1]);
									}
									
								} else if (args[2].equalsIgnoreCase("deny")) {
									
									if (Main.UseSQL == true) {
										SQLLink.removeApp(args[1]);
									} else {
										InvConfig.removeApp(args[1]);
									}
									//TODO: Send message of application denial to player
									
								} else {
									
									p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Invalid Command Syntax!");
									p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Usage: " + ChatColor.ITALIC + "/pr approval <name> <approve/deny>");
									
								}
								
							} catch (SQLException | FieldValueException e) {
								p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Invalid Player Name: " + args[1]);
							} catch (InvalidGroupException e) {
								p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Invalid Rank In Application!");
							}
							
						} else {
							
							p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Invalid Syntax!");
							p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Usage: " + ChatColor.ITALIC + "/pr approval <name> <approve/deny>	");
							
						}
						
					//pr removeapplication <name>
					} else if (((args[0].equalsIgnoreCase("removeapplication")) | (args[0].equalsIgnoreCase("ra")) | (args[0].equalsIgnoreCase("remapp"))) && (p.hasPermission("pr.removeapplication"))) {
						
						if (args.length == 2) {
							
							try {
								 if (Main.UseSQL == true) {
									 SQLLink.removeApp(args[1]);
									p.sendMessage(Main.prefix + ChatColor.AQUA + "Application Removed!");
								 } else {
									 InvConfig.removeApp(args[1]);
									p.sendMessage(Main.prefix + ChatColor.AQUA + "Application Removed!");
								 }
								
							} catch (SQLException | FieldValueException e) {
								p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Application For " + args[1] + "Does Not Exist!");
							}
							
						} else {
							
							p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Invalid Syntax!");
							p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Usage: " + ChatColor.ITALIC + "/pr removeapplication <name>");
							
						}
						
					} else if ((args[0].equalsIgnoreCase("version")) && (p.hasPermission("pr.version"))) {
        				
            			String version = Bukkit.getServer().getPluginManager().getPlugin("PlayerReviewer").getDescription().getVersion();
            			
            			Main.InfoHeader(p, "Player Reviewer Version Info");
            			p.sendMessage("");
        		    	p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "Version Number " + ChatColor.WHITE + ":: " + ChatColor.RED + version);
        		    	p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "Author " + ChatColor.WHITE + ":: " + ChatColor.RED + "EngineersBox");
        		    	p.sendMessage("");
        		    	Main.InfoHeader(p, "Player Reviewer Version Info");
					
					} else {
						
						p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Invalid Command!");
						p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "View Valid Commands With: " + ChatColor.ITALIC + "/pr help");
						
					}
					
				}
				
				return true;
				
			}
			
			return false;
			
		}
		
		return false;
	}

}
