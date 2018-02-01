package com.fenlan.storm.storm;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;

public class NginxStorm {
    public static void main(String[] argv) throws InterruptedException {

        Config config = new Config();
        config.setDebug(true);

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("LogSpout", new LogSpout(), 2);
        builder.setBolt("SpliteBolt", new SpliteBolt(), 4);

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("NginxLog", config, builder.createTopology());
        Thread.sleep(10000);

        cluster.killTopology("NginxLog");
        cluster.shutdown();
    }
}
