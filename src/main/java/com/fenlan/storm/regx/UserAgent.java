package com.fenlan.storm.regx;

import java.util.regex.Pattern;

public class UserAgent {


    public static String systemRegx(String record) {
        String Android = ".*Android.*";
        String iPhone = ".*iPhone.*";
        String Mac = ".*Mac OS.*";
        String Windows = ".*Windows.*";
        String Linux = ".*Linux.*";

        // 一段令人窒息的代码，需要改进
        if (Pattern.compile(Android).matcher(record).find())    return "Android";
        else if (Pattern.compile(iPhone).matcher(record).find())    return "iPhone";
        else if (Pattern.compile(Mac).matcher(record).find())   return "Mac OS";
        else if (Pattern.compile(Windows).matcher(record).find())   return "Windows";
        else if (Pattern.compile(Linux).matcher(record).find())     return "Linux";
        else    return "Others";
    }
}
