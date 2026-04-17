package com.sprint.mission.discodeit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  //AUTH
  INVALID_CREDENTIALS(401, "AU001", "잘못된 아이디 또는 비밀번호"),
  //USER
  USER_NOT_FOUND(404, "U001", "존재하지 않는 사용자"),
  EMAIL_ALREADY_EXIST(409, "U101", "이미 존재하는 이메일"),
  USER_NAME_ALREADY_EXIST(409, "U102", "이미 존재하는 사용자이름"),
  //USER_STATUS
  USER_STATUS_NOT_FOUND(404, "US001", "존재하지 않는 사용자 상태 정보"),
  USER_STATUS_ALREADY_EXIST(400, "US101", "이미 존재하는 사용자 상태"),
  //CHANNEL
  CHANNEL_NOT_FOUND(404, "CH001", "존재하지 않는 채널"),
  CHANNEL_ALREADY_EXIST(400, "CH101", "이미 존재하는 채널"),
  NOT_ALLOWED_IN_PRIVATE_CHANNEL(400, "CH201", "비공개 채널에 허용되지 않은 기능"),
  //READ_STATUS
  READ_STATUS_NOT_FOUND(404, "RS001", "존재하지 않는 읽기 상태(멤버) 정보"),
  READ_STATUS_ALREADY_EXIST(400, "RS101", "이미 존재하는 읽기 상태(멤버)"),
  //MESSAGE
  MESSAGE_NOT_FOUND(404, "MSG001", "존재하지 않는 메세지"),
  INVALID_MESSAGE(400, "MSG101", "잘못된 메세지 형식"),
  MESSAGE_AUTHOR_ONLY(403, "MSG201", "메시지 작성자와 일치하지 않는 사용자"),
  //BINARY_CONTENT
  BINARY_CONTENT_NOT_FOUND(404, "BC001", "존재하지 않는 바이너리 컨텐츠"),
  // 저장소 관련 (S3)
  STORAGE_UPLOAD_FAILED(500, "BC101", "파일 업로드 중 오류 발생"),
  STORAGE_DOWNLOAD_FAILED(500, "BC102", "파일 다운로드 중 오류 발생"),
  STORAGE_GET_FAILED(500, "BC103", "파일을 읽어오는 중 오류 발생"),

  //그외
  BAD_REQUEST(400, "C001", "입력값 유효성 검증 실패"),
  INTERNAL_SERVER_ERROR(500, "C999", "서버 내부 오류 발생");

  private final int status;
  private final String code;
  private final String message;

}
