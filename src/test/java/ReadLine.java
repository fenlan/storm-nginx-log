import com.fenlan.storm.GeoIP2.AnalyzeIP;
import com.fenlan.storm.regx.UserAgent;
import com.maxmind.geoip2.exception.GeoIp2Exception;

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

    public static void main(String[] argv) throws IOException, ParseException {
        String record = Files.readAllLines(Paths.get(srcFile)).get(0);
        String regx = "([^ ]*) ([^ ]*) ([^ ]*) (\\[.*\\]) (\\\".*?\\\") (-|[0-9]*) (-|[0-9]*) (\\\".*?\\\") (\\\".*?\\\")";
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher = pattern.matcher(record);

        if (matcher.find()) {
            String http_user_agent = matcher.group(9);
            System.out.println(http_user_agent);

            String system = http_user_agent.split(" ")[2];
            System.out.println(system);
        } else {
            System.out.println("NO MATCH");
        }

        String debian = "Debian";
        String record1 = "Debian APT-HTTP/1.3 (1.2.24) Chrome";
        String regx1 = ".*" + debian + ".*";
        Pattern pattern1 = Pattern.compile(regx1);
        Matcher matcher1 = pattern1.matcher(record1);
        if (matcher1.find()) {
            System.out.println("ok");
        } else {
            System.out.println(regx1 + " failed");
        }

        System.out.println(UserAgent.browserRegx(record1));

        try {
            System.out.println(AnalyzeIP.cityOfIP("23.83.230.171"));
        } catch (GeoIp2Exception e) {
            System.out.println("ip 没有找到");
        }

        String time_local = matcher.group(4).substring(1, matcher.group(4).length()-1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
        LocalDateTime dateTime = LocalDateTime.parse(time_local, formatter);
        System.out.println("date : " + dateTime + " " + dateTime.getMonthValue() + " " + dateTime.getDayOfMonth());
        System.out.println(20180208 / 10000);
        System.out.println(20180208 % 100);
        System.out.println((20180208 - 20180208 / 10000 * 10000) / 100);
    }
}
