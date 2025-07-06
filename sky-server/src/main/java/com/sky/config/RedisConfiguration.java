package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@Slf4j
public class RedisConfiguration {

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("开始创建redis模版对象");
        RedisTemplate redisTemplate = new RedisTemplate();
        //设置redis的连接工厂对象redisConnectionFactory内置封装了对redis数据库的操作方法
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //设置redis key的序列化，使最后显示结果为字符串类型
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }
}
