package com.airticket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication(scanBasePackages = "com.airticket")
@EnableJpaRepositories
@EnableDiscoveryClient
public class AirticketApplication {
 
	public static void main(String[] args) {
		SpringApplication.run(AirticketApplication.class, args);
	}

}