package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDto;
import java.io.InputStream;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface BinaryContentStorage {

    // uuid 키 정보를 바탕으로 byte[] 데이터 저장
    // uuid = binaryContent의 id다.
    UUID put(UUID uuid, byte[] bytes);


    // 키 정보를 바탕으로 byte[]를 읽어 InputStream 타입으로 반환
    // uuid는 binaryContent의 id다.
    InputStream get(UUID uuid);

    // HTTP API로 다운로드 기능 제공
    // BinaryContentDto 정보 바탕으로 파일을 다운로드 수 있는 응답을 반환.
    ResponseEntity<?> download(BinaryContentDto binaryContentDto);
}
