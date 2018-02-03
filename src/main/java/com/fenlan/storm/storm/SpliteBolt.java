package com.fenlan.storm.storm;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
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
        Pipeline pipelineq = jedis.pipelined();

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

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
            LocalDateTime dateTime = LocalDateTime.parse(time_local, formatter);
            Long milli_time = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

//            pipelineq.sadd("remote_addr", milli_time + "##" + remote_addr);
//            pipelineq.sadd("request", milli_time + "##" + request);
//            pipelineq.sadd("status", milli_time + "##" + status);
//            pipelineq.sadd("body_bytes_sent", milli_time + "##" + body_bytes_sent);
//            pipelineq.sadd("http_user_agent", milli_time + "##" + http_user_agent);

            collector.emit(new Values("remote_addr", milli_time + "##" + remote_addr));
            collector.emit(new Values("request", milli_time + "##" + request));
            collector.emit(new Values("status", milli_time + "##" + status));
            collector.emit(new Values("body_bytes_sent", milli_time + "##" + body_bytes_sent));
            collector.emit(new Values("http_user_agent", milli_time + "##" + http_user_agent));

            logger.info("#########################");
        }
        else {
            System.out.println("NO MATCH");
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("item", "value"));
    }
}
