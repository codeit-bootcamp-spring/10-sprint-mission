package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import org.springframework.http.ResponseEntity;

import java.io.InputStream;
import java.util.UUID;

public interface BinaryContentStorage {
    public UUID put(UUID uuid, byte[] bytes);
    public InputStream get(UUID uuid);
    public ResponseEntity<?> download(BinaryContentDto binaryContentDto);
}
