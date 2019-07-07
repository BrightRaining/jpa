package com.atles.springbootjpa.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;


/**
 * user@Bright Rain .
 * 2019/7/6.
 */
@Configuration
public class DataSourcesConfig {

    @Bean(name = "primaryDataSource")
    @Qualifier("primaryDataSource")
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.primary") //为配置文件中的数据库信息配置属性
    public DataSource primaryDataSource(){
        System.out.println("primary db built");
        return DataSourceBuilder.create().build();
    }
    @Bean(name = "secondaryDataSource")
    @Qualifier("secondaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.secondary")
    public DataSource secondaryDataSource(){
        System.out.println("secondary db built");
        return DataSourceBuilder.create().build();
    }





}
