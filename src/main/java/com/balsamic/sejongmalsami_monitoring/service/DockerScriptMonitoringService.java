package com.balsamic.sejongmalsami_monitoring.service;

import com.balsamic.sejongmalsami_monitoring.object.constants.DockerCmdOption;
import com.balsamic.sejongmalsami_monitoring.util.ssh.SshCommandExecutor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DockerScriptMonitoringService {

  private final SshCommandExecutor sshCommandExecutor;
  private final ObjectMapper objectMapper;

  @Value("${ssh.docker-info-script-path}")
  private String dockerInfoScriptPath;

  /**
   * option 맵을 String으로 변환
   */
  private String buildOptionsString(Map<DockerCmdOption, String> optionsMap) {
    if (optionsMap == null || optionsMap.isEmpty()) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<DockerCmdOption, String> entry : optionsMap.entrySet()) {
      DockerCmdOption option = entry.getKey();
      String value = entry.getValue();

      sb.append(" ")
          .append(option.getOptionName()); // 예: --format, --filter, -f

      if (value != null && !value.trim().isEmpty()) {
        // 값에 공백, 따옴표 등이 섞일 수 있으므로 적절히 이스케이프 처리
        sb.append(" \"").append(value).append("\"");
      }
    }
    return sb.toString();
  }

  /* ========== container 명령어 관련 ========== */

  /**
   * docker_info.sh container <containerName>
   * 특정 컨테이너의 docker inspect 결과 (Raw JSON)
   */
  public List<Map<String, Object>> getContainerRawInfo(String containerName) {
    return getContainerRawInfo(containerName, null);
  }

  /**
   * docker_info.sh container <containerName> [OPTIONS...]
   */
  public List<Map<String, Object>> getContainerRawInfo(String containerName,
      Map<DockerCmdOption, String> options) {
    try {
      // 예: "/path/to/docker_info.sh container my_container --format '{{.Id}}'"
      String command = dockerInfoScriptPath
          + " container " + containerName
          + (options != null ? buildOptionsString(options) : "");

      String jsonOutput = sshCommandExecutor.executeCommandWithSudoStdin(command);

      // docker_info.sh 내부에서 "jq '.[0]'" 형태로 하나만 출력하므로
      // 실제로는 List<Map<String,Object>>가 아니라 Map<String,Object> 단일이 내려올 수도 있음
      // 여기서는 사용자가 만든 스크립트에 따라 달라질 수 있으니, 일단 List로 파싱
      return objectMapper.readValue(jsonOutput, new TypeReference<>() {});
    } catch (Exception e) {
      log.error("[getContainerRawInfo] Failed to fetch container info: {}", containerName, e);
      return Collections.emptyList();
    }
  }

  /* ========== image 명령어 관련 ========== */

  /**
   * docker_info.sh image <imageName>
   * 특정 이미지의 docker inspect 결과 (Raw JSON)
   */
  public List<Map<String, Object>> getImageRawInfo(String imageName) {
    return getImageRawInfo(imageName, null);
  }

  /**
   * docker_info.sh image <imageName> [OPTIONS...]
   */
  public List<Map<String, Object>> getImageRawInfo(String imageName,
      Map<DockerCmdOption, String> options) {
    try {
      String command = dockerInfoScriptPath
          + " image " + imageName
          + (options != null ? buildOptionsString(options) : "");

      String jsonOutput = sshCommandExecutor.executeCommandWithSudoStdin(command);
      return objectMapper.readValue(jsonOutput, new TypeReference<>() {});
    } catch (Exception e) {
      log.error("[getImageRawInfo] Failed to fetch image info: {}", imageName, e);
      return Collections.emptyList();
    }
  }

  /* ========== ps 명령어 관련 (모든 컨테이너 리스트) ========== */

  /**
   * docker_info.sh ps
   * 모든 컨테이너 목록 (docker ps [OPTIONS]) - Raw JSON
   */
  public List<Map<String, Object>> listAllContainers() {
    return listAllContainers(null);
  }

  /**
   * docker_info.sh ps [OPTIONS...]
   * 모든 컨테이너 목록 (docker ps [OPTIONS]) - 옵션 지원 버전
   */
  public List<Map<String, Object>> listAllContainers(Map<DockerCmdOption, String> options) {
    try {
      // 예: "/path/to/docker_info.sh ps -a"
      String command = dockerInfoScriptPath
          + " ps"
          + (options != null ? buildOptionsString(options) : "");

      String jsonOutput = sshCommandExecutor.executeCommandWithSudoStdin(command);
      // docker ps --format '{{json .}}' | jq -s . => List<Map<String, Object>>
      return objectMapper.readValue(jsonOutput, new TypeReference<>() {});
    } catch (Exception e) {
      log.error("[listAllContainers] Failed to list containers", e);
      return Collections.emptyList();
    }
  }

  /* ========== images 명령어 관련 (모든 이미지 리스트) ========== */

  /**
   * docker_info.sh images
   * 모든 이미지 목록 (docker images [OPTIONS]) - Raw JSON
   */
  public List<Map<String, Object>> listAllImages() {
    return listAllImages(null);
  }

  /**
   * docker_info.sh images [OPTIONS...]
   * 모든 이미지 목록 (docker images [OPTIONS]) - 옵션 지원 버전
   */
  public List<Map<String, Object>> listAllImages(Map<DockerCmdOption, String> options) {
    try {
      String command = dockerInfoScriptPath
          + " images"
          + (options != null ? buildOptionsString(options) : "");

      String jsonOutput = sshCommandExecutor.executeCommandWithSudoStdin(command);
      // docker images --format '{{json .}}' | jq -s .
      return objectMapper.readValue(jsonOutput, new TypeReference<>() {});
    } catch (Exception e) {
      log.error("[listAllImages] Failed to list images", e);
      return Collections.emptyList();
    }
  }

  /* ========== logs 명령어 관련 (특정 컨테이너 로그) ========== */

  /**
   * docker_info.sh logs <containerName>
   * 특정 컨테이너 로그 - Raw JSON array of strings
   */
  public List<String> getContainerLogs(String containerName) {
    return getContainerLogs(containerName, null);
  }

  /**
   * docker_info.sh logs <containerName> [OPTIONS...]
   * 특정 컨테이너 로그
   */
  public List<String> getContainerLogs(String containerName,
      Map<DockerCmdOption, String> options) {
    try {
      String command = dockerInfoScriptPath
          + " logs " + containerName
          + (options != null ? buildOptionsString(options) : "");

      String jsonOutput = sshCommandExecutor.executeCommandWithSudoStdin(command);

      // logs는 [ "line1", "line2", ... ] 형태로 내려옴
      return objectMapper.readValue(jsonOutput, new TypeReference<>() {});
    } catch (Exception e) {
      log.error("[getContainerLogs] Failed to fetch logs for container: {}", containerName, e);
      return Collections.emptyList();
    }
  }

  /* ========== system 명령어 관련 (docker info) ========== */

  /**
   * docker_info.sh system
   * 시스템 전체 정보 (docker info)
   */
  public Map<String, Object> getSystemInfo() {
    return getSystemInfo(null);
  }

  /**
   * docker_info.sh system [OPTIONS...]
   */
  public Map<String, Object> getSystemInfo(Map<DockerCmdOption, String> options) {
    try {
      String command = dockerInfoScriptPath
          + " system"
          + (options != null ? buildOptionsString(options) : "");

      String jsonOutput = sshCommandExecutor.executeCommandWithSudoStdin(command);
      return objectMapper.readValue(jsonOutput, new TypeReference<>() {});
    } catch (Exception e) {
      log.error("[getSystemInfo] Failed to fetch system info", e);
      return Collections.emptyMap();
    }
  }

  /* ========== 추가적인 메서드 예시 ========== */

  /**
   * docker_info.sh ps [OPTIONS...]
   * 특정 필터를 적용하여 컨테이너 목록을 가져옴
   *
   * @param filters 필터 조건 (예: status=running)
   * @return 필터링된 컨테이너 목록
   */
  public List<Map<String, Object>> listContainersWithFilters(Map<String, String> filters) {
    try {
      StringBuilder filterBuilder = new StringBuilder();
      if (filters != null && !filters.isEmpty()) {
        for (Map.Entry<String, String> entry : filters.entrySet()) {
          filterBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append(",");
        }
        // 마지막 콤마 제거
        filterBuilder.setLength(filterBuilder.length() - 1);
      }

      // 옵션 맵 구성
      Map<DockerCmdOption, String> options = Collections.singletonMap(
          DockerCmdOption.FILTER,
          filterBuilder.toString()
      );

      String command = dockerInfoScriptPath
          + " ps"
          + buildOptionsString(options);

      String jsonOutput = sshCommandExecutor.executeCommandWithSudoStdin(command);
      return objectMapper.readValue(jsonOutput, new TypeReference<>() {});
    } catch (Exception e) {
      log.error("[listContainersWithFilters] Failed to list containers with filters", e);
      return Collections.emptyList();
    }
  }

  /**
   * docker_info.sh images [OPTIONS...]
   * 특정 레지스트리에서 이미지를 필터링하여 가져옴
   *
   * @param repository 이미지 레지스트리 이름
   * @return 필터링된 이미지 목록
   */
  public List<Map<String, Object>> listImagesWithRepository(String repository) {
    try {
      Map<DockerCmdOption, String> options = Collections.singletonMap(
          DockerCmdOption.FILTER,
          "reference=" + repository
      );

      String command = dockerInfoScriptPath
          + " images"
          + buildOptionsString(options);

      String jsonOutput = sshCommandExecutor.executeCommandWithSudoStdin(command);
      return objectMapper.readValue(jsonOutput, new TypeReference<>() {});
    } catch (Exception e) {
      log.error("[listImagesWithRepository] Failed to list images with repository: {}", repository, e);
      return Collections.emptyList();
    }
  }

  /**
   * docker_info.sh logs <containerName> --tail <number>
   * 특정 컨테이너의 최근 로그 몇 개를 가져옴
   *
   * @param containerName 컨테이너 이름
   * @param tailCount     가져올 로그의 수
   * @return 최근 로그 목록
   */
  public List<String> getContainerLogsWithTail(String containerName, int tailCount) {
    try {
      Map<DockerCmdOption, String> options = Collections.singletonMap(
          DockerCmdOption.TAIL,
          String.valueOf(tailCount)
      );

      String command = dockerInfoScriptPath
          + " logs " + containerName
          + buildOptionsString(options);

      String jsonOutput = sshCommandExecutor.executeCommandWithSudoStdin(command);
      return objectMapper.readValue(jsonOutput, new TypeReference<>() {});
    } catch (Exception e) {
      log.error("[getContainerLogsWithTail] Failed to fetch logs for container: {}", containerName, e);
      return Collections.emptyList();
    }
  }

  /**
   * docker_info.sh ps --format "{{.Names}}: {{.Status}}"
   * 모든 컨테이너의 이름과 상태를 특정 형식으로 출력
   *
   * @param formatFormat 출력 형식
   * @return 형식화된 컨테이너 정보 목록
   */
  public List<String> listContainersWithFormat(String formatFormat) {
    try {
      Map<DockerCmdOption, String> options = Collections.singletonMap(
          DockerCmdOption.FORMAT,
          formatFormat
      );

      String command = dockerInfoScriptPath
          + " ps"
          + buildOptionsString(options);

      String jsonOutput = sshCommandExecutor.executeCommandWithSudoStdin(command);
      return objectMapper.readValue(jsonOutput, new TypeReference<>() {});
    } catch (Exception e) {
      log.error("[listContainersWithFormat] Failed to list containers with format: {}", formatFormat, e);
      return Collections.emptyList();
    }
  }

  // 추가적인 메서드는 필요에 따라 유사한 패턴으로 계속 추가할 수 있습니다.
}
