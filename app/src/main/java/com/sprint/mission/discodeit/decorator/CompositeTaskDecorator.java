package com.sprint.mission.discodeit.decorator;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskDecorator;

@RequiredArgsConstructor
public class CompositeTaskDecorator implements TaskDecorator {

  private final List<TaskDecorator> decorators;

  @Override
  public Runnable decorate(Runnable runnable) {
    for (int i = decorators.size() - 1; i >= 0; i--) {
      runnable = decorators.get(i).decorate(runnable);
    }
    return runnable;
  }
}
