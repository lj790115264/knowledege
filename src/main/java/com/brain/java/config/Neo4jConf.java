package com.brain.java.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: java
 * @description:
 * @author: lanj
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
        Driver driver = GraphDatabase.driver(neo4jUrl, AuthTokens.basic(username, password));
        return driver;
    }

}
