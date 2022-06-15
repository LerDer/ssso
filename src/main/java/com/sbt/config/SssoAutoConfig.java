package com.sbt.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = "com.sbt")
//将配置文件中的变量加载到Properties的配置类对象当中，并注入到容器当中
@EnableConfigurationProperties(SssoProperties.class)
public class SssoAutoConfig {

    @Bean
    @ConditionalOnBean(SssoConfig.class)
    public String getSssoConfig(SssoProperties properties) {
        properties.init();
        return "";
    }

}
