package com.balsamic.sejongmalsami_monitoring.object;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContainerStats {
  private String containerId;
  private String name;
  private Double cpuUsage;
  private Long memoryUsage;
}