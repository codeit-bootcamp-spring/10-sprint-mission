package com.sprint.mission.discodeit.util;

import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 첨부파일(프사, 메시지 등) 저장하는 용도...
 */
@Component
@RequiredArgsConstructor
public class AttachmentUtil {

  @Value("${discodeit.static.file-directory}")
  private String uploadDir;
  private Path uploadBasePath;

  @PostConstruct
  public void init() {
    uploadBasePath = Path.of(uploadDir).toAbsolutePath().normalize();
  }

  public void saveOne(UUID randomId, MultipartFile attachment) {
    Path uploadDest = uploadBasePath.resolve(randomId + "_" + attachment.getOriginalFilename());

    try {
      if (!Files.exists(uploadBasePath)) {
        Files.createDirectories(uploadBasePath);
      }
      attachment.transferTo(new File(uploadDest.toString()));
    } catch (IOException e) {
      throw new BusinessLogicException(ExceptionCode.ATTACHMENT_SAVE_EXCEPTION);
    }
  }
}
