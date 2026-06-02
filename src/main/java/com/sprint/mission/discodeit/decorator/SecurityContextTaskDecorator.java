package com.sprint.mission.discodeit.decorator;

import org.springframework.core.task.TaskDecorator;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContextTaskDecorator implements TaskDecorator {

  @Override
  public Runnable decorate(Runnable runnable) {
    SecurityContext context = SecurityContextHolder.getContext();
    return () -> {
      try {
        SecurityContextHolder.setContext(context);
        runnable.run();
      } finally {
        SecurityContextHolder.clearContext();
      }
    };
  }
}
