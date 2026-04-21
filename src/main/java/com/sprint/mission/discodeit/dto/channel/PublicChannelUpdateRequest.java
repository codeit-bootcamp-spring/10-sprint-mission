package com.sprint.mission.discodeit.dto.channel;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class PublicChannelUpdateRequest {
    @Size(min = 2, max = 100, message = "채널 이름은 2자 이상 100자 이하입니다")
    private String newName;

    @Size(min = 2, max = 500, message = "채널 이름은 2자 이상 500자 이하입니다")
    private String newDescription;
}
