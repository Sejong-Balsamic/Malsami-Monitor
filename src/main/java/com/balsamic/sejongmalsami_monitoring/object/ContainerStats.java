package com.balsamic.sejongmalsami_monitoring.object;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContainerStats {
  private String containerId;
  private String name;
  private Double cpuUsage;
  private Long memoryUsage;
  private String status;
  private String network;
  private List<String> ports;
  private String image;
}