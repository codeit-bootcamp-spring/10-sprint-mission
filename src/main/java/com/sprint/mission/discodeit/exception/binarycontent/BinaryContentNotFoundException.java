package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

/*
    BinaryContentNotFoundException
    -----------------------------------------
    해당 첨부파일이 시스템 내 존재하지 않을 때 발생하는 예외 클래스
 */
public class BinaryContentNotFoundException extends BinaryContentException {

    public BinaryContentNotFoundException(UUID binaryContentId) {
        super(
                ErrorCode.BINARY_CONTENT_NOT_FOUND,
                Map.of("binaryContentId", binaryContentId)
        );
    }
}
