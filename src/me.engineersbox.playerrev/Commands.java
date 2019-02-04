package me.engineersbox.playerrev;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {
	
	public enum RankEnum {
		DEFAULT,
		BUILDER,
		SQUIRE,
		MOD("MODERATOR"),
		ADMIN("ADMIN", "ADMINISTRATOR");
		
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
					
				if (args.length > 0) {
					//pr apply <rank>
					if ((args[0].equalsIgnoreCase("apply")) && (args.length == 1) && (p.hasPermission("pr.apply"))) {
						
						if (RankEnum.isValid(args[1]) == true) {
							
							InvConfig.newApp(p.getDisplayName(), args[1].toString().toLowerCase());
							
						} else {
							
							p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Invalid Rank!");
							p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "View Valid Ranks With: " + ChatColor.ITALIC + "/pr validranks");
							
						}
						
					} else if ((args[0].equalsIgnoreCase("validranks")) && (p.hasPermission("pr.validranks"))) {
						
						Main.InfoHeader(p);
        		    	for (RankEnum re : RankEnum.values()) {
        		    		p.sendMessage(ChatColor.BLACK + "> " + ChatColor.DARK_GREEN + re);
        		    	}
        		    	Main.InfoHeader(p);
					//pr rate <player> <atmosphere> <originality> <skill>
					} else if ((args[0].equalsIgnoreCase("rate")) && (args.length > 1) && (p.hasPermission("pr.rate"))) {
						
						InvConfig.ratePlayer(p.getDisplayName(), args[1], Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
					
					//pr viewratings <player>
					} else if ((args[0].equalsIgnoreCase("viewratings")) && (args.length == 1) && (p.hasPermission("pr.viewratings"))) {
						
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
						
					} else {
						
						p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "Invalid Command!");
						p.sendMessage(Main.prefix + ChatColor.DARK_PURPLE + "View Valid Commands With: " + ChatColor.ITALIC + "/pr help");
						
					}
					
				}
				
			} 
			
		}
		
		return false;
	}

}
