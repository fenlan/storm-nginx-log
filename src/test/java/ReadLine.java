import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    }
}
