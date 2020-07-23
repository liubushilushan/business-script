package com.liuapi.aliyun.code.env;

import com.liuapi.property.PropertyReader;

public class SmsProperties {

	public String getRegionId() {
		return PropertyReader.get("aliyun.sms.regionId");
	}

	public String getAccessKeyId() {
		return PropertyReader.get("aliyun.sms.accessKeyId");
	}

	public String getAccessKeySecret() {
		return PropertyReader.get("aliyun.sms.accessKeySecret");
	}

	public String getDomain() {
		return PropertyReader.get("aliyun.sms.domain");
	}

	public String getAction() {
		return PropertyReader.get("aliyun.sms.action");
	}

	public String getSignName() {
		return PropertyReader.get("aliyun.sms.signName");
	}

	public String getTemplateId() {
		return PropertyReader.get("aliyun.sms.templateId");
	}
}
