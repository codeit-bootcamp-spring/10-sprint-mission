package com.sprint.mission.discodeit.exception.file;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;

import java.util.Map;

public class FileUploadFailException extends FileException {
    public FileUploadFailException() {
        super(ErrorCode.FILE_UPLOAD_FAIL, Map.of(
                "resourceId", "MULTIPART_FILE",
                "operation", "FILE_UPLOAD",
                "currentState", "STREAM_TRANSFER_ERROR",
                "rule", "업로드된 파일 스트림이 유효하고 서버 스토리지에 기록 가능해야 함"
        ));
    }
}
