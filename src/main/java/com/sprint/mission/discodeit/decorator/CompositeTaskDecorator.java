package com.sprint.mission.discodeit.decorator;

import lombok.RequiredArgsConstructor;
import org.springframework.core.task.TaskDecorator;

import java.util.List;

// 여러 TaskDecorator를 하나로 묶어주는 Decorator
@RequiredArgsConstructor
public class CompositeTaskDecorator implements TaskDecorator {

    // 적용할 Decorator 목록으로, AsyncConfig에서 순서를 정함
    private final List<TaskDecorator> taskDecorators;

    @Override
    public Runnable decorate(Runnable runnable) {
        // List.of(A, B)일 때, 최종 실행 흐름은 A 시작 -> B 시작 -> 실제 작업 -> B 정리 -> A 정리
        // 즉, 목록의 제일 앞에 있는 Decorator가 가장 바깥쪽 래퍼가 된다.
        // Decorator 구조: A(B(실행 작업))
        for (int i = taskDecorators.size() - 1; i >= 0; i--) {
            runnable = taskDecorators.get(i).decorate(runnable);
        }

        return runnable;
    }
}
