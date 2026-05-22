package com.sprint.mission.discodeit.auth;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@Slf4j
public class SessionEventLoggerListener implements HttpSessionListener {

  @Override
  public void sessionCreated(HttpSessionEvent se) {
    log.info("====> 세션 생성됨: ID = {}", se.getSession().getId());
  }

  @Override
  public void sessionDestroyed(HttpSessionEvent se) {
    log.info("====> 세션 소멸됨: ID = {}", se.getSession().getId());
  }
}