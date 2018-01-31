import java.io.*;

public class ReadLine {

    private static String srcFile = "/var/log/nginx/access.log";

    public static void main(String[] argv) throws FileNotFoundException {
        LineNumberReader lineNumberReader = new LineNumberReader(new FileReader(srcFile));
        System.out.println("lines : " + lineNumberReader.lines().count());

    }
}
