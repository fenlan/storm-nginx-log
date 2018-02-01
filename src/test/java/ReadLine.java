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
            System.out.println(matcher.group(4).substring(1, matcher.group(4).length()-1));
            String time = matcher.group(4).substring(1, matcher.group(4).length()-1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);
            LocalDateTime dateTime = LocalDateTime.parse(time, formatter);
            System.out.println(dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        }
        else {
            System.out.println("NO MATCH");
        }
    }
}
