package com.sprint.mission.discodeit.dto.channel;

import com.sprint.mission.discodeit.utils.Validation;

public record PublicChannelCreateRequest(
        String name
) {
    public void validate() {
        Validation.notBlank(name, "채널 이름");
    }
}
