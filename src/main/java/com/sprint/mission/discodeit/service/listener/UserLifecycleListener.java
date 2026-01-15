package com.sprint.mission.discodeit.service.listener;

import java.util.UUID;

// 함수형 인터페이스 -> 람다 표현식
public interface UserLifecycleListener {
    // 유저가 삭제될 때 호출될 메서드
    void onUserDelete(UUID userId);
}

// 제어의 역전
// UserService는 수동적으로 이벤트만 발생시키고, 실제 흐름(채널 삭제 등)은 외부에서 주입된 로직에 의해 결정