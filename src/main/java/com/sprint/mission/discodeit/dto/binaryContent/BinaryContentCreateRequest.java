package com.sprint.mission.discodeit.dto.binaryContent;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class BinaryContentCreateRequest {
    @NotNull(message = "컨텐츠의 길이는 필수입니다")
    private long size;
    @NotNull(message = "컨텐츠의 이름은 필수입니다")
    @Size(min = 2, max = 225, message = "컨텐츠의 이름은 2자 이상 225자 이하입니다")
    private String name;
    @NotNull(message = "컨텐츠의 타입은 필수입니다")
    @Size(min = 2, max = 100, message = "컨텐츠의 타입은 2자 이상 100자 이하입니다")
    private String contentType;
    @NotNull(message = "컨텐츠의 데이터는 필수입니다")
    private byte[] fileData;
}
