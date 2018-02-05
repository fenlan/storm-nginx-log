package com.fenlan.storm.Properties;

import java.util.ResourceBundle;

public class RedisProperties {

    public static String getRedisHost() {

        ResourceBundle resourceBundle = ResourceBundle.getBundle("application");

        return resourceBundle.getString("redis.host");
    }
    public static int getredisPort() {

        ResourceBundle resourceBundle = ResourceBundle.getBundle("application");

        return Integer.parseInt(resourceBundle.getString("redis.port"));
    }
}
