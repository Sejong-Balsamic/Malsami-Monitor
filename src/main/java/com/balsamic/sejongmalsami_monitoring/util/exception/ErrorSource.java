package com.balsamic.sejongmalsami_monitoring.util.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorSource {
  GAME,
  INTRO,
  NOTI,
  MEMBER,
  ADMIN,
  OTHERS;
}
