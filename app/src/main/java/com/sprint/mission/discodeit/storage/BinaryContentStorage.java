package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public interface BinaryContentStorage {
    UUID put(UUID binaryContentId, byte[] bytes) throws IOException;
    InputStream get(UUID binaryContentId) throws IOException;
    void delete(UUID binaryContentId) throws IOException;
    ResponseEntity<?> download(BinaryContentDto dto) throws IOException;
}
