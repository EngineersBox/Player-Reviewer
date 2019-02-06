package me.engineersbox.playerrev;

import java.util.List;

public class SQLConfig extends SQLAbstractFile {

    public SQLConfig(Main main) {
        
        super(main, "sqlconfig.yml");
       
    }
    
    private static List<String> getDetails() {
    	return config.getStringList("User-Details");
    }
    
    public static String getDBURL() {
    	List<String> details = getDetails();
    	return details.get(0).substring(details.get(0).lastIndexOf(":") + 1);
    }
    
    public static String getUSER() {
    	List<String> details = getDetails();
    	return details.get(1).substring(details.get(1).lastIndexOf(":") + 1);
    }
    
    public static String getPASS() {
    	List<String> details = getDetails();
    	return details.get(2).substring(details.get(2).lastIndexOf(":") + 1);
    }

}
