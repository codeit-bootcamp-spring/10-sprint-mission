package com.codeit.mission.deokhugam.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/*
    커스텀 예외 코드
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {
  // 공통
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST,
      "Invalid input value"),                             // 입력값 오류
  METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED,
      "Method not allowed"),                        // HTTP 메서드 오류
  MISSING_REQUEST_PARAMETER(HttpStatus.BAD_REQUEST,
      "Missing request parameter"),                 // 필수 파라미터 누락
  METHOD_ARGUMENT_TYPE_MISMATCH(HttpStatus.BAD_REQUEST,
      "Method argument type mismatch"),         // 파라미터 타입 불일치
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
      "Internal server error"),               // 서버 내부 오류
  MISSING_REQUEST_HEADER(HttpStatus.BAD_REQUEST,"Missing request header"), // 필수 헤더 누락

  // 유저 (로그인/회원가입)
  LOGIN_INPUT_INVALID(HttpStatus.UNAUTHORIZED, "Invalid email or password"),
  EMAIL_DUPLICATION(HttpStatus.CONFLICT, "Email address already exists"),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "User not found"),

  // 리뷰
  INVALID_REVIEW_RATING_RANGE(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5"),
  REVIEW_CONTENT_BLANK(HttpStatus.BAD_REQUEST, "Review content cannot be blank"),
  DUPLICATE_REVIEWS(HttpStatus.CONFLICT, "Review with BookId and UserId already exists"),
  REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "Review with Id not found"),
  REVIEW_AUTHOR_MISMATCH(HttpStatus.FORBIDDEN, "Review author mismatch with requestUserId"),
  DUPLICATE_REVIEW_LIKE_REQUEST(HttpStatus.CONFLICT, "Review like request already exists"),
  REVIEW_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "Review like with Id not found"),
  REQUEST_USER_MISMATCH(HttpStatus.BAD_REQUEST, "Parameter and Header requestUserId mismatch"),

  // 커서/페이징 관련
  CURSOR_AFTER_NOT_PROVIDED_TOGETHER(HttpStatus.BAD_REQUEST,
      "Cursor and after must be provided together"),
  CURSOR_OR_AFTER_FORMAT_NOT_VALID(HttpStatus.BAD_REQUEST, "Invalid cursor or after format"),
  INVALID_CURSOR_FORMAT(HttpStatus.BAD_REQUEST, "cursor format is invalid"),
  ILLEGAL_LIMIT_VALUE(HttpStatus.BAD_REQUEST,
      "Limit value must be greater than 0 and less than maximum size"),

  // 배치 작업
  JOB_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Batch Job has failed"),
  JOB_LISTENER_SNAPSHOT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR,
      "Job listener has failed to mark snapshot as failed"),
  POWER_AGGREGATION_BATCH_JOB_FAILED(HttpStatus.INTERNAL_SERVER_ERROR,
      "PowerUser Aggregation Batch job Failed"),
  INVALID_JOB_PARAMETER(HttpStatus.BAD_REQUEST, "Batch Job Parameter is Invalid"),

  // 스냅샷 / 집계
  SNAPSHOT_NOT_FOUND(HttpStatus.NOT_FOUND, "Snapshot is not found"),
  SNAPSHOT_ID_NOT_EQUAL(HttpStatus.CONFLICT, "Snapshot Ids are not equal"),
  SNAPSHOT_NOT_STAGE_BUT_PUBLISH(HttpStatus.BAD_REQUEST, "Only staging snapshot can be published"),
  DOMAIN_NOT_EQUAL(HttpStatus.CONFLICT, "Domain Types are not equal"),
  KEEP_COUNT_INVALID(HttpStatus.BAD_REQUEST, "Keep Count should be greater than or equal to 2"),

  // 도서
  WRONG_FILE_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Wrong file type"),
  S3_UPLOAD_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "S3 upload failed"),
  INVALID_ISBN(HttpStatus.BAD_REQUEST, "Invalid ISBN"),
  DUPLICATE_ISBN(HttpStatus.CONFLICT, "Duplicate ISBN"),
  BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "Book not found"),
  EXTERNAL_API_ERROR(HttpStatus.BAD_GATEWAY, "external api error"),
  OCR_DETECT_FAILED(HttpStatus.BAD_REQUEST, "OCR detection failed"),
  S3_URL_PARSE_FAILED(HttpStatus.BAD_REQUEST, "S3 url parsing failed"),

  // 알림
  NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "Notification not found"),
  NOTIFICATION_NOT_OWNED(HttpStatus.FORBIDDEN,
      "No permission to access or modify this notification"),

  // 댓글
  FORBIDDEN_COMMENT_UPDATE(HttpStatus.FORBIDDEN, "Permission denied to edit this comment"),
  COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Comment not found");

  private final HttpStatus httpStatus;
  private final String message;
}
