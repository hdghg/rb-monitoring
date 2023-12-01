package com.guthub.hdghg.rbmonitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RbMonitoringApplication {

	public static void main(String[] args) {
		SpringApplication.run(RbMonitoringApplication.class, args);
	}

}
