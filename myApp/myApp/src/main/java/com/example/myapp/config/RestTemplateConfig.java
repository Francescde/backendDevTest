package com.example.myapp.config;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

@Configuration
public class RestTemplateConfig {

    @Value("${rest.template.connection-timeout}")
    private int connectionTimeout;

    @Value("${rest.template.read-timeout}")
    private int readTimeout;
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder)
    {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(connectionTimeout))
           .setReadTimeout(Duration.ofSeconds(readTimeout))
           .build();
    }

}
