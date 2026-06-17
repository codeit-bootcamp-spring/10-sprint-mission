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
        // 목록 앞쪽 데코레이터가 가장 바깥쪽 래퍼가 되도록 뒤에서부터 감싼다
        for (int i = taskDecorators.size() - 1; i >= 0; i--) {
            runnable = taskDecorators.get(i).decorate(runnable);
        }
        return runnable;
    }
}
