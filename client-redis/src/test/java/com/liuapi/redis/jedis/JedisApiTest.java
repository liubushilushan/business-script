package com.liuapi.redis.jedis;

import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

public class JedisApiTest {
    /**
     * redis锁： 无法实现可重入
     */
    @Test
    void testSet(){
        try(Jedis jedis = new Jedis("localhost");){
            String result = jedis.set("user1.lock", "1", SetParams.setParams().nx().ex(30));
            if(result == null){
                System.out.println("acquire lock fail");
            }else{
                System.out.println("acquire lock success");
            }
        }
    }

    @Test
    void assertNull(){
        Object obj = null;
        System.out.println("How to print null?");
        System.out.println(obj);
        System.out.println("below is.");
        System.out.println("How to print `null`?");
        System.out.println("null");
        System.out.println("below is.");
    }
}
