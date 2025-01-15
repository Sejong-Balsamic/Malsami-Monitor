package com.balsamic.sejongmalsami_monitoring.service;

import com.balsamic.sejongmalsami_monitoring.object.ContainerStats;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Statistics;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DockerMonitoringService {
  private final DockerClient dockerClient;

  public List<ContainerStats> getContainersStats() {
    List<Container> containers = dockerClient.listContainersCmd()
        .withShowAll(true)
        .exec();

    return containers.stream()
        .map(container -> {
          StatsResultCallback statsCallback = new StatsResultCallback();
          try {
            dockerClient.statsCmd(container.getId())
                .exec(statsCallback);
            Statistics stats = statsCallback.getStatistics();
            // 1초 후에도 응답이 없으면 타임아웃
            statsCallback.awaitCompletion(1, TimeUnit.SECONDS);

            return ContainerStats.builder()
                .containerId(container.getId())
                .name(container.getNames()[0])
                .cpuUsage(calculateCpuUsage(stats))
                .memoryUsage(calculateMemoryUsage(stats))
                .build();
          } catch (InterruptedException e) {
            throw new RuntimeException("Failed to get container stats", e);
          }
        })
        .collect(Collectors.toList());
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