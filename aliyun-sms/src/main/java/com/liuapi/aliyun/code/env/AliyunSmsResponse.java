package com.liuapi.aliyun.code.env;

import lombok.Data;

@Data
public class AliyunSmsResponse {
	private String Message;
	private String RequestId;
	private String BizId;
	private String Code;
}
