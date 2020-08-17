package com.liuapi.redis.jedis;

import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

public class CopyOnWriteModeTest {
    /**
     * 如何用CopyOnWrite的机制来修改Key的值
     */
    @Test
    void modifyKeyValue(){
        try (Jedis jedis = new Jedis("localhost");) {
            // 1）数据存储至keyTmp
            // 2）rename keyTmp key 该命令会覆盖原先key的值

            // 这是一种copyOnWrite的机制
            jedis.set("keyTmp","hi");
            jedis.rename("keyTmp", "key");
        }
    }
}
