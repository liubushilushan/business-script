package com.liuapi.redis.jedis;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.Arrays;
import java.util.List;

public class ShardedJedisPoolTest {
    public static void main(String[] args) {
        //连接池配置
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(1);
        poolConfig.setMaxWaitMillis(200);
        poolConfig.setTestOnBorrow(false);
        poolConfig.setTestOnReturn(false);
 
        //分片信息
        JedisShardInfo shardInfo1 = new JedisShardInfo("localhost", 6379, 500);
        JedisShardInfo shardInfo2 = new JedisShardInfo("192.168.110.12", 6379, 500);
 
        //根据分片信息创建连接池
        List<JedisShardInfo> infoList = Arrays.asList(shardInfo1, shardInfo2);
        ShardedJedisPool jedisPool = new ShardedJedisPool(poolConfig, infoList);
 
        ShardedJedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set("test", "this is a test");
        } finally {
            if(jedis!=null)
                jedis.close();
        }
 
        try {
            jedis = jedisPool.getResource();
            String value = jedis.get("test");
            System.out.println(value);
        } finally {
            if(jedis!=null)
                jedis.close();
        }
    }
}