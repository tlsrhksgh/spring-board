package com.single.springboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;

@Configuration
public class LoadScriptConfig {

    @Bean
    public RedisScript<String> temporaryUserIncrScript() {
        return RedisScript.of(new ClassPathResource("scripts/temporaryUserIncr.lua"), String.class);
    }
}

