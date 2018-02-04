package com.fenlan.storm.storm;

import com.fenlan.storm.GeoIP2.AnalyzeIP;
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
    private static String redisHost = "localhost";
    private static int redisPort = 6379;
    private static Jedis jedis = new Jedis(redisHost, redisPort);
    private static Logger logger = Logger.getLogger("CounterBolt");

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.status_counter = new HashMap<String, Integer>();
        this.system_counter = new HashMap<String, Integer>();
        this.browser_counter = new HashMap<String, Integer>();
        this.virtualHost_counter = new HashMap<String, Integer>();
        this.cityOfIP_counter = new HashMap<String, Integer>();
        this.collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        String item = tuple.getString(0);
        Pipeline pipelineq = jedis.pipelined();
        String value = tuple.getString(1).split("##")[1];
        String city;

        // 统计客户端地址所在城市
        if (item.equals("remote_addr")) {
            try {
                city = AnalyzeIP.cityOfIP(value);
            } catch (IOException e) {
                city = "Unknown";
            } catch (GeoIp2Exception e) {
                city = "Unknown";
            }
            if (!cityOfIP_counter.containsKey(city)) {
                cityOfIP_counter.put(city, 1);
            } else {
                Integer c = cityOfIP_counter.get(city) + 1;
                cityOfIP_counter.put(city, c);
            }
            jedis.hset("city_of_ip", city, cityOfIP_counter.get(city).toString());
        }

        // http 状态码统计
        else if (item.equals("status")) {
            if (!status_counter.containsKey(value)) {
                status_counter.put(value, 1);
            } else {
                Integer c = status_counter.get(value) + 1;
                status_counter.put(value, c);
            }
            jedis.hset("status_code", value, status_counter.get(value).toString());
        }

        // 客户端信息
        else if (item.equals("http_user_agent")) {
            String system = UserAgent.systemRegx(value);
            String browser = UserAgent.browserRegx(value);

            // 客户端系统类型统计
            if (!system_counter.containsKey(system)) {
                system_counter.put(system, 1);
            } else {
                Integer c = system_counter.get(system) + 1;
                system_counter.put(system, c);
            }
            // 客户端浏览器类型统计
            if (!browser_counter.containsKey(browser)) {
                browser_counter.put(browser, 1);
            } else {
                Integer c = browser_counter.get(browser) + 1;
                browser_counter.put(browser, c);
            }
            jedis.hset("http_user_agent_system", system, system_counter.get(system).toString());
            jedis.hset("http_user_agent_browser", browser, browser_counter.get(browser).toString());
        }

        // virtual_host 统计
        else if (item.equals("virtual_host")) {
            if (!virtualHost_counter.containsKey(value)) {
                virtualHost_counter.put(value, 1);
            } else {
                Integer c = virtualHost_counter.get(value) + 1;
                virtualHost_counter.put(value, c);
            }
            jedis.hset("virtual_host", value, virtualHost_counter.get(value).toString());
            logger.info(value + " : " + jedis.hget("virtual_host", value));
        }
        collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("item"));
    }
}
