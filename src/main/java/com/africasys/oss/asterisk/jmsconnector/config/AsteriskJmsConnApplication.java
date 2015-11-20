package com.africasys.oss.asterisk.jmsconnector.config;

import org.asteriskjava.manager.PingThread;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableAutoConfiguration
public class AsteriskJmsConnApplication {

    public static void main(String[] args) {
        SpringApplication.run(AsteriskJmsConnApplication.class, args);
    }
    
    @Bean
    public PingThread amiPingThread(){
    	return new PingThread();
    }
}
