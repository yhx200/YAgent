package com.yagent.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.yagent.platform.mapper")
public class YAgentPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(
                YAgentPlatformApplication.class,
                args
        );
    }
}
