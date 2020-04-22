package com.brain.java.config;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: java
 * @description:
 * @author: liyx
 * @create: 2020-04-22 16:25
 */
@Configuration
public class Neo4jConf {

    @Value("${neo4j.url}")
    private String neo4jUrl;

    @Value("${neo4j.username}")
    private String username;

    @Value("${neo4j.password}")
    private String password;

    @Bean
    public Driver driver() {
        Config noSSL = Config.build().withEncryptionLevel(Config.EncryptionLevel.NONE).toConfig();
        Driver driver = GraphDatabase.driver(neo4jUrl, AuthTokens.basic(username, password), noSSL);
        return driver;
    }

}
