package com.balsamic.sejongmalsami_monitoring.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@Builder
public class DockerPsContainerDto {
  @JsonProperty("ID")
  private String id;

  @JsonProperty("Image")
  private String image;

  @JsonProperty("Command")
  private String command;

  @JsonProperty("CreatedAt")
  private String createdAt;

  @JsonProperty("Status")
  private String status;

  @JsonProperty("Ports")
  private String ports;

  @JsonProperty("Names")
  private String names;

  @JsonProperty("Networks")
  private String networks;

  @JsonProperty("RunningFor")
  private String runningFor;

  @JsonProperty("Size")
  private String size;

  @JsonProperty("State")
  private String state;

  @JsonProperty("Labels")
  private String labels;

  @JsonProperty("LocalVolumes")
  private String localVolumes;

  @JsonProperty("Mounts")
  private String mounts;
}
