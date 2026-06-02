package com.sprint.mission.discodeit.exception.etc;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class S3UploadException extends DiscodeitException {

    private S3UploadException(ErrorCode errorCode, Map<String, Object> details, Throwable cause) {
        super(errorCode, details, cause);
    }

    // 1. 버킷 없음 예외
    public static S3UploadException bucketNotFound(UUID fileKey, Throwable cause) {
        return new S3UploadException(
                ErrorCode.S3_UPLOAD_BUCKET_NOT_FOUND,
                Map.of("fileKey", fileKey.toString()),
                cause
        );
    }

    // 2. 권한 없음 (403) 예외
    public static S3UploadException accessDenied(UUID fileKey, Throwable cause) {
        return new S3UploadException(
                ErrorCode.S3_UPLOAD_ACCESS_DENIED,
                Map.of("fileKey", fileKey.toString()),
                cause
        );
    }

    // 3. 기본 업로드 실패 예외
    public static S3UploadException defaultError(UUID fileKey, Throwable cause) {
        return new S3UploadException(
                ErrorCode.S3_UPLOAD_ERROR,
                Map.of("fileKey", fileKey.toString()),
                cause
        );
    }
}
