package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class PublicChannelCreateRequest {
    @NotNull(message = "공용 채널은 채널 이름이 필수입니다")
    @Size(min = 2, max = 100, message = "채널 이름은 2자 이상 100자 이하입니다")
    private String name;

    @NotNull(message = "공용 채널은 채널 설명이 필수입니다")
    @Size(min = 2, max = 500, message = "채널 이름은 2자 이상 500자 이하입니다")
    private String description;
}
