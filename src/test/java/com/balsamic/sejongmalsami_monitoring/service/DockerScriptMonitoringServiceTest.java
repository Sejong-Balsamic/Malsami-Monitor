package com.balsamic.sejongmalsami_monitoring.service;

import static com.balsamic.sejongmalsami_monitoring.util.log.LogUtil.*;

import com.balsamic.sejongmalsami_monitoring.object.DockerResponse;
import com.balsamic.sejongmalsami_monitoring.object.constants.DockerCmdOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class DockerScriptMonitoringServiceTest {
  @Autowired
  DockerScriptMonitoringService dockerScriptMonitoringService;

  @Test
  public void mainTest() {
//    lineLog("getContainerRawInfo_테스트");
//    timeLog(this::getContainerRawInfo_테스트);

//    lineLog("getContainerRawInfo_withOptions_테스트");
//    timeLog(this::getContainerRawInfo_withOptions_테스트);
//
//    lineLog("getImageRawInfo_테스트");
//    timeLog(this::getImageRawInfo_테스트);
//
//    lineLog("getImageRawInfo_withOptions_테스트");
//    timeLog(this::getImageRawInfo_withOptions_테스트);
//
    lineLog("listAllContainers_테스트");
    timeLog(this::listAllActiveContainers_테스트);

    lineLog("listAllContainers_withOptions_테스트");
    timeLog(this::listAllActiveContainers_withOptions_테스트);
//
//    lineLog("listAllImages_테스트");
//    timeLog(this::listAllImages_테스트);
//
//    lineLog("listAllImages_withOptions_테스트");
//    timeLog(this::listAllImages_withOptions_테스트);
//
//    lineLog("getContainerLogs_테스트");
//    timeLog(this::getContainerLogs_테스트);
//
//    lineLog("getContainerLogs_withOptions_테스트");
//    timeLog(this::getContainerLogs_withOptions_테스트);
//
//    lineLog("getSystemInfo_테스트");
//    timeLog(this::getSystemInfo_테스트);
//
//    lineLog("getSystemInfo_withOptions_테스트");
//    timeLog(this::getSystemInfo_withOptions_테스트);
//
//    // 추가된 메서드 테스트
//    lineLog("listContainersWithFilters_테스트");
//    timeLog(this::listContainersWithFilters_테스트);
//
//    lineLog("listImagesWithRepository_테스트");
//    timeLog(this::listImagesWithRepository_테스트);
//
//    lineLog("getContainerLogsWithTail_테스트");
//    timeLog(this::getContainerLogsWithTail_테스트);
//
//    lineLog("listContainersWithFormat_테스트");
//    timeLog(this::listContainersWithFormat_테스트);
  }

  /* ========== 기존 테스트 메서드 ========== */

  public void getContainerRawInfo_테스트() {
    List<Map<String, Object>> containerRawInfo
        = dockerScriptMonitoringService.getContainerRawInfo("sejong-malsami-back");
    superLog(containerRawInfo);
  }

  public void getImageRawInfo_테스트() {
    List<Map<String, Object>> imageRawInfo
        = dockerScriptMonitoringService.getImageRawInfo("cassiiopeia/sejong-malsami-back-container:main");
    superLog(imageRawInfo);
  }

  public void listAllActiveContainers_테스트() {
    DockerResponse dockerResponse = dockerScriptMonitoringService.listAllContainers();
    superLog(dockerResponse);
  }

  public void listAllImages_테스트(){
    List<Map<String, Object>> allImages = dockerScriptMonitoringService.listAllImages();
    superLog(allImages);
  }

  public void getContainerLogs_테스트(){
    List<String> containerLogs = dockerScriptMonitoringService.getContainerLogs("sejong-malsami-back");
    superLog(containerLogs);
  }

  public void getSystemInfo_테스트() {
    Map<String, Object> systemInfo = dockerScriptMonitoringService.getSystemInfo();
    superLog(systemInfo);
  }

  /* ========== 기존 메서드에 옵션을 적용한 테스트 메서드 ========== */

  public void getContainerRawInfo_withOptions_테스트() {
    Map<DockerCmdOption, String> options = new HashMap<>();
    options.put(DockerCmdOption.FORMAT, "{{.Id}}");
    options.put(DockerCmdOption.NO_TRUNC, "");

    List<Map<String, Object>> containerRawInfo
        = dockerScriptMonitoringService.getContainerRawInfo("sejong-malsami-back", options);
    superLog(containerRawInfo);
  }

  public void getImageRawInfo_withOptions_테스트() {
    Map<DockerCmdOption, String> options = new HashMap<>();
    options.put(DockerCmdOption.FORMAT, "{{.Id}}");
    options.put(DockerCmdOption.NO_TRUNC, "");

    List<Map<String, Object>> imageRawInfo
        = dockerScriptMonitoringService.getImageRawInfo("cassiiopeia/sejong-malsami-back-container:main", options);
    superLog(imageRawInfo);
  }

  public void listAllActiveContainers_withOptions_테스트() {
    Map<DockerCmdOption, String> options = new HashMap<>();
    options.put(DockerCmdOption.ALL, "");
    options.put(DockerCmdOption.FILTER, "status=running");
    options.put(DockerCmdOption.FORMAT, "{{json .}}");

    DockerResponse dockerResponse = dockerScriptMonitoringService.listAllContainers(options);
    superLog(dockerResponse);
  }

  public void listAllImages_withOptions_테스트() {
    Map<DockerCmdOption, String> options = new HashMap<>();
    options.put(DockerCmdOption.FILTER, "reference=cassiiopeia/*");
    options.put(DockerCmdOption.FORMAT, "{{json .}}");

    List<Map<String, Object>> allImages = dockerScriptMonitoringService.listAllImages(options);
    superLog(allImages);
  }

  public void getContainerLogs_withOptions_테스트(){
    Map<DockerCmdOption, String> options = new HashMap<>();
    options.put(DockerCmdOption.TAIL, "50");

    List<String> containerLogs = dockerScriptMonitoringService.getContainerLogs("sejong-malsami-back", options);
    superLog(containerLogs);
  }

  public void getSystemInfo_withOptions_테스트() {
    Map<DockerCmdOption, String> options = new HashMap<>();
    options.put(DockerCmdOption.SIZE, "");

    Map<String, Object> systemInfo = dockerScriptMonitoringService.getSystemInfo(options);
    superLog(systemInfo);
  }

  /* ========== 추가된 테스트 메서드 ========== */

  public void listContainersWithFilters_테스트() {
    Map<String, String> filters = new HashMap<>();
    filters.put("status", "running");
    filters.put("ancestor", "cassiiopeia/sejong-malsami-back-container:main");

    List<Map<String, Object>> filteredContainers = dockerScriptMonitoringService.listContainersWithFilters(filters);
    superLog(filteredContainers);
  }

  public void listImagesWithRepository_테스트() {
    String repository = "cassiiopeia/sejong-malsami-front-container";

    List<Map<String, Object>> images = dockerScriptMonitoringService.listImagesWithRepository(repository);
    superLog(images);
  }

  public void getContainerLogsWithTail_테스트() {
    int tailCount = 100;
    List<String> containerLogs = dockerScriptMonitoringService.getContainerLogsWithTail("sejong-malsami-back", tailCount);
    superLog(containerLogs);
  }

  public void listContainersWithFormat_테스트() {
    String format = "{{.Names}}: {{.Status}}";

    List<String> formattedContainers = dockerScriptMonitoringService.listContainersWithFormat(format);
    superLog(formattedContainers);
  }
}
