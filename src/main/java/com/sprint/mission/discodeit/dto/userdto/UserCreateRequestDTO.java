package com.sprint.mission.discodeit.dto.userdto;

public record UserCreateRequestDTO(
    String username,
    String email,
    String password
) {

}
