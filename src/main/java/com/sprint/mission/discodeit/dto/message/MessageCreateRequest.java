package com.sprint.mission.discodeit.dto.message;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.utils.Validation;

import java.util.List;
import java.util.UUID;

public record MessageCreateRequest(
        UUID senderId,
        UUID channelId,
        String content,
        List<BinaryContentCreateRequest> attachments // 첨부파일
) {
    public void validate(){
        if(senderId==null || channelId==null) throw new IllegalArgumentException("senderId나 channelId가 null일수는 없습니다.");
        Validation.notBlank(content, "메세지 내용");
    }

}
