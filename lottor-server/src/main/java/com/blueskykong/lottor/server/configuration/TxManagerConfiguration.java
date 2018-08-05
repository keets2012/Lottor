package com.blueskykong.lottor.server.configuration;

import com.blueskykong.lottor.common.enums.SerializeProtocolEnum;
import com.blueskykong.lottor.common.holder.ServiceBootstrap;
import com.blueskykong.lottor.common.serializer.KryoSerializer;
import com.blueskykong.lottor.common.serializer.ObjectSerializer;
import com.blueskykong.lottor.server.config.NettyConfig;
import com.blueskykong.lottor.server.discovery.DiscoveryService;
import com.blueskykong.lottor.server.netty.handler.NettyServerMessageHandler;
import com.blueskykong.lottor.server.service.BaseItemService;
import com.blueskykong.lottor.server.service.TxManagerService;
import com.blueskykong.lottor.server.service.impl.BaseItemServiceImpl;
import com.blueskykong.lottor.server.task.TxSyncTask;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

@Configuration
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class TxManagerConfiguration {
    @Bean
    public DiscoveryService discoveryService(DiscoveryClient discoveryClient) {
        return new DiscoveryService(discoveryClient);
    }

    @Bean
    public TxSyncTask txSyncTask(TxManagerService txManagerService, NettyServerMessageHandler serverMessageHandler,
                                 NettyConfig nettyConfig, BaseItemService baseItemService) {
        return new TxSyncTask(txManagerService, serverMessageHandler, nettyConfig, baseItemService);
    }

    @Bean
    public BaseItemService baseItemService(MongoTemplate mongoTemplate) {
        return new BaseItemServiceImpl(mongoTemplate);
    }

    @Configuration
    static class NettyConfiguration {

        @Bean
        @ConfigurationProperties("lottor.manager.netty")
        public NettyConfig getNettyConfig() {
            return new NettyConfig();
        }

        @Bean
        public ObjectSerializer objectSerializer(NettyConfig nettyConfig) {
            final SerializeProtocolEnum serializeProtocolEnum =
                    SerializeProtocolEnum.acquireSerializeProtocol(nettyConfig.getSerialize());

            final ServiceLoader<ObjectSerializer> objectSerializers = ServiceBootstrap.loadAll(ObjectSerializer.class);

            final Optional<ObjectSerializer> serializer = StreamSupport.stream(objectSerializers.spliterator(), false)
                    .filter(objectSerializer ->
                            Objects.equals(objectSerializer.getScheme(), serializeProtocolEnum.getSerializeProtocol())).findFirst();
            return serializer.orElse(new KryoSerializer());
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
        public Mongo mongo() {
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

    @Configuration
    protected static class CORSConfiguration {
        @Bean
        public WebMvcConfigurer corsConfigurer() {
            return new WebMvcConfigurerAdapter() {
                @Override
                public void addCorsMappings(CorsRegistry registry) {
                    registry.addMapping("/**")
                            .allowedHeaders("*")
                            .allowedMethods("*")
                            .allowedOrigins("*");
                }
            };
        }
    }

    @Configuration
    static class RestConfiguration {
        @Bean
        public RestTemplate getRestTemplate() {
            return new RestTemplate();
        }
    }

    @Configuration
    static class swagger {
        @Bean
        public Docket createRestApi() {
            return new Docket(DocumentationType.SWAGGER_2)
                    .apiInfo(apiInfo())
                    .select()
                    .apis(RequestHandlerSelectors.basePackage("com.blueskykong.lottor.server.controller"))
                    .paths(PathSelectors.any())
                    .build();
        }

        private ApiInfo apiInfo() {
            return new ApiInfoBuilder()
                    .title("Lottor Server")
                    .description("Lottor UI接口描述")
                    .termsOfServiceUrl("http://blueskykong.com")
                    .version("1.0")
                    .build();
        }

    }
}
