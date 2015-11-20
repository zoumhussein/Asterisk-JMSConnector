package com.africasys.oss.asterisk.jmsconnector.config;

import org.asteriskjava.manager.PingThread;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages="com.africasys.oss")
public class AsteriskJmsConnApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(AsteriskJmsConnApplication.class, args);
//        
//        while(true){
//        	Thread.sleep(100000);
//        }
    }
    
    @Bean
    public PingThread amiPingThread(){
    	return new PingThread();
    }
}
