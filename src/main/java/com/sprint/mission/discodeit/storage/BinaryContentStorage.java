package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.io.InputStream;
import java.util.UUID;
import org.springframework.http.ResponseEntity;

public interface BinaryContentStorage {

  UUID put(UUID binaryContentId, byte[] bytes, String fileName, String contentType);

  InputStream get(UUID binaryContentId, String fileName);

  ResponseEntity<?> download(BinaryContentDto metaData);
}
