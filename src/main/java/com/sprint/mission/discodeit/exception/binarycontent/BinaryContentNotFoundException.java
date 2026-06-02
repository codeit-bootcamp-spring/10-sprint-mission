package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class BinaryContentNotFoundException extends BinaryContentException{
    private BinaryContentNotFoundException(Map<String, Object> details) {
        super(ErrorCode.BINARY_CONTENT_NOT_FOUND, details);
    }

    public static BinaryContentNotFoundException withId(UUID id) {
        return new BinaryContentNotFoundException(Map.of("fileId", id));
    }

    // 여러 개 조회 시 누락된 ID들이 있을 때
    public static BinaryContentNotFoundException withIds(int requestedCount, Collection<UUID> missingIds) {
        return new BinaryContentNotFoundException(Map.of(
                "requestedCount", requestedCount,
                "foundedCount", requestedCount - missingIds.size(),
                "missingIds", missingIds,
                "reason", "일부 파일 데이터가 존재하지 않습니다."
        ));
    }

}
