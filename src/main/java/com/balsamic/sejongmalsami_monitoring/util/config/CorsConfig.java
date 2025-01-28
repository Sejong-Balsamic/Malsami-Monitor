package com.balsamic.sejongmalsami_monitoring.util.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();

    // 허용할 Origin 도메인 등록
    config.addAllowedOrigin("*"); // 모든 Origin 허용 (보안상 특정 Origin만 허용 권장)
    config.addAllowedMethod("*"); // 모든 HTTP Method 허용 (GET, POST, PUT, DELETE ...)
    config.addAllowedHeader("*"); // 모든 HTTP Header 허용
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);      // Pre-flight 요청 캐싱 시간(1시간)

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    // 모든 패스에 대해 위 설정 적용
    source.registerCorsConfiguration("/**", config);
    return source;
  }
}
