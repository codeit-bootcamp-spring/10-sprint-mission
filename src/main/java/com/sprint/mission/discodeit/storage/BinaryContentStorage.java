package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.UUID;

public interface BinaryContentStorage {
    // id 기반 bytes 저장..
    UUID put(UUID id, byte[] bytes);
    // id를 기반으로 inputstream으로 반환.
    InputStream get(UUID id);
    // http 다운로드 응답 생성.
    ResponseEntity<?> download(BinaryContentDto dto);
}
