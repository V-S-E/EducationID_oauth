package com.vsa5edu.educationID.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class JedisConnect {
    @Value("${project.redis.host}")
    private String host;
    @Value("${project.redis.port}")
    private Integer port;

    public void set(byte[] key, byte[] value){
        try (Jedis jedis = new JedisPool(host, port).getResource()){
            jedis.set(key, value);
        }
    }

    public byte[] get(byte[] key){
        try (Jedis jedis = new JedisPool(host, port).getResource()){
            return jedis.get(key);
        }
    }

    public void del(byte[] key){
        try (Jedis jedis = new JedisPool(host, port).getResource()){
            jedis.del(key);
        }
    }
}
