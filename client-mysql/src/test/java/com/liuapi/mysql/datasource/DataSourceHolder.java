package com.liuapi.mysql.datasource;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;

public class DataSourceHolder {
    public static DataSource get() {
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
