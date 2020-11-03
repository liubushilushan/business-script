package com.liuapi.mysql;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

/**
 * @auther 柳俊阳
 * @github https://github.com/johnliu1122/
 * @csdn https://blog.csdn.net/qq_35695616
 * @email johnliu1122@163.com
 * @date 2020/8/30
 */
@Slf4j
public class TPSTester {
        /**
     * 等值查询，4C8G 1000 QPS
     * 插入语句，4C8G 646 QPS
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    void testUpdateQps() throws InterruptedException {
        DataSource druidDataSource = dataSource();
        int num  = 8;
        // 起num个线程
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                num,num,0, TimeUnit.DAYS,new LinkedBlockingDeque<>()
        );
        System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        long start = System.currentTimeMillis();
        CyclicBarrier barrier = new CyclicBarrier(num);
        CountDownLatch latch  = new CountDownLatch(num);
        int batchNum = 1000;
        for(int i = 0;i<num;i++){
            executor.execute(
                    ()->{
                        Connection connection = null;
                        try {
                            connection = druidDataSource.getConnection();
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        }
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
                                PreparedStatement preparedStatement =
                                        connection.prepareStatement("update `idc`.`identity_t` " +
                                                "set step = step +10 where `biz_tag`=128758;");
                                preparedStatement.execute();
                            }
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        } finally {// 关闭连接
                            latch.countDown();
                            try {
                                connection.close();
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            }
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

    public static void printResultSet(ResultSet rs) {
        if (rs == null) {
            return;
        }
        try {
            ResultSetMetaData meta = rs.getMetaData();
            int cols = meta.getColumnCount();
            StringBuffer b = new StringBuffer();
            while (rs.next()) {
                for (int i = 1; i <= cols; i++) {
                    b.append(meta.getColumnName(i) + "=");
                    b.append(rs.getString(i) + "/t");
                }
                b.append("/n");
            }
            System.out.print(b.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        //dataSource.setDriverClassName(driverClassName);//如果不配置druid会根据url自动识别dbType，然后选择相应的driverClassName
        dataSource.setUrl("jdbc:mysql://localhost:3306/idc?useUnicode=true&characterEncoding=utf-8&useSSL=false&useLocalSessionState=true&serverTimezone=UTC");
        dataSource.setUsername("root");
        dataSource.setPassword("tiger");
        dataSource.setValidationQuery("SELECT 1");//用来检测连接是否有效
        dataSource.setTestOnBorrow(false);//申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
        dataSource.setTestOnReturn(false);//归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能
        //申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
        dataSource.setTestWhileIdle(true);//如果检测失败，则连接将被从池中去除
        dataSource.setTimeBetweenEvictionRunsMillis(600000);
        dataSource.setMaxActive(100);
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setInitialSize(50);
        return dataSource;
    }
}
