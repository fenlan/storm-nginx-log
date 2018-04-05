package com.fenlan.storm.Properties;

import java.util.ResourceBundle;

public class KafkaProperties {

    public static String getKafkaCluster() {

        ResourceBundle resourceBundle = ResourceBundle.getBundle("application");

        return resourceBundle.getString("kafkaCluster.hosts");
    }

    public static String getKafkaTopic() {

        ResourceBundle resourceBundle = ResourceBundle.getBundle("application");

        return resourceBundle.getString("kafkaTopic");
    }

    public static String getZookeeperHosts() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("application");

        return resourceBundle.getString("zookeeper.hosts");
    }
}
