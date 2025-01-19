package com.balsamic.sejongmalsami_monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SejongMalsamiMonitoringApplication {

  public static void main(String[] args) {
    SpringApplication.run(SejongMalsamiMonitoringApplication.class, args);
  }

}
