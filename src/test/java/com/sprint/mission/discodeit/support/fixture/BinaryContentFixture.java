package com.sprint.mission.discodeit.support.fixture;

import com.sprint.mission.discodeit.binarycontent.entity.BinaryContent;
import java.util.List;
import org.instancio.Instancio;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public final class BinaryContentFixture {

  private static final BaseFixture baseFixture = BaseFixture.INSTANT;

  public static BinaryContent createEntity() {
    return baseFixture.baseEntity(BinaryContent.class).create();
  }

  public static List<BinaryContent> createEntities() {
    return baseFixture.baseEntities(BinaryContent.class).create();
  }

  public static MultipartFile createFile() {
    return Instancio.create(MockMultipartFile.class);
  }

  public static MockMultipartFile createMockFile() {
    return Instancio.create(MockMultipartFile.class);
  }
}
