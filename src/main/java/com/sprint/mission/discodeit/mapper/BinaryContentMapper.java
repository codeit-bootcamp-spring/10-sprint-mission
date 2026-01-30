package com.sprint.mission.discodeit.mapper;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;

import java.util.UUID;

public class BinaryContentMapper {
    public static BinaryContent toEntity(UUID userId, BinaryContentCreateDTO dto) {
        return new BinaryContent(
                userId,
                dto.data(),
                dto.contentType(),
                dto.filename()
        );
    }

    public static BinaryContent toEntity(UUID userId, UUID messageId, BinaryContentCreateDTO dto) {
        // dto는 record라 null이면 NPE 날 수 있으니, 서비스에서 null 방지하는 게 일반적이지만
        // 여기서도 최소한 방어 가능
        if (dto == null) {
            throw new IllegalArgumentException("attachment dto는 null일 수 없습니다.");
        }
        if (dto.data() == null) {
            throw new IllegalArgumentException("attachment data는 null일 수 없습니다.");
        }
        return new BinaryContent(
                userId,
                messageId,
                dto.data(),
                dto.contentType(),
                dto.filename()
        );
    }
}
