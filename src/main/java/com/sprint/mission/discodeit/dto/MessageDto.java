package com.sprint.mission.discodeit.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public class MessageDto {
    public record CreateRquest(
            @NotBlank
            String content,

            @NotBlank
            UUID channelId,

            @NotBlank
            UUID authorId

            //선택적으로 여러 개의 첨부파일을 같이 등록할 수 있습니다.
            //List<BinaryContentDto.CreateRequest> attachments
    ) { }

    public record UpdateRequest(
            String content
    ){}
}
