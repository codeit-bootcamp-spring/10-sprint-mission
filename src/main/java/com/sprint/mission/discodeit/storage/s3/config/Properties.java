package com.sprint.mission.discodeit.storage.s3.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws")
public class Properties {

  private Credentials credentials = new Credentials();
  private String region;
  private S3 s3 = new S3();

  public static class Credentials {

    private String accessKey;  // env에서 주입
    private String secretKey;  // env에서 주입

    // getter/setter
    public String getAccessKey() {
      return accessKey;
    }

    public void setAccessKey(String accessKey) {
      this.accessKey = accessKey;
    }

    public String getSecretKey() {
      return secretKey;
    }

    public void setSecretKey(String secretKey) {
      this.secretKey = secretKey;
    }
  }

  public static class S3 {

    private String bucket;

    public String getBucket() {
      return bucket;
    }

    public void setBucket(String bucket) {
      this.bucket = bucket;
    }
  }

  public Credentials getCredentials() {
    return credentials;
  }

  public void setCredentials(Credentials credentials) {
    this.credentials = credentials;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public S3 getS3() {
    return s3;
  }

  public void setS3(S3 s3) {
    this.s3 = s3;
  }
}
