package com.app.awsconnect.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AwsUtility {
    public static String is2String(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        StringBuffer sb = new StringBuffer();
        char[] b =  new char[1024];
        int line;
        while(0 <= (line = reader.read(b))){
            sb.append(b, 0, line);
        }
        return sb.toString();
    }

    public static Map<String, String> mapObject2mapString(Map<String, Object> map){

        Map<String, String> newMap = new HashMap<>();

        for(Map.Entry<String, Object> entry : map.entrySet()){
            newMap.put(entry.getKey().toString(), entry.getValue().toString());
        }
        return  newMap;
    }
}
