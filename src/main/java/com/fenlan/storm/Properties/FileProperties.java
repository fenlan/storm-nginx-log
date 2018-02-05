package com.fenlan.storm.Properties;

import java.util.ResourceBundle;

public class FileProperties {

    public static String getLogPath() {

        ResourceBundle resourceBundle = ResourceBundle.getBundle("application");

        return resourceBundle.getString("logFile.path");
    }
    public static String getGeoLitePath() {

        ResourceBundle resourceBundle = ResourceBundle.getBundle("application");

        return resourceBundle.getString("geolite2City.path");
    }
}
