package com.liuapi.kafka;

import com.google.common.collect.Maps;
import com.liuapi.kafka.env.KafkaProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

/**
 * Kafka客户端
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/7/22
 */
@Slf4j
public class KafkaClients {
    private final static KafkaProperties properties = new KafkaProperties();
    private final static KafkaConsumer<String, String> consumer;
    private static final KafkaProducer<String,String> producer;
    static{
        Properties props = new Properties();
        // 必须设置的属性
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBrokers());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
        // 可选设置属性
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "script.client0");
        consumer = new KafkaConsumer<>(props);
    }

    static{
        Map<String,Object> props = Maps.newHashMap();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBrokers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // 确认机制-分区主节点确认即可
        props.put(ProducerConfig.ACKS_CONFIG, "1");
        // 最大延时时间 100ms
        props.put(ProducerConfig.LINGER_MS_CONFIG, 100);
        // 批处理最大消息数 16*1024条
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16*1024);
        producer = new KafkaProducer<>(props);
    }

    public static void receiveMsgToFile(String topic,String abstractFilePath) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(abstractFilePath);
        while(true) {
            consumer.subscribe(Collections.singletonList(topic));
            //  从服务器开始拉取数据
            ConsumerRecords<String, String> records = consumer.poll(1000);
            if(records.isEmpty()){
                continue;
            }
            log.info("-----------收到{}条消息-----------",records.count());
            records.forEach(record -> {
                // 输出只文件
                writer.printf("topic = %s ,partition = %d,offset = %d, key = %s, value = %s", record.topic(), record.partition(),
                        record.offset(), record.key(), record.value());
                writer.println();
            });
            writer.flush();
            consumer.commitSync();
        }
    }


    public static void sendMsg(String topic,String key,String value) {
        producer.send(new ProducerRecord(topic,key,value));
    }
}
