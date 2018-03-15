import com.fenlan.storm.GeoIP2.AnalyzeIP;
import com.fenlan.storm.Properties.RedisProperties;
import com.fenlan.storm.data.DataAnalyze;
import com.fenlan.storm.regx.UserAgent;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import org.apache.storm.tuple.Values;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadLine {

    private static String srcFile = "/var/log/nginx/access.log";
    private static String redisHost = RedisProperties.getRedisHost();
    private static int redisPort = RedisProperties.getredisPort();
    private static Jedis jedis = new Jedis(redisHost, redisPort);

    public static void main(String[] argv) throws IOException, ParseException {


    }
}
