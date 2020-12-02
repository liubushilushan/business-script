package com.liuapi.redis.jedis;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

/**
 * redis-benchmark 10万 qps
 */
public class TPSTester {

    /**
     * 秒杀性能测试，4C8G单机上部署redis，tps可达4万
     * @throws InterruptedException
     */
    @Test
    void testTps() throws InterruptedException {
        JedisPool jedisPool = testPool();
        int num  = 100;
        int batchNum = 10000;
        // 起num个线程
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                num,num,0, TimeUnit.DAYS,new LinkedBlockingDeque<>()
        );
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        long start = System.currentTimeMillis();
        CyclicBarrier barrier = new CyclicBarrier(num);
        CountDownLatch latch  = new CountDownLatch(num);
        for(int i = 0;i<num;i++){
            executor.execute(
                    ()->{
                        Jedis jedis = jedisPool.getResource();
                        try {
                            barrier.await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (BrokenBarrierException e) {
                            e.printStackTrace();
                        }
                        int times = batchNum;// 1万次
                        try {
                            while (times-->0) {
                                int buyNumber = 1;
                                /**
                                 *  String lua = "if (redis.call('get',KEYS[1])-ARGV[1])>=0 then" +
                                 *                     " return redis.call('decrby',KEYS[1],ARGV[1])" +
                                 *                     " else" +
                                 *                     " return -1" +
                                 *                     " end";
                                 */
//                                jedis.evalsha("d5a009b70fd2da18a58eb1ecf20ef5fac54b8024", Lists.newArrayList("goods1.number"), Lists.newArrayList(buyNumber + ""));
                                String plan = jedis.get("route");
                            }

                        } finally {// 关闭连接
                            jedis.close();
                            latch.countDown();
                        }
                    }
            );
        }
        latch.await();
        executor.shutdown();
        long end = System.currentTimeMillis();
        System.out.println("main execute");
        long qps = batchNum * num * 1000 / (end - start);
        System.out.println("QPS:"+qps);
    }

    private static JedisPool testPool() {
        JedisPoolConfig poolCfg = new JedisPoolConfig();
        // 最大空闲数
        poolCfg.setMaxIdle(1000);
        poolCfg.setMinIdle(20);
        // 最大连接数
        poolCfg.setMaxTotal(2000);
        // 最大等待毫秒数
        poolCfg.setMaxWaitMillis(200000);
        // 使用配置创建连接池
        JedisPool pool = new JedisPool(poolCfg, "localhost");
        return pool;
    }

}
