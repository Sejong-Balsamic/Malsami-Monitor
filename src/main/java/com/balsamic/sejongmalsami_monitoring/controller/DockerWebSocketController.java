package com.balsamic.sejongmalsami_monitoring.controller;

import com.balsamic.sejongmalsami_monitoring.object.ContainerStats;
import com.balsamic.sejongmalsami_monitoring.object.DockerResponse;
import com.balsamic.sejongmalsami_monitoring.service.DockerAPIMonitoringService;
import com.balsamic.sejongmalsami_monitoring.service.DockerScriptMonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class DockerWebSocketController {

  private final DockerScriptMonitoringService dockerScriptMonitoringService;
  private final SimpMessagingTemplate messagingTemplate;

  /**
   * 5초마다 컨테이너 상태 정보를 WebSocket으로 전송
   */
  @Scheduled(fixedRate = 15000) // 15초
  public void sendContainerStats() {
    try {
      log.info("Docker 컨테이너 정보 조회 시작");
      DockerResponse dockerResponse = dockerScriptMonitoringService.listAllContainers();

      if (dockerResponse == null) {
        log.warn("Docker 응답이 null입니다");
        return;
      }

      if (dockerResponse.getDockerPsContainerDtos() == null) {
        log.warn("Docker 컨테이너 목록이 null입니다");
        return;
      }

      log.info("전송할 컨테이너 개수: {}", dockerResponse.getDockerPsContainerDtos().size());
      log.info("WebSocket으로 데이터 전송 시작: /topic/stats");
      messagingTemplate.convertAndSend("/topic/stats", dockerResponse);
      log.info("WebSocket 데이터 전송 완료");

    } catch (Exception e) {
      log.error("Docker 컨테이너 정보 전송 중 에러 발생", e);
    }
  }
}
