package com.fenlan.storm.storm;

import com.fenlan.storm.GeoIP2.AnalyzeIP;
import com.fenlan.storm.Properties.RedisProperties;
import com.fenlan.storm.regx.UserAgent;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import redis.clients.jedis.Jedis;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CounterBolt extends BaseRichBolt {

    private OutputCollector collector;
    private static String redisHost = RedisProperties.getRedisHost();
    private static int redisPort = RedisProperties.getredisPort();
    private static Jedis jedis = new Jedis(redisHost, redisPort);

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        String item = tuple.getString(0);
        String value = tuple.getString(1).split("##")[1];
        String city;

        switch (item) {
            // 统计客户端地址所在城市
            case "remote_addr" :
                try {
                    city = AnalyzeIP.cityOfIP(value);
                    if (city == null)   city = "Unknown";
                } catch (IOException e) {
                    city = "Unknown";
                } catch (GeoIp2Exception e) {
                    city = "Unknown";
                }
                jedis.hincrBy("city_of_ip", city, 1);
                break;

            // http 状态码统计
            case "status" :
                // 将状态码分为 1** 2** 3** 4** 5**
                Integer status = Integer.parseInt(value) / 100;
                String statusStr = status + "**";
                jedis.hincrBy("status_code", statusStr, 1);
                break;

            // 客户端信息
            case "http_user_agent" :
                String system = UserAgent.systemRegx(value);
                String browser = UserAgent.browserRegx(value);

                // 客户端系统类型统计
                // 客户端浏览器类型统计
                jedis.hincrBy("http_user_agent_system", system, 1);
                jedis.hincrBy("http_user_agent_browser", browser, 1);
                break;

            // virtual_host 统计
            case "virtual_host" :
                String regx = "([^/]*)(\\/\\/[^/]*\\/)([^ ]*)";
                Pattern pattern = Pattern.compile(regx);
                Matcher matcher = pattern.matcher(value);
                if (matcher.find()) {
                    String matcherString = matcher.group(2);
                    String virtual_host = matcherString.substring(2, matcherString.length()-1);
                    jedis.hincrBy("virtual_host", virtual_host, 1);
                }
                break;

            default:
        }
        collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("item"));
    }
}