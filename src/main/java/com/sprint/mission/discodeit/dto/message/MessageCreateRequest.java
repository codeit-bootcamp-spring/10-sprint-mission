package com.sprint.mission.discodeit.dto.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

@Schema(description = "Message 생성 정보")
public record MessageCreateRequest(

    @NotNull(message = "채널ID는 필수 입니다.")
    UUID channelId,

    @NotNull(message = "사용자ID는 필수 입니다.")
    @JsonProperty("authorId")
    UUID authorId,

    @Size(max = 255, message = "메시지는 255자 이하입니다.")
    String content,
    
    @JsonIgnore
    List<UUID> attachmentIds
) {

}
