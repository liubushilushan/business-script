package com.liuapi.property;

import org.junit.jupiter.api.Test;

import java.util.Objects;

class PropertyReaderTest {
    @Test
    void testLoadKey(){
        System.out.println(PropertyReader.get("aliyun.sms.accessKeyId"));
        System.out.println(PropertyReader.get("aliyun.oss.accesskeySecret"));
        System.out.println(Objects.equals(PropertyReader.get("aliyun.oss.xx"),null));
        System.out.println("---------end----------");
    }
}