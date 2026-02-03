package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.Message;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateBinaryContentRequestDto {
    @NotBlank(message = "파일 이름이 존재하지 않습니다.")
    private String fileName;

    @NotBlank
    private String contentType;

    @Min(value = 1, message = "파일 크기는 0보다 커야합니다.")
    private long size;

    @NotEmpty(message = "파일 내용이 비어있습니다.")
    private byte[] contents;
}
