package com.liuapi.oss.env;

import com.liuapi.property.PropertyReader;

public class OssProperty {

    public String getEndpoint() {
        return PropertyReader.get("aliyun.oss.endpoint");
    }

    public String getAccessKeyId() {
        return PropertyReader.get("aliyun.oss.accesskeyId");
    }

    public String getAccessKeySecret() {
        return PropertyReader.get("aliyun.oss.accesskeySecret");
    }

    public String getBucketName() {
        return PropertyReader.get("aliyun.oss.bucketName");
    }
}
