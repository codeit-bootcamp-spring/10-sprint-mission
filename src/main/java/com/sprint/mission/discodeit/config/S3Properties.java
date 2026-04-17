package com.sprint.mission.discodeit.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "discodeit.storage.s3")
@Getter
@AllArgsConstructor
public class S3Properties {

  private final String accessKey;
  private final String secretKey;
  private final String region;
  private final String bucket;
  private final int presignedUrlExpiration;
}
