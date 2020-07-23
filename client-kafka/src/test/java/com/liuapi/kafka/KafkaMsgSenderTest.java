package com.liuapi.kafka;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.Scanner;

class KafkaMsgSenderTest {
    /**
     * 解析调度系统的主数据并存储至kafka
     */
    public static void main(String[] args) {
        // 控制台中输入绝对路径
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入调度系统log文件全路径!");
        while (scanner.hasNext()) {
            String line = scanner.next();
            if("quit".equalsIgnoreCase(line)){
                System.out.println("-------------SAFE QUIT-------------");
                return;
            }
            if(!line.endsWith(".log")){
                System.out.println("-------------该文件名无法解析-------------");
                System.out.println("请输入调度系统log文件全路径!");
                continue;
            }
            String fileName = line;
            // 解析fileName获取topic名
            String merchantId = getMerchantId(fileName);
            if(merchantId.length()!=8){
                System.out.println("-------------该文件名无法解析出商户名-------------");
                continue;
            }
            String topic = "DISPATCH_"+merchantId;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(fileName), "UTF-8"))) {
                String message = null;
                while ((message = reader.readLine()) != null) {
                    // 解析单行
                    int i = message.indexOf("}");
                    String key = message.substring(0, i+1);
                    String value = message.substring(i+1).trim();

                    KafkaClients.sendMsg(topic,key,value);
                }
                System.out.println("本次消息发送完成!");
                System.out.println("请输入调度系统log文件全路径!");
            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }
    @Test
    void testTopicPick(){
        System.out.println(getMerchantId("33010001.log"));
        System.out.println(getMerchantId("c:\\user\\john\\33010001.log"));
    }

    /**
     * param:
     * 1) 33052101.log
     * 2) 33052101-2020-07-22.log
     *
     *
     * @param filePath
     * @return
     */
    private static String getMerchantId(String filePath){
        String fileName = filePath.substring(filePath.lastIndexOf(File.separator) + 1);
        return fileName.substring(0,8);
    }
}