package com.balsamic.sejongmalsami_monitoring.util.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DockerConfig {
  @Bean
  public DockerClient dockerClient() {
    DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
        .withDockerHost("unix:///var/run/docker.sock")
        .withDockerTlsVerify(false)
        .withDockerConfig("/root/.docker")  // Docker config 위치 지정
        .withApiVersion("1.41")  // Docker API 버전 명시
        .build();

    return DockerClientBuilder.getInstance(config)
        .withDockerHttpClient(new ApacheDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .sslConfig(config.getSSLConfig())
            .maxConnections(100)
            .connectionTimeout(Duration.ofSeconds(30))
            .responseTimeout(Duration.ofSeconds(45))
            .build())
        .build();
  }
}