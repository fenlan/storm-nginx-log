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
import redis.clients.jedis.Pipeline;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class CounterBolt extends BaseRichBolt {

    private Map<String, Integer> status_counter, system_counter, browser_counter;
    private Map<String, Integer> virtualHost_counter, cityOfIP_counter;
    private OutputCollector collector;
    private static String redisHost = RedisProperties.getRedisHost();
    private static int redisPort = RedisProperties.getredisPort();
    private static Jedis jedis = new Jedis(redisHost, redisPort);
    private static Logger logger = Logger.getLogger("CounterBolt");

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.status_counter = new HashMap<>();
        this.system_counter = new HashMap<>();
        this.browser_counter = new HashMap<>();
        this.virtualHost_counter = new HashMap<>();
        this.cityOfIP_counter = new HashMap<>();
        this.collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        String item = tuple.getString(0);
        Pipeline pipelineq = jedis.pipelined();
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
                counter(cityOfIP_counter, city);
                jedis.hset("city_of_ip", city, cityOfIP_counter.get(city).toString());
                break;

            // http 状态码统计
            case "status" :
                counter(status_counter, value);
                jedis.hset("status_code", value, status_counter.get(value).toString());
                break;

            // 客户端信息
            case "http_user_agent" :
                String system = UserAgent.systemRegx(value);
                String browser = UserAgent.browserRegx(value);

                // 客户端系统类型统计
                counter(system_counter, system);

                // 客户端浏览器类型统计
                counter(browser_counter, browser);
                jedis.hset("http_user_agent_system", system, system_counter.get(system).toString());
                jedis.hset("http_user_agent_browser", browser, browser_counter.get(browser).toString());
                break;

            // virtual_host 统计
            case "virtual_host" :
                counter(virtualHost_counter, value);
                jedis.hset("virtual_host", value, virtualHost_counter.get(value).toString());
                break;

            default:
        }
        collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("item"));
    }

    public static void counter(Map<String, Integer> map, String value) {
        if (!map.containsKey(value)) {
            map.put(value, 1);
        } else {
            Integer c = map.get(value) + 1;
            map.put(value, c);
        }
    }
}
