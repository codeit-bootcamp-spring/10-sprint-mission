package com.sprint.mission.discodeit.dto.binarycontent;

public record BinaryContentCreateRequestDto(
        //바이너리컨텐츠에서 생성하는 용도의 Dto
        byte[] content
) {
}
