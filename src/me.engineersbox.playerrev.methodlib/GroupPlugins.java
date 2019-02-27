package me.engineersbox.playerrev.methodlib;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;
import me.engineersbox.playerrev.Main;
import me.engineersbox.playerrev.exceptions.InvalidGroupException;
import me.lucko.luckperms.api.Contexts;
import me.lucko.luckperms.api.Group;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.caching.MetaData;

public class GroupPlugins {

	public static List<String> pexGetGroups(Player p) {
		
		PermissionUser user2 = PermissionsEx.getUser(p);
		
		List<String> groups = new ArrayList<String>();
		List<String> g2 = user2.getOwnParentIdentifiers();
		
		for (int i = 0; i < g2.size(); i++) {
			
			groups.add(g2.get(i).toString());
		}
		
		return groups;
		
	}
	
	public static List<String> pexGetGroupPrefixes(Player p) {
		
		PermissionUser user2 = PermissionsEx.getUser(p);
		
		List<String> g2 = user2.getOwnParentIdentifiers();
		List<String> groupPrefix = new ArrayList<String>();
		
		for (int i = 0; i < g2.size(); i++) {
			
			groupPrefix.add(Lib.format(user2.getParents().get(i).getPrefix()));
		}
		
		return groupPrefix;
		
	}
	
	public static void pexAssignGroup(Player p, String rank) throws InvalidGroupException {
		
		try {
			PermissionUser user2 = PermissionsEx.getUser(p);
			user2.addGroup(rank);
		} catch (Exception e) {
			throw new InvalidGroupException(p.getName());
		}
		
	}
	
	public static void lpAssignGroup(Player p, String rank) throws InvalidGroupException {
		
		try {
			User user = Main.LPapi.getUser(p.getUniqueId());
			Group group = Main.LPapi.getGroupManager().getGroup(rank);
			Node node = Main.LPapi.getNodeFactory().makeGroupNode(group).build();
			user.setPermission(node);
		} catch (Exception e) {
			throw new InvalidGroupException(p.getName());
		}
		
	}
	
	public static boolean lpInGroup(Player p, String group) {
		
		User user = Main.LPapi.getUser(p.getUniqueId());
		Set<Group> groups = Main.LPapi.getGroups();
		boolean groupCount = false;
		for (Group cGroup : groups) {
			if ((user.inheritsGroup(cGroup)) && (!cGroup.getName().equalsIgnoreCase(group))) {
				groupCount = true;
			}
		}
		
		return groupCount;
		
	}
	
	public static Integer lpGetGroupCount(Player p) {
		
		User user = Main.LPapi.getUser(p.getUniqueId());
		Set<Group> groups = Main.LPapi.getGroups();
		Integer groupCount = 0;
		for (Group cGroup : groups) {
			if ((user.inheritsGroup(cGroup)) && (!cGroup.getName().equalsIgnoreCase("default"))) {
				groupCount += 1;
			}
		}
		
		return groupCount;
		
	}
	
	public static List<String> lpGetGroups(Player p) {
		
		User user = Main.LPapi.getUser(p.getUniqueId());
		Set<Group> groups = Main.LPapi.getGroups();
		List<String> retGroups = new ArrayList<String>();
		for (Group cGroup : groups) {
			if ((user.inheritsGroup(cGroup)) && (!cGroup.getName().equalsIgnoreCase("default"))) {
				retGroups.add(cGroup.getName());
			}
		}
		
		return retGroups;
	}
	
	public static List<String> lpGetGroupPrefixes(Player p) {
		
		User user = Main.LPapi.getUser(p.getUniqueId());
		Contexts contexts = Main.LPapi.getContextManager().getApplicableContexts(p);
		MetaData metaData;
		Set<Group> groups = Main.LPapi.getGroups();
		List<String> retGroups = new ArrayList<String>();
		for (Group cGroup : groups) {
			if ((user.inheritsGroup(cGroup)) && (!cGroup.getName().equalsIgnoreCase("default"))) {
				metaData = cGroup.getCachedData().getMetaData(contexts);
				if (metaData.getPrefix() != null) {
					retGroups.add(Lib.format(metaData.getPrefix()));
				} else {
					retGroups.add(Lib.format("&f[" + cGroup.getName() + "]"));
				}
			}
		}
		
		return retGroups;
		
	}
	
	public static String lpGetUserPrefix(Player p) {
		
		User user = Main.LPapi.getUser(p.getUniqueId());
		Contexts contexts = Main.LPapi.getContextManager().getApplicableContexts(p);
		MetaData metaData = user.getCachedData().getMetaData(contexts);
		return Lib.format(metaData.getPrefix());
		
	}
	
}
