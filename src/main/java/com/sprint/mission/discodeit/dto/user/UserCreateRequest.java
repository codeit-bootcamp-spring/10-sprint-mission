package com.sprint.mission.discodeit.dto.user;

// 유저 생성 시 필요한 데이터
public record UserCreateRequest(
    String username,
    String email,
    String password
) {

}
