package me.engineersbox.playerrev.enums;

import java.util.HashMap;
import java.util.Map;

public enum Status {
	APPROVED("Approved"),
	DENIED("Denied"),
	AWAITING_REVIEW("Awaiting Review");
	
	private String[] aliases;

    private static Map<String, Status> status = new HashMap<>();
    static {
        for (Status vals : Status.values()) {
            for (String alias : vals.aliases) {
            	status.put(alias, vals);
            }
        }
    }

    private Status(String ... aliases) {
        this.aliases = aliases;
    }

    public static Status valueOfByAlias(String alias) {
    	Status vals = status.get(alias);
        if (vals == null) {
            throw new IllegalArgumentException("No enum alias " + Status.class.getCanonicalName() + "." + alias);
        }
        return vals;
    }
    
    public static boolean isValid(String alias) {
    	Status vals = status.get(alias);
    	if (vals == null) {
    		return false;
    	} else {
    		return true;
    	}
    }
}
