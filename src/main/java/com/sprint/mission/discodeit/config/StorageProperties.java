package com.sprint.mission.discodeit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "discodeit.storage")
public class StorageProperties {

  private String type;
  private final Local local = new Local();
  private final S3 s3 = new S3();


  @Getter
  @Setter
  public static class Local {
    private String rootPath;
  }

  @Getter
  @Setter
  public static class S3 {
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucket;
    private long presignedUrlExpiration;
  }
}
