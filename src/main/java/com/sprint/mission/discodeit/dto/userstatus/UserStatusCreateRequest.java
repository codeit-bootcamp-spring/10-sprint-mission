package com.sprint.mission.discodeit.dto.userstatus;

import java.util.UUID;

public record UserStatusCreateRequest(
        UUID userId
) {
    public void validate(){
        if(userId==null) throw new IllegalArgumentException("userId는 null일 수 없습니다.");
    }
}
