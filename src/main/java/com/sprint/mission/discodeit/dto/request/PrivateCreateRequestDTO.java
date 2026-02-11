package com.sprint.mission.discodeit.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record PrivateCreateRequestDTO(
    @NotEmpty(message = "한명 이상의 참여자가 필요합니다")
    List<UUID> participantIds
    // List<User>라면 User 객체를 호출하는 쪽(controller)에서 DB를 조회해 만들어놔야함
    // 이건 Service가 해야할 일(controller는 이 id들을 처리하 달라고 요청만)
) {}
