package com.balsamic.sejongmalsami_monitoring.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogMonitoringService {
  private static final String LOG_PATH = "/volume1/projects/sejong-malsami/logs/";

  public String getTailLog(String containerName, int lines) {
    Path logFile = Paths.get(LOG_PATH, containerName + ".log");
    try (Stream<String> stream = Files.lines(logFile)) {
      return stream.skip(Math.max(0, stream.count() - lines))
          .collect(Collectors.joining("\n"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}