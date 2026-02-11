package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.utils.Validation;

import java.util.UUID;

public record MessageUpdateRequest(
        UUID meesageId,
        String newContent
) {
    public void validate(){
        if(meesageId == null) throw new IllegalArgumentException("messageId는 null 불가능!!");
        Validation.notBlank(newContent, "메세지 내용");
    }
}
