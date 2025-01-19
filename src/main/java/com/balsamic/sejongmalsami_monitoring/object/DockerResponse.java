package com.balsamic.sejongmalsami_monitoring.object;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Setter
@Getter
public class DockerResponse {
  private List<DockerPsContainerDto> dockerPsContainerDtos;
}
