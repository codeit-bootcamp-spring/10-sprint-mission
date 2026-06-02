package com.sprint.mission.discodeit.exception.etc;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;
import java.util.UUID;

public class S3DownloadException extends DiscodeitException {

    private S3DownloadException(ErrorCode errorCode, Map<String, Object> details, Throwable cause) {
        super(errorCode, details, cause);
    }

    // 1. 파일 없음 (S3 실물 부재) 예외
    public static S3DownloadException fileNotFound(UUID fileKey, Throwable cause) {
        return new S3DownloadException(
                ErrorCode.S3_DOWNLOAD_FILE_NOT_FOUND,
                Map.of("fileKey", fileKey.toString()),
                cause
        );
    }

    // 2. 버킷 없음 예외
    public static S3DownloadException bucketNotFound(UUID fileKey, Throwable cause) {
        return new S3DownloadException(
                ErrorCode.S3_DOWNLOAD_BUCKET_NOT_FOUND,
                Map.of("fileKey", fileKey.toString()),
                cause
        );
    }

    // 3. 읽기 권한 없음 (403) 예외
    public static S3DownloadException accessDenied(UUID fileKey, Throwable cause) {
        return new S3DownloadException(
                ErrorCode.S3_DOWNLOAD_ACCESS_DENIED,
                Map.of("fileKey", fileKey.toString()),
                cause
        );
    }

    // 4. Presigned URL 생성 실패 예외
    public static S3DownloadException presignError(UUID fileKey, Throwable cause) {
        return new S3DownloadException(
                ErrorCode.S3_DOWNLOAD_PRESIGN_ERROR,
                Map.of("fileKey", fileKey.toString()),
                cause
        );
    }

    // 5. 기본 다운로드 실패 예외
    public static S3DownloadException defaultError(UUID fileKey, Throwable cause) {
        return new S3DownloadException(
                ErrorCode.S3_DOWNLOAD_ERROR,
                Map.of("fileKey", fileKey.toString()),
                cause
        );
    }
}
