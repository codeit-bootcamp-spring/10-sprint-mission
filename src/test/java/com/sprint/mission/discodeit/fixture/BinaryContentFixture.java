package com.sprint.mission.discodeit.fixture;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.IntStream;
import net.datafaker.Faker;
import org.jspecify.annotations.Nullable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public final class BinaryContentFixture {
  private static final Faker faker = new Faker();

  private BinaryContentFixture() {
  }

  public static BinaryContent createEntity() {
    return new BinaryContent(getFileName(), getSize(), getContentType(), getBytes());
  }

  public static List<BinaryContent> createEntities() {
    return IntStream.range(0, 3)
        .mapToObj(i -> createEntity())
        .toList();
  }

  public static BinaryContentDto createDto() {
    return BinaryContentDto.builder()
        .fileName(getFileName())
        .size(getSize())
        .contentType(getContentType())
        .bytes(getBytes())
        .build();
  }

  public static MultipartFile createFile() {
    return new FakeMultiPartFile(getFileName(), getSize(), getContentType(), getBytes());
  }

  public static MockMultipartFile toMockFile(BinaryContent profile) {
    return new MockMultipartFile("profile", profile.getFileName(), profile.getContentType(), profile.getBytes());
  }

  public static MockMultipartFile createMockFile() {
    return new MockMultipartFile("profile", getFileName(), getContentType(), getBytes());
  }

  private record FakeMultiPartFile(String name, long size, String contentType,
                                   byte[] bytes) implements MultipartFile {

    @Override
      public String getName() {
        return name;
      }

      @Override
      public @Nullable String getOriginalFilename() {
        return getName();
      }

      @Override
      public @Nullable String getContentType() {
        return contentType;
      }

      @Override
      public boolean isEmpty() {
        return false;
      }

      @Override
      public long getSize() {
        return size;
      }

      @Override
      public byte[] getBytes() throws IOException {
        return bytes;
      }

      @Override
      public InputStream getInputStream() throws IOException {
        return null;
      }

      @Override
      public void transferTo(File dest) throws IOException, IllegalStateException {

      }
    }

  private static long getSize() {
    return faker.number().numberBetween(1L, 999999L);
  }

  private static String getFileName() {
    return faker.file().fileName();
  }

  private static byte[] getBytes() {
    return faker.lorem().sentence().getBytes();
  }

  private static String getContentType() {
    return faker.file().extension();
  }
}
