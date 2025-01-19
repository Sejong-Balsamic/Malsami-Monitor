package com.balsamic.sejongmalsami_monitoring.object.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Docker 이미지 정보를 관리하는 Enum
 */
@Getter
@AllArgsConstructor
public enum DockerImage {

  SEJONG_MALSAMI_FRONT_TEST("cassiiopeia/sejong-malsami-front-container:test", "Frontend Container - Test"),
  SEJONG_MALSAMI_BACK_MAIN("cassiiopeia/sejong-malsami-back-container:main", "Backend Container - Main"),
  SEJONG_MALSAMI_BACK_TEST("cassiiopeia/sejong-malsami-back-container:test", "Backend Container - Test"),
  SELENIUM_CHROME("selenium/standalone-chrome", "Selenium Chrome"),
  POSTGRES("postgres-postgres", "Postgres Database"),
  SEJONG_MALSAMI_FRONT_MAIN("cassiiopeia/sejong-malsami-front-container:main", "Frontend Container - Main"),
  REDIS("redis:latest", "Redis Cache"),
  MONGO("mongo:4.4", "MongoDB");

  private final String imageName;
  private final String description;
}
