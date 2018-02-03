package com.fenlan.storm.storm;

import com.fenlan.storm.regx.UserAgent;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class CounterBolt extends BaseRichBolt {

    Map<String, Integer> status_counter, system_counter;
    private OutputCollector collector;
    private static Logger logger = Logger.getLogger("CounterBolt");

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.status_counter = new HashMap<String, Integer>();
        this.system_counter = new HashMap<String, Integer>();
        this.collector = outputCollector;
    }

    @Override
    public void execute(Tuple tuple) {
        String item = tuple.getString(0);
        String value = tuple.getString(1).split("##")[1];

        // http 状态码统计
        if (item.equals("status")) {
            if (!status_counter.containsKey(value)) {
                status_counter.put(value, 1);
            } else {
                Integer c = status_counter.get(value) + 1;
                status_counter.put(value, c);
            }
            logger.info(value + " : " + status_counter.get(value));
        }
        else if (item.equals("http_user_agent")) {
            String system = UserAgent.systemRegx(value);
            if (!system_counter.containsKey(system)) {
                system_counter.put(system, 1);
            } else {
                Integer c = system_counter.get(system) + 1;
                system_counter.put(system, c);
            }
            logger.info(system + " : " + system_counter.get(system));
        }
        collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("item"));
    }
}
