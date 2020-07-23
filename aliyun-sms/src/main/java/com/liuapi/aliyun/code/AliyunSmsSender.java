package com.liuapi.aliyun.code;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.google.gson.Gson;
import com.liuapi.aliyun.code.env.AliyunSmsResponse;
import com.liuapi.aliyun.code.env.SmsProperties;

public class AliyunSmsSender {

    private static SmsProperties smsProperties = new SmsProperties();

    private static IAcsClient client;

    static{
        DefaultProfile profile = DefaultProfile.getProfile(smsProperties.getRegionId(),smsProperties.getAccessKeyId(),
                smsProperties.getAccessKeySecret());
        client = new DefaultAcsClient(profile);
    }

    public static void sendCode(String mobile, String code){
        CommonRequest request = new CommonRequest();
        request.setMethod(MethodType.POST);
        request.setDomain(smsProperties.getDomain());
        request.setAction(smsProperties.getAction());
        request.putQueryParameter("RegionId", smsProperties.getRegionId());
        request.putQueryParameter("SignName", smsProperties.getSignName());
        request.putQueryParameter("PhoneNumbers", mobile);
        request.putQueryParameter("Version", "2017-05-25");
        request.putQueryParameter("TemplateCode", smsProperties.getTemplateId());
        request.putQueryParameter("TemplateParam", "{\"code\":\""+code+"\"}");
        try {
            CommonResponse response = client.getCommonResponse(request);
            String data = response.getData();
            AliyunSmsResponse resp = new Gson().fromJson(data, AliyunSmsResponse.class);
            if("OK".equals(resp.getCode())){
                System.out.println("OK");
            }else{
                System.out.println("error:"+data+" phone:"+mobile+" pwd:"+code);
            }
        } catch (Exception e) {
            System.out.println("error:"+e.getMessage()+" phone:"+mobile+" pwd:"+code);
        }
    }
}
