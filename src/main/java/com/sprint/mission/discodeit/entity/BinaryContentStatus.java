package com.sprint.mission.discodeit.entity;

public enum BinaryContentStatus {
  // 메타 데이터 저장 , 실제 파일은 저장중
  PROCESSING,
  // 실제 파일데이터가 S3에 저장
  SUCCESS,
  FAIL
}
