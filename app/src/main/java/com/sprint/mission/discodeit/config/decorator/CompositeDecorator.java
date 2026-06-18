package com.sprint.mission.discodeit.config.decorator;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.core.task.TaskDecorator;

@AllArgsConstructor
public class CompositeDecorator implements TaskDecorator {

  private final List<TaskDecorator> decorators;

  @Override
  public Runnable decorate(Runnable runnable) {
    for (int i = decorators.size() - 1; i >= 0; i--) {
      runnable = decorators.get(i).decorate(runnable);
    }
    return runnable;
  }
}
