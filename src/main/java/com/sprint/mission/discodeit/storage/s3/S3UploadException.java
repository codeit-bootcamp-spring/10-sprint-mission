package com.sprint.mission.discodeit.storage.s3;

// S3 업로드 실패 시 발생하는 전용 예외
// 전용 예외를 두어 retryFor/@Recover 첫 인자 타입을 정확히 맞춤
public class S3UploadException extends RuntimeException {

  public S3UploadException(String message, Throwable cause) {
    super(message, cause);
  }
}
