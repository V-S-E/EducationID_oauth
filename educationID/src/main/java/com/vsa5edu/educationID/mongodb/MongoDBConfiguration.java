package com.vsa5edu.educationID.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoDBConfiguration extends AbstractMongoClientConfiguration {
    @Value("${project.mongodb.url}")
    String mongodb_url;

    @Value("${project.mongodb.dbname}")
    String db_name;

    @Bean
    public MongoClient createConfig(){
        ConnectionString cs = new ConnectionString(mongodb_url+db_name);
        MongoClientSettings mcs = MongoClientSettings.builder()
                .applyConnectionString(cs)
                .build();
        return MongoClients.create(mcs);
    }

    @Override
    protected String getDatabaseName() {
        return db_name;
    }
}
