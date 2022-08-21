package com.wjx.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.TemplateEngine;


/**
 * @author wjx
 * @description
 */
@SpringBootApplication
@MapperScan(value = "com.wjx.community.dao")
public class CommunityApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class,args);
    }

    @Bean
    public TemplateEngine getEngine(){
        return new TemplateEngine();
    }


}
