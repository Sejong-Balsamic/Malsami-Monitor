package com.balsamic.sejongmalsami_monitoring.util.ssh;

import com.balsamic.sejongmalsami_monitoring.util.config.SshConnectionProperties;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Slf4j
@Component
public class SshCommandExecutor {

  private final SshConnectionProperties sshProps;

  public SshCommandExecutor(SshConnectionProperties sshProps) {
    this.sshProps = sshProps;
  }

  /**
   * (1) 기본 SSH 명령어 실행
   *     - sudo 없이 바로 실행
   */
  public String executeCommand(String command) {
    Session session = null;
    ChannelExec channel = null;

    try {
      // 1) 세션 생성
      JSch jsch = new JSch();
      session = jsch.getSession(sshProps.getUsername(), sshProps.getHost(), sshProps.getPort());
      session.setPassword(sshProps.getPassword());

      // 2) 호스트 key 체크 설정
      java.util.Properties config = new java.util.Properties();
      config.put("StrictHostKeyChecking", "no");
      session.setConfig(config);

      // 3) SSH 연결
      session.connect(10_000); // 10초 타임아웃

      // 4) 채널 열기
      channel = (ChannelExec) session.openChannel("exec");
      channel.setCommand(command);
      channel.setErrStream(System.err);

      // 5) 명령어 실행
      InputStream in = channel.getInputStream();
      channel.connect();

      // 6) 결과 읽기
      StringBuilder outputBuffer = new StringBuilder();
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
        String line;
        while ((line = reader.readLine()) != null) {
          outputBuffer.append(line).append("\n");
        }
      }

      // 7) 채널 종료 대기
      while (!channel.isClosed()) {
        Thread.sleep(100);
      }

      int exitStatus = channel.getExitStatus();
      log.debug("SSH command exitStatus: {}, command: {}", exitStatus, command);

      return outputBuffer.toString().trim();

    } catch (Exception e) {
      log.error("SSH command execution failed: {}", command, e);
      return "";
    } finally {
      // 8) 자원 정리
      if (channel != null && !channel.isClosed()) {
        channel.disconnect();
      }
      if (session != null && session.isConnected()) {
        session.disconnect();
      }
    }
  }

  /**
   * (2) sudo + 표준입력(-S)으로 비번 전달 + PATH 설정
   *     - 'tty 없이' sudo 인증
   *     - docker 등 PATH 문제 해결을 위해 env 설정
   *
   * 예시:
   *   echo "<PASSWORD>" | sudo -S bash -c 'export PATH=$PATH:/usr/local/bin && <command>'
   */
  public String executeCommandWithSudoStdin(String command) {
    Session session = null;
    ChannelExec channel = null;

    // SSH 접속 비번 == sudo 비번이라고 가정
    String sudoPassword = sshProps.getPassword();

    // (중요) docker 등 명령어를 찾기 위해 bash -c 에서 PATH 설정
    //   export PATH=$PATH:/usr/local/bin && <원래 command>
    //   예) "echo \"PW\" | sudo -S bash -c 'export PATH=$PATH:/usr/local/bin && /volume1/.../docker_info.sh ...'"
    String wrappedCommand = String.format(
        "echo \"%s\" | sudo -S bash -c 'export PATH=$PATH:/usr/local/bin && %s'",
        sudoPassword, command
    );

    try {
      JSch jsch = new JSch();
      session = jsch.getSession(sshProps.getUsername(), sshProps.getHost(), sshProps.getPort());
      session.setPassword(sshProps.getPassword());

      java.util.Properties config = new java.util.Properties();
      config.put("StrictHostKeyChecking", "no");
      session.setConfig(config);

      session.connect(10_000);

      channel = (ChannelExec) session.openChannel("exec");
      channel.setCommand(wrappedCommand);
      channel.setErrStream(System.err);

      InputStream in = channel.getInputStream();
      channel.connect();

      StringBuilder outputBuffer = new StringBuilder();
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
        String line;
        while ((line = reader.readLine()) != null) {
          outputBuffer.append(line).append("\n");
        }
      }

      while (!channel.isClosed()) {
        Thread.sleep(100);
      }

      int exitStatus = channel.getExitStatus();
      log.debug("SSH command with SUDO exitStatus: {}, command: {}", exitStatus, wrappedCommand);

      return outputBuffer.toString().trim();

    } catch (Exception e) {
      log.error("SSH sudo command execution failed: {}", command, e);
      return "";
    } finally {
      if (channel != null && !channel.isClosed()) {
        channel.disconnect();
      }
      if (session != null && session.isConnected()) {
        session.disconnect();
      }
    }
  }
}
