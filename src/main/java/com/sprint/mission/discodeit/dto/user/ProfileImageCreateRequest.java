package com.sprint.mission.discodeit.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProfileImageCreateRequest(

    @NotBlank(message = "파일이름은 필수입니다.")
    String fileName,

    @NotBlank(message = "contentType은 필수입니다.")
    String contentType,

    @NotNull(message = "파일 데이터는 필수입니다.")
    @Size(min = 1, message = "파일데이터가 없습니다.")
    byte[] data
) {

}
