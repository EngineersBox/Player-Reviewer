package me.engineersbox.playerrev.enums;

import java.util.HashMap;
import java.util.Map;

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
