package com.sprint.mission.discodeit.decorator;

import org.springframework.core.task.TaskDecorator;

import java.util.List;

public class CompositeTaskDecorator implements TaskDecorator {

    private final List<TaskDecorator> taskDecorators;

    public CompositeTaskDecorator(List<TaskDecorator> taskDecorators) {
        this.taskDecorators = taskDecorators;
    }

    @Override
    public Runnable decorate(Runnable runnable) {
        Runnable decoratedRunnable = runnable;

        for (TaskDecorator taskDecorator : taskDecorators) {
            decoratedRunnable = taskDecorator.decorate(decoratedRunnable);
        }

        return decoratedRunnable;
    }
}