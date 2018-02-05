package com.fenlan.storm.regx;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class UserAgent {

    private static final Map<String, String> OS;
    static
    {
        OS = new LinkedHashMap<String, String>();
        OS.put("Android", "Android");
        OS.put("Windows", "Windows");
        OS.put("iPhone", "iOS");
        OS.put("iPad", "iOS");
        OS.put("iPod", "iOS");
        OS.put("AppleTV", "iOS");
        OS.put("Mac OS", "Mac");
        OS.put("Debian", "Linux");
        OS.put("Ubuntu", "Linux");
        OS.put("Mint", "Linux");
        OS.put("SUSE", "Linux");
        OS.put("Mandriva", "Linux");
        OS.put("Red Hat", "Linux");
        OS.put("Gentoo", "Linux");
        OS.put("CentOS", "Linux");
        OS.put("PCLinuxOS", "Linux");
        OS.put("Linux", "Linux");
        //匹配没有收录的系统
        OS.put("", "Others");
    }

    // 收录部分 goaccess 收录的浏览器,其他浏览器有待收录
    private static final Map<String, String> browser;
    static
    {
        browser = new LinkedHashMap<String, String>();
        browser.put("IEMobile", "MSIE");
        browser.put("MSIE", "MSIE");
        browser.put("Trident/7.0", "MSIE");
        browser.put("Edge", "MSIE");

        browser.put("Opera Mini", "Opera");
        browser.put("Opera Mobi", "Opera");
        browser.put("Opera", "Opera");
        browser.put("OPR", "Opera");
        browser.put("OPiOS", "Opera");
        browser.put("Coast", "Opera");

        browser.put("AppleNewsBot", "Feeds");
        browser.put("Bloglines", "Feeds");
        browser.put("Digg Feed Fetcher", "Feeds");
        browser.put("Feedbin", "Feeds");
        browser.put("FeedHQ", "Feeds");
        browser.put("Feedly", "Feeds");
        browser.put("Flipboard", "Feeds");
        browser.put("Netvibes", "Feeds");
        browser.put("NewsBlur", "Feeds");
        browser.put("PinRSS", "Feeds");
        browser.put("WordPress.com Reader", "Feeds");
        browser.put("YandexBlogs", "Feeds");

        browser.put("Googlebot", "Crawlers");
        browser.put("AdsBot-Google", "Crawlers");
        browser.put("AppEngine-Google", "Crawlers");
        browser.put("Mediapartners-Google", "Crawlers");
        browser.put("Google", "Crawlers");
        browser.put("WhatsApp", "Crawlers");

        browser.put("Iceweasel", "Firefox");
        browser.put("Firefox", "Firefox");

        // Chrome 匹配必须在 Safari 前面
        browser.put("HeadlessChrome", "Chrome");
        browser.put("Chrome", "Chrome");
        browser.put("CriOS", "Chrome");

        browser.put("Safari", "Safari");
        browser.put("Mozilla", "Others");
        // 匹配没有收录的浏览器
        browser.put("", "Others");

    }

    public static String systemRegx(String record) {
        String result;
        Iterator iterator = OS.entrySet().iterator();

        result = iterator(iterator, record);
        return result;
    }

    public static String browserRegx(String record) {
        String result;
        Iterator iterator = browser.entrySet().iterator();

        result = iterator(iterator, record);
        return result;
    }

    private static String iterator(Iterator iterator, String record) {
        String result = null;
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            boolean match_boolen = Pattern.compile(".*" + entry.getKey() + ".*").matcher(record).find();

            if (match_boolen) {
                result = entry.getValue().toString();
                break;
            }
        }

        return result;
    }
}
