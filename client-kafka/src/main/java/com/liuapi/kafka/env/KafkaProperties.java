package com.liuapi.kafka.env;

import com.liuapi.property.PropertyReader;
import lombok.Data;

@Data
public class KafkaProperties {
    String brokers;

    public String getBrokers() {
        return PropertyReader.get("kafak.bootstrap.servers");
    }

    public void setBrokers(String brokers) {
        this.brokers = brokers;
    }
}
