package com.balsamic.sejongmalsami_monitoring.service;

import com.balsamic.sejongmalsami_monitoring.object.ContainerStats;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DockerMonitoringService {

  private final DockerClient dockerClient;
  private static final String TARGET_NETWORK = "sejong-malsami-network";

  public List<ContainerStats> getContainersStats() {
    List<Container> containers = dockerClient.listContainersCmd()
        .withShowAll(true)
        .withNetworkFilter(Collections.singleton(TARGET_NETWORK))  // 특정 네트워크의 컨테이너만 필터링
        .exec();

    return containers.stream()
        .map(this::getContainerStats)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private ContainerStats getContainerStats(Container container) {
    StatsResultCallback statsCallback = new StatsResultCallback();
    try {
      dockerClient.statsCmd(container.getId()).exec(statsCallback);
      Statistics stats = statsCallback.getStatistics();
      statsCallback.awaitCompletion(1, TimeUnit.SECONDS);

      return ContainerStats.builder()
          .containerId(container.getId())
          .name(container.getNames()[0].replaceFirst("^/", ""))  // 컨테이너 이름에서 앞의 '/' 제거
          .cpuUsage(calculateCpuUsage(stats))
          .memoryUsage(calculateMemoryUsage(stats))
          .status(container.getStatus())
          .build();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return null;
    }
  }

  private static class StatsResultCallback extends ResultCallback.Adapter<Statistics> {
    private Statistics statistics;

    @Override
    public void onNext(Statistics stats) {
      this.statistics = stats;
      try {
        close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    public Statistics getStatistics() {
      return statistics;
    }
  }

  private Double calculateCpuUsage(Statistics stats) {
    if (stats == null || stats.getCpuStats() == null) {
      return 0.0;
    }

    long cpuDelta = stats.getCpuStats().getCpuUsage().getTotalUsage() -
        stats.getPreCpuStats().getCpuUsage().getTotalUsage();
    long systemDelta = stats.getCpuStats().getSystemCpuUsage() -
        stats.getPreCpuStats().getSystemCpuUsage();

    if (systemDelta > 0 && cpuDelta > 0) {
      return (double) cpuDelta / systemDelta * 100;
    }
    return 0.0;
  }

  private Long calculateMemoryUsage(Statistics stats) {
    if (stats == null || stats.getMemoryStats() == null) {
      return 0L;
    }
    return stats.getMemoryStats().getUsage();
  }
}