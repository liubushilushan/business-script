package com.liuapi.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.internal.OSSHeaders;
import com.aliyun.oss.model.*;
import com.liuapi.oss.env.OssProperty;
import com.liuapi.oss.env.PutObjectProgressListener;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
@Slf4j
public class OssUtil {
    private OssProperty ossProperty = new OssProperty();

    private OSS ossClient;

    public void init() throws Exception {
        ossClient = new OSSClientBuilder().build(ossProperty.getEndpoint(), ossProperty.getAccessKeyId(), ossProperty.getAccessKeySecret());
        String bucketName = ossProperty.getBucketName();
        // 验证bucket是否存在
        boolean exists = ossClient.doesBucketExist(bucketName);
        if (exists) {
            return;
        }
        // 当前bucket不存在
        CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
        createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
        createBucketRequest.setStorageClass(StorageClass.Standard);
        ossClient.createBucket(createBucketRequest);
        log.info("create a new bucket named {}",bucketName);
        // 设置bucket所有符合backup/前缀的对象的过期时间为60天
        SetBucketLifecycleRequest request = new SetBucketLifecycleRequest(bucketName);
        String ruleId1 = "rule1";
        String matchPrefix1 = "backup/";
        LifecycleRule rule = new LifecycleRule(ruleId1, matchPrefix1, LifecycleRule.RuleStatus.Enabled);
        LifecycleRule.AbortMultipartUpload abortMultipartUpload = new LifecycleRule.AbortMultipartUpload();
        abortMultipartUpload.setExpirationDays(60);
        rule.setAbortMultipartUpload(abortMultipartUpload);
        request.AddLifecycleRule(rule);
        ossClient.setBucketLifecycle(request);
    }

    public void shutdown(){
        if(null == ossClient){
            return;
        }
        ossClient.shutdown();
    }

    public void upload(String objectName, String content) {
        byte[] bytes = new byte[0];
        try {
            bytes = content.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        // 存储一个文件
        PutObjectRequest putObjectRequest = new PutObjectRequest(ossProperty.getBucketName(), objectName, new ByteArrayInputStream(bytes));
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
        metadata.setObjectAcl(CannedAccessControlList.PublicRead);
        putObjectRequest.setMetadata(metadata);
        // 添加上传进度条监听器
        putObjectRequest.withProgressListener(new PutObjectProgressListener(objectName,bytes.length));
        ossClient.putObject(putObjectRequest);
    }

    public String getDownloadUrl(String objectName){
        String endpoint = ossProperty.getEndpoint();
        String bucketName = ossProperty.getBucketName();

        String protocal = endpoint.substring(0,endpoint.indexOf("://"));
        String domain = endpoint.substring(protocal.length()+3);
        return protocal+"://"+bucketName+"."+domain+"/"+objectName;
    }
}
