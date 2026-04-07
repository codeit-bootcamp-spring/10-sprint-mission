package com.sprint.mission.discodeit.dto.message;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.util.UUID;

@Schema(description = "수정할 Message 내용")
public record MessageUpdateRequest(

    @JsonIgnore
    UUID newMessageId,


    @Size(max = 255, message = "메시지는 255자 이하입니다.")
    String newContent
) {

}
