package com.sprint.mission.discodeit.decorator;

import org.springframework.core.task.TaskDecorator;

import java.util.List;

public class CompositeTaskDecorator implements TaskDecorator {
    private final List<TaskDecorator> decorators;

    public CompositeTaskDecorator(List<TaskDecorator> decorators){
        this.decorators = decorators;
    }

    @Override
    public Runnable decorate(Runnable runnable) {
        for(int i = decorators.size() - 1; i >=0; i--){
            runnable = decorators.get(i).decorate(runnable);
        }

        return runnable;
    }
}
