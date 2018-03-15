package com.fenlan.storm.logSource;

import com.fenlan.storm.Properties.FileProperties;
import com.fenlan.storm.Properties.KafkaProperties;
import org.apache.kafka.clients.producer.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

public class GetLogRecord {

    private static long index = 0;
    private static String srcFile = FileProperties.getLogPath();
    private static String kafkaClusterIP = KafkaProperties.getKafkaCluster();
    private static String kafkaTopic = KafkaProperties.getKafkaTopic();

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaClusterIP);//kafka clusterIP
        props.put("acks", "1");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = new KafkaProducer<>(props);

        while (true) {
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
                    producer.send(new ProducerRecord<String, String>(kafkaTopic, Long.toString(i), record), new Callback() {
                        public void onCompletion(RecordMetadata metadata, Exception e) {
                            if (e != null)
                                e.printStackTrace();
                            System.out.println("The offset of the record we just sent is: " + metadata.offset());
                        }
                    });
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
    }
}
