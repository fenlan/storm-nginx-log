package com.fenlan.storm.storm;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpliteBolt extends BaseRichBolt {

    private OutputCollector collector;
    private static String redisHost = "localhost";
    private static int redisPort = 6379;
    private static Logger logger = Logger.getLogger("SpliteBolt");
    private static Jedis jedis = new Jedis(redisHost, redisPort);

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        long index = tuple.getLong(0);
        String record = tuple.getString(1);

        logger.info(index + " : " + record);
        String regx = "([^ ]*) ([^ ]*) ([^ ]*) (\\[.*\\]) (\\\".*?\\\") (-|[0-9]*) (-|[0-9]*) (\\\".*?\\\") (\\\".*?\\\")";
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher = pattern.matcher(record);

        if (matcher.find()) {
            String remote_addr = matcher.group(1);
            String time_local = matcher.group(4).substring(1, matcher.group(4).length()-1);
            String request = matcher.group(5);
            String status = matcher.group(6);
            String body_bytes_sent = matcher.group(7);
            String http_user_agent = matcher.group(9);

            jedis.sadd("remote_addr", remote_addr);

        }
        else {
            System.out.println("NO MATCH");
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("Splite"));
    }
}
