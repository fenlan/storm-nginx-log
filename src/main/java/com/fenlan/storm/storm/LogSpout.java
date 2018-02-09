package com.fenlan.storm.storm;

import com.fenlan.storm.Properties.FileProperties;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class LogSpout extends BaseRichSpout {

    private SpoutOutputCollector collector;
    private TopologyContext context;
    private static String srcFile = FileProperties.getLogPath();
    private static long index = 0;

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.collector = spoutOutputCollector;
        this.context = topologyContext;
    }

    @Override
    public void nextTuple() {
        long lines = 0;
        String record = null;

        try {
            Stream<String> fileStream = Files.lines(Paths.get(srcFile));
            lines = fileStream.count();
            fileStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (lines != index) {
            if (lines < index)      index = 0;
            for (long i = index; i < lines; i++) {
                try {
                    List<String> fileStream = Files.readAllLines(Paths.get(srcFile));
                    record = fileStream.get((int)i);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.collector.emit(new Values(i, record));
            }
            index = lines;
        }
        else {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("index", "record"));
    }
}
