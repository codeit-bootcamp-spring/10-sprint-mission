package com.sprint.mission.discodeit.global.config.async;

import java.util.Map;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import org.springframework.stereotype.Component;

@Component
public class MdcTaskDecorator implements TaskDecorator {

  @Override
  public Runnable decorate(Runnable runnable) {
    Map<String, String> contextMap = MDC.getCopyOfContextMap();
    return () -> {
      try {
        if (contextMap != null) {
          MDC.setContextMap(contextMap);
        }
        runnable.run();
      } finally {
        MDC.clear();
      }
    };
  }
}
