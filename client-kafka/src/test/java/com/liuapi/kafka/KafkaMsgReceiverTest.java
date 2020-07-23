package com.liuapi.kafka;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

class KafkaMsgReceiverTest {

    @Test
    void receiveDispatcherMsg() throws FileNotFoundException {
        KafkaClients.receiveMsgToFile("DISPATCH_53010001","C:\\Users\\Administrator\\Desktop\\DISPATCH_53010001.log");
    }
    @Test
    void receiveDispatcherScheduleMsg() throws FileNotFoundException {
        KafkaClients.receiveMsgToFile("DISPATCH_SCHEDULE_37028301","C:\\Users\\Administrator\\Desktop\\DISPATCH_SCHEDULE_37028301.log");
    }
}