package com.sprint.mission.discodeit.async;

import java.util.Map;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class MDCTaskDecorator implements TaskDecorator {

  @Override
  public Runnable decorate(Runnable runnable) {
    Map<String, String> contextMap = MDC.getCopyOfContextMap();
    SecurityContext securityContext = SecurityContextHolder.getContext();

    return () -> {
      try {
        if (contextMap != null) {
          MDC.setContextMap(contextMap);
        }
        SecurityContextHolder.setContext(securityContext);
        runnable.run();

      } finally {
        MDC.clear();
        SecurityContextHolder.clearContext();
      }
    };
  }
}
