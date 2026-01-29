package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.utils.Validation;

//유저 생성 용 DTO
public record UserCreateRequest(
        String userName,
        String alias,
        String email,
        String password,
        BinaryContentCreateRequest profileImage
) {
    public void validate(){
        Validation.notBlank(userName, "이름");
        Validation.notBlank(alias, "별명");
        Validation.notBlank(email, "이메일");
        Validation.notBlank(password, "비밀번호");
    }
}
