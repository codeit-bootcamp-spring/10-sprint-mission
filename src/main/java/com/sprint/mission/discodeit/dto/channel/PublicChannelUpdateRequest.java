package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.Size;

// 채널 정보 수정 시 필요한 데이터
public record PublicChannelUpdateRequest(

    @Size(max = 30, message = "채널명은 30자 이하로 입력해주세요.")
    String newName,

    @Size(max = 50, message = "채널 설명은 50자 이하로 입력해주세요.")
    String newDescription
) {

}
