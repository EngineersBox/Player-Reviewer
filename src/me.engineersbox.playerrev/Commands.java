package me.engineersbox.playerrev;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Range;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
	
	String[] testEnum = {
		
		"default",
		"builder",
		"mod"
	};
	
	public enum RankEnum {
		DEFAULT("DEFAULT"),
		BUILDER("BUILDER"),
		SQUIRE("SQUIRE"),
		MOD("MOD", "MODERATOR"),
		ADMIN("ADMIN", "ADMIN", "ADMINISTRATOR");
		
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
	
	public static int returnInRange(Object value) {
		
		Range<Integer> RateRange = Range.between(0, 100);
		
		if (value.getClass().equals(int.class)) {
			
			if (RateRange.contains((Integer) value)) {
				return (Integer) value;
			} else {
				throw new NumberFormatException();
			}
		} else {
			throw new IllegalStateException();
		}
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (sender instanceof Player) {
			
			Player p = (Player) sender;
			
			if ((cmd.getName().equalsIgnoreCase("pr")) && (p.hasPermission("pr.use"))) {
					
				if (args.length == 0) {
					
					p.sendMessage("Tester");
					
				} else if (args.length > 0) {
					
					//pr apply <rank>
					if ((args[0].equalsIgnoreCase("apply")) && (args.length > 1) && (p.hasPermission("pr.apply"))) {
						
						if (RankEnum.isValid(args[1].toUpperCase()) == true) {
							
							InvConfig.newApp(p.getDisplayName(), args[1].toString().toLowerCase());
							p.sendMessage(Main.prefix + ChatColor.AQUA + "Application Submitted!");
							
						} else {
							
							p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Invalid Rank!");
							p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "View Valid Ranks With: " + ChatColor.ITALIC + "/pr validranks");
							
						}
						return true;
						
					} else if ((args[0].equalsIgnoreCase("validranks")) && (p.hasPermission("pr.validranks"))) {
						
						Main.InfoHeader(p, "Player Reviewer Valid Ranks");
        		    	for (RankEnum re : RankEnum.values()) {
        		    		p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + re);
        		    	}
        		    	Main.InfoHeader(p, "Player Reviewer Valid Ranks");
        		    	return true;
        		    	
					//pr rate <player> <atmosphere> <originality> <skill>
					} else if ((args[0].equalsIgnoreCase("rate")) && (args.length > 1) && (p.hasPermission("pr.rate"))) {
						
						try {
							
							InvConfig.ratePlayer(p.getDisplayName(), args[1], returnInRange(args[2]), returnInRange(args[3]), returnInRange(args[4]));
							
						} catch (NumberFormatException e) {
							
							p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Invalid Rating Value!");
							p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Valid Values Are 0 - 100 Inclusive");
							Bukkit.getLogger().info(e.toString());
							
						} catch (IllegalStateException i) {
							
							p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Invalid Rating Value!");
							p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Values Must Be Integers");
							Bukkit.getLogger().info(i.toString());
							
						}
						return true;
					
					//pr viewratings <player>
					} else if ((args[0].equalsIgnoreCase("viewratings")) && (args.length > 1) && (p.hasPermission("pr.viewratings"))) {
						
						ArrayList<List<String>> Ratings = InvConfig.getRatings(args[1]);
						//raters name-0-0-0, avAt-avOr-avSk-TotalRatings
						
						p.sendMessage("");
						p.sendMessage(ChatColor.DARK_GRAY + "----={<" + ChatColor.RED + "  [" + ChatColor.DARK_AQUA + args[1] + " Ratings" + ChatColor.RED + "]  " + ChatColor.DARK_GRAY + "}>=----");
						p.sendMessage(ChatColor.DARK_PURPLE + "Format: <name> :: <Atmosphere> <Originality> <Skill>");
						for (String ra : Ratings.get(0)) {
							String[] ratingValues = ra.split("-");
							p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + ratingValues[0] + " " + ChatColor.WHITE +  ":: " +  ChatColor.DARK_RED + ratingValues[1] + " " + ratingValues[2] + " " + ratingValues[3]);
						}
						p.sendMessage("");
						p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "Averages " + ChatColor.WHITE + ":: " + ChatColor.DARK_RED + Ratings.get(1).get(0) + Ratings.get(1).get(1) + Ratings.get(1).get(2));
						p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "Total Ratings " + ChatColor.WHITE + ":: " + ChatColor.DARK_RED + Ratings.get(1).get(3));
						p.sendMessage(ChatColor.DARK_GRAY + "----={<" + ChatColor.RED + "  [" + ChatColor.DARK_AQUA + args[1] + " Ratings" + ChatColor.RED + "]  " + ChatColor.DARK_GRAY + "}>=----");
						return true;
					
					} else if ((args[0].equalsIgnoreCase("viewratings")) && (args[1].equalsIgnoreCase(p.getDisplayName()))) {
						
						ArrayList<List<String>> Ratings = InvConfig.getRatings(args[1]);
						//raters name-0-0-0, avAt-avOr-avSk-TotalRatings
						
						p.sendMessage("");
						p.sendMessage(ChatColor.DARK_GRAY + "----={<" + ChatColor.RED + "  [" + ChatColor.DARK_AQUA + args[1] + " Ratings" + ChatColor.RED + "]  " + ChatColor.DARK_GRAY + "}>=----");
						p.sendMessage(ChatColor.DARK_PURPLE + "Format: <name> :: <Atmosphere> <Originality> <Skill>");
						for (String ra : Ratings.get(0)) {
							String[] ratingValues = ra.split("-");
							p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + ratingValues[0] + " " + ChatColor.WHITE +  ":: " +  ChatColor.DARK_RED + ratingValues[1] + " " + ratingValues[2] + " " + ratingValues[3]);
						}
						p.sendMessage("");
						p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "Averages " + ChatColor.WHITE + ":: " + ChatColor.DARK_RED + Ratings.get(1).get(0) + Ratings.get(1).get(1) + Ratings.get(1).get(2));
						p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + "Total Ratings " + ChatColor.WHITE + ":: " + ChatColor.DARK_RED + Ratings.get(1).get(3));
						p.sendMessage(ChatColor.DARK_GRAY + "----={<" + ChatColor.RED + "  [" + ChatColor.DARK_AQUA + args[1] + " Ratings" + ChatColor.RED + "]  " + ChatColor.DARK_GRAY + "}>=----");
						return true;
						
					//pr approval <name> <approve/deny>	
					} else if ((args[0].equalsIgnoreCase("approval")) && (args.length > 1) && (p.hasPermission("pr.approval"))) {
						
						if (args[2].equalsIgnoreCase("approve")) {
							
							//TODO: Remove application and approve rank (hook into rank plugin)
							//String rank = InvConfig.getAppRank(args[1]);
							
						} else if (args[2].equalsIgnoreCase("deny")) {
							
							InvConfig.removeApp(args[1]);
							//TODO: Send message of application denial to player
							
						} else {
							
							p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Invalid Command Syntax!");
							p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Usage: " + ChatColor.ITALIC + "/pr approval <name> <approve/deny>");
							
						}
						
					//pr removeapplication <name>
					} else if ((args[0].equalsIgnoreCase("removeapplication")) && (p.hasPermission("pr.removeapplication"))) {
						
						InvConfig.removeApp(args[1]);
						p.sendMessage(Main.prefix + ChatColor.AQUA + "Application Removed!");
					
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
