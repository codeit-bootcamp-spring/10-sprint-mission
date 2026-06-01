package com.sprint.mission.discodeit.dto.userdto;

public record UserUpdateDTO(
    String newUsername,
    String newEmail,
    String newPassword

) {

}
