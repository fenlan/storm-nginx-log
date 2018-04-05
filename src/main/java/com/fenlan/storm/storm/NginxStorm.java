package com.fenlan.storm.storm;

import com.fenlan.storm.Properties.KafkaProperties;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.kafka.*;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import java.util.UUID;

public class NginxStorm {
    private static String kafkaTopic = KafkaProperties.getKafkaTopic();
    private static String zookeeperHosts = KafkaProperties.getZookeeperHosts();

    public static void main(String[] argv) throws InterruptedException, InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        Config config = new Config();
        config.setDebug(true);
        config.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);
        String zkConnString = zookeeperHosts;
        String topic = kafkaTopic;
        BrokerHosts hosts = new ZkHosts(zkConnString);

        SpoutConfig kafkaSpoutConfig = new SpoutConfig (hosts, topic, "/" + topic,
                UUID.randomUUID().toString());
        kafkaSpoutConfig.bufferSizeBytes = 1024 * 1024 * 4;
        kafkaSpoutConfig.fetchSizeBytes = 1024 * 1024 * 4;
        kafkaSpoutConfig.scheme = new SchemeAsMultiScheme(new StringScheme());

        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("LogSpout", new KafkaSpout(kafkaSpoutConfig));
        builder.setBolt("SpliteBolt", new SpliteBolt(), 1).shuffleGrouping("LogSpout");
        builder.setBolt("CounterBolt", new CounterBolt(), 1)
                .fieldsGrouping("SpliteBolt", new Fields("item"));

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("LogNginx", config, builder.createTopology());
    }
}
