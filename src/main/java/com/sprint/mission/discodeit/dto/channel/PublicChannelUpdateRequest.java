package com.sprint.mission.discodeit.dto.channel;

// 채널 정보 수정 시 필요한 데이터
public record PublicChannelUpdateRequest(
    String newName,
    String newDescription
) {

}
