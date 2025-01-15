package com.balsamic.sejongmalsami_monitoring.controller;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Frame;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/containers")
@RequiredArgsConstructor
public class ContainerController {
  private final DockerClient dockerClient;

  @PostMapping("/{containerId}/restart")
  public ResponseEntity<Void> restartContainer(@PathVariable String containerId) {
    dockerClient.restartContainerCmd(containerId).exec();
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{containerId}/logs")
  public ResponseEntity<String> getContainerLogs(@PathVariable String containerId) {
    List<String> logs = new ArrayList<>();
    try {
      dockerClient.logContainerCmd(containerId)
          .withStdOut(true)
          .withStdErr(true)
          .withTail(100) // 마지막 100줄만 가져옴
          .exec(new ResultCallback.Adapter<Frame>() {
            @Override
            public void onNext(Frame frame) {
              logs.add(new String(frame.getPayload()));
            }
          }).awaitCompletion(5, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return ResponseEntity.internalServerError().build();
    }

    return ResponseEntity.ok(String.join("\n", logs));
  }
}