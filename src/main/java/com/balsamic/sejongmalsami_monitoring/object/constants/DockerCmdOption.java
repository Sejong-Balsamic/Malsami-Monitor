package com.balsamic.sejongmalsami_monitoring.object.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.checkerframework.checker.units.qual.A;

/**
 * 도커 명령어 옵션
 */
@Getter
@AllArgsConstructor
public enum DockerCmdOption {
  ALL("-a"),
  QUIET("-q"),
  NO_TRUNC("--no-trunc"),
  FORMAT("--format"),   // "--format {{.Something}}"
  FILTER("--filter"),   // "--filter key=value"
  FOLLOW("-f"),
  TAIL("--tail"),
  SIZE("--size");

  private final String optionName;
}
