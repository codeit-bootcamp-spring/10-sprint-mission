package com.sprint.mission.discodeit.exception.binarycontent;

import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;
import java.util.UUID;

/*
    BinaryContentFileProcessingErrorException
    -----------------------------------------
    첨부파일 변환 (MultipartFile -> BinaryContent)이 실패했을 때 발생하는 예외 클래스
 */
public class BinaryContentFileProcessingErrorException extends BinaryContentException {

    public BinaryContentFileProcessingErrorException(UUID messageId, String filename) {
        super(ErrorCode.BINARY_CONTENT_FILE_PROCESSING_ERROR,
                Map.of(
                        "messageId", messageId,
                        "filename", filename
                )
        );
    }

    public BinaryContentFileProcessingErrorException(String username, String filename) {
        super(ErrorCode.BINARY_CONTENT_FILE_PROCESSING_ERROR,
                Map.of(
                        "username", username,
                        "filename", filename
                )
        );
    }
}
