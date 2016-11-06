package com.beolnix.marvin.im.plugin.statistics.configuration;

import com.beolnix.StatisticsClientConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.beolnix.marvin.statistics.api.Constants;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by beolnix on 07/02/16.
 */
@Configuration
@ComponentScan("com.beolnix.marvin")
@EnableFeignClients("com.beolnix.marvin")
@EnableAutoConfiguration
@SpringBootApplication
@RibbonClient(value = Constants.FEIGN_SERVICE, configuration = StatisticsClientConfiguration.class)
public class StatisticsConfiguration {

    @Bean
    public ObjectMapper jsonMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JSR310Module());
        //Fully qualified path shows I am using the latest version of fasterxml enum constants
        objectMapper.configure(com.fasterxml.jackson.databind.SerializationFeature.
                WRITE_DATES_AS_TIMESTAMPS , false);
        objectMapper.getSerializationConfig().with(new ISO8601DateFormat());
        return objectMapper;
    }

}
