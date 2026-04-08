package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.UUID;

public interface BinaryContentStorage {
    UUID put(UUID id, byte[] bytes);
    InputStream get(UUID id);
    ResponseEntity<?> download(BinaryContentDto dto);
}
