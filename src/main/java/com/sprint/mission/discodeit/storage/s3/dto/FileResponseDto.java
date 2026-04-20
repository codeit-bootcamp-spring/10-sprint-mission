package com.sprint.mission.discodeit.storage.s3.dto;

import java.time.Instant;

/**
 * 파일 목록 화면/JSON 응답을 위한 DTO - key: S3 object key - url: 퍼블릭 접근 URL (미리보기/원본열기용) - size: 바이트 크기 -
 * lastModified: 마지막 수정 시각
 */
public record FileResponseDto(
    String key,
    String url,
    long size,
    Instant lastModified
) {

}