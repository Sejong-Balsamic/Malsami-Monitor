package com.balsamic.sejongmalsami_monitoring.controller;

import com.balsamic.sejongmalsami_monitoring.object.ContainerStats;
import com.balsamic.sejongmalsami_monitoring.service.DockerMonitoringService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class DockerStatsController {
  private final DockerMonitoringService monitoringService;
  private final SimpMessagingTemplate messagingTemplate;

  @Scheduled(fixedRate = 5000)
  public void sendStats() {
    List<ContainerStats> stats = monitoringService.getContainersStats();
    messagingTemplate.convertAndSend("/topic/stats", stats);
  }
}