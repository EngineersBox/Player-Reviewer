package me.engineersbox.playerrev.mysql;

import me.engineersbox.playerrev.AbstractFile;
import me.engineersbox.playerrev.Main;

public class SQLConfig extends AbstractFile {

    public SQLConfig(Main main) {
        
        super(main, "applications.yml");
       
    }
    public static String getHOSTNAME() {
    	return config.getString("User-Details.HOSTNAME");
    }
    
    public static String getPORT() {
    	return config.getString("User-Details.PORT");
    }
    
    public static String getDATABASE() {
    	return config.getString("User-Details.DATABASE");
    }
    
    public static String getUSER() {
    	return config.getString("User-Details.USER");
    }
    
    public static String getPASS() {
    	return config.getString("User-Details.PASS");
    }

}
