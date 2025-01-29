package com.balsamic.sejongmalsami_monitoring.util.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();

    // 허용할 Origin 목록
    config.setAllowedOrigins(List.of(
        "http://localhost:8089",
        "https://monitor.sejong-malsami.co.kr",
        "http://suh-project.synology.me:8089"
    ));

    // 모든 HTTP Method 허용 (GET, POST, PUT, DELETE 등)
    config.setAllowedMethods(List.of("*"));

    // 모든 HTTP Header 허용
    config.setAllowedHeaders(List.of("*"));

    // 자격 증명 허용 (쿠키, Authorization 헤더 포함)
    config.setAllowCredentials(true);

    // Pre-flight 요청 캐싱 시간(1시간)
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return source;
  }
}
