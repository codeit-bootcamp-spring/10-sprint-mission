package com.sprint.mission.discodeit.dto.login;

import com.sprint.mission.discodeit.utils.Validation;

public record LoginRequest(
        String userName,
        String password
) {
    public void validate(){
        Validation.notBlank(userName, "유저 이름은 null 불가");
        Validation.notBlank(password, "패스워드는 null 불가");
    }
}
