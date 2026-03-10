package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.UUID;


public interface BinaryContentStorage { //바이너리 데이터의 저장/로드를 담당하는 컴포넌트
    UUID put(UUID id, byte[] bytes); //UUID key 정보(BinaryContent의 id)를 바탕으로 byte[] 데이터를 저장.
    InputStream get(UUID id);

    //BinaryContentDto 정보를 바탕으로 파일을 다운로드할 수 있는 응답 반환
    ResponseEntity<?> download(BinaryContentDto binaryContentDto); //HTTP API로 다운로드 기능 제공
}
