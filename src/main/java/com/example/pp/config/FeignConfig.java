package com.example.pp.config;

import com.example.pp.exeption.CustomErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public CustomErrorDecoder errorDecoder(){
        return new CustomErrorDecoder();
    }
}
