package com.sprint.mission.discodeit.exception.etc;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileProcessingException extends DiscodeitException {
    private FileProcessingException(Map<String, Object> details, Throwable cause) {
        super(ErrorCode.FILE_PROCESSING_ERROR, details, cause);
    }

    public static FileProcessingException readFailed(String fileName, Throwable cause) {
        return new FileProcessingException(Map.of(
                "fileName", fileName,
                "action", "READ"
        ), cause);
    }

    public static FileProcessingException storageInitFailed(String path, Throwable cause) {
        return new FileProcessingException(Map.of("path", path, "action", "INIT"), cause);
    }

    public static FileProcessingException writeFailed(UUID id, Throwable cause) {
        return new FileProcessingException(Map.of("fileId", id, "action", "WRITE"), cause);
    }

    public static FileProcessingException fileMissingOnDisk(UUID id) {
        return new FileProcessingException(Map.of(
                "fileId", id,
                "reason", "DB 레코드는 존재하나 물리 파일이 디스크에서 발견되지 않음"
        ), null);
    }

    public static FileProcessingException deleteFailed(UUID id, Throwable cause) {
        return new FileProcessingException(Map.of("fileId", id, "action", "DELETE"), cause);
    }
}
