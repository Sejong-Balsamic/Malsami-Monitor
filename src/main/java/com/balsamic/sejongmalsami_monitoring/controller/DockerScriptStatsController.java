package com.balsamic.sejongmalsami_monitoring.controller;

import com.balsamic.sejongmalsami_monitoring.object.ContainerStats;
import com.balsamic.sejongmalsami_monitoring.service.DockerAPIMonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DockerScriptStatsController {

  private final DockerAPIMonitoringService monitoringService;
  private final SimpMessagingTemplate messagingTemplate;

  /**
   * (A) REST API로 컨테이너 상태 확인
   */
  @GetMapping("/api/docker/containers")
  public List<ContainerStats> getAllContainers() {
    List<ContainerStats> stats = monitoringService.getContainersStats();
    return stats;
  }

  /**
   * (B) 5초마다 WebSocket 발송
   */
  @Scheduled(fixedRate = 5000)
  public void broadcastContainerStats() {
    List<ContainerStats> stats = monitoringService.getContainersStats();
    messagingTemplate.convertAndSend("/topic/stats", stats);
  }
}
