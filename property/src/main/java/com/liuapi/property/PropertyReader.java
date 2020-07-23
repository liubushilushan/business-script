package com.liuapi.property;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class PropertyReader {
    private static Properties properties;

    static {
        // 读取 company.properties 文件的内容
        try (Reader reader = new InputStreamReader(
                PropertyReader.class.getClassLoader().getResourceAsStream("company.properties")
                , "UTF-8"
        )) {
            properties = new Properties();
            properties.load(reader);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return (String) properties.get(key);
    }
}
