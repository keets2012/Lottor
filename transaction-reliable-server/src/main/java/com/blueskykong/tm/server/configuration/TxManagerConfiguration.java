package com.blueskykong.tm.server.configuration;

import com.blueskykong.tm.server.config.NettyConfig;
import com.blueskykong.tm.server.discovery.DiscoveryService;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Arrays;
import java.util.Map;

@Configuration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class TxManagerConfiguration {
    @Bean
    public DiscoveryService discoveryService(DiscoveryClient discoveryClient) {
        return new DiscoveryService(discoveryClient);
    }


    @Configuration
    static class NettyConfiguration {

        @Bean
        @ConfigurationProperties("tx.manager.netty")
        public NettyConfig getNettyConfig() {
            return new NettyConfig();
        }

    }

    @Configuration
    protected static class SpringMongoConfig extends AbstractMongoConfiguration {

        @Value("${spring.data.mongodb.host}")
        private String MONGODB_HOST;

        @Value("${spring.data.mongodb.port}")
        private int MONGODB_PORT;

        @Value("${spring.data.mongodb.database}")
        private String MONGODB_DATABASE;

        @Override
        protected String getDatabaseName() {
            return MONGODB_DATABASE;
        }

        @Override
        public Mongo mongo() throws Exception {
            return new MongoClient(MONGODB_HOST, MONGODB_PORT);
        }

        @Bean
        @Override
        public MappingMongoConverter mappingMongoConverter() throws Exception {
            MappingMongoConverter mmc = super.mappingMongoConverter();
            mmc.setTypeMapper(new DefaultMongoTypeMapper(null));
            return mmc;
        }

    }
}
