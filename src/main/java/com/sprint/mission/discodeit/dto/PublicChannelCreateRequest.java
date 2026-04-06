package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PublicChannelCreateRequest {
    @NotBlank(message = "채널 이름은 필수입니다.")
    @Size(min = 2, max = 50, message = "채널 이름은 2자에서 50자 사이여야 합니다.")
    private String name;

    @Size(max = 200, message = "채널 설명은 200자를 초과할 수 없습니다.")
    private String description;

    public PublicChannelCreateRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
