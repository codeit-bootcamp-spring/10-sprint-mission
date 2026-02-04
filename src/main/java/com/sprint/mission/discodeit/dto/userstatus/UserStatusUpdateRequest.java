package com.sprint.mission.discodeit.dto.userstatus;

import java.util.UUID;

public record UserStatusUpdateRequest(
        UUID userStatusId,
        boolean refreshLogin
) {
    public void validate(){
        if(userStatusId == null){
            throw new IllegalStateException("UserStatusId null일 수 없습니다.");
        }
    }
}
