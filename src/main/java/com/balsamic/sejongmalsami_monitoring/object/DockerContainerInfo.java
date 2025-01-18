package com.balsamic.sejongmalsami_monitoring.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DockerContainerInfo {
  @JsonProperty("Id")
  private String id;

  @JsonProperty("Name")
  private String name;

  @JsonProperty("Image")
  private String image;

  @JsonProperty("State")
  private DockerState state;

  @JsonProperty("NetworkSettings")
  private DockerNetworkSettings networkSettings;

  @Getter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class DockerState {
    @JsonProperty("Status")
    private String status;  // "running", "exited" 등
  }

  @Getter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class DockerNetworkSettings {
    @JsonProperty("Ports")
    private Object ports;
    // 필요하면 Map<String, List<Map<String, String>>>
  }
}