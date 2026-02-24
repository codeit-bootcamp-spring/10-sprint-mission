package com.sprint.mission.discodeit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import java.io.Serializable;
import java.util.UUID;

@Getter
public class BinaryContent extends BaseEntity implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String fileName;
  private final String contentType;
  
  @JsonIgnore
  private final byte[] data;

  private final UUID profileUserId;
  private final UUID messageId;

  public BinaryContent(String fileName, String contentType, byte[] data, UUID profileUserId,
      UUID messageId) {
    super();
    this.fileName = fileName;
    this.contentType = contentType;
    this.data = data;
    this.profileUserId = profileUserId;
    this.messageId = messageId;
  }

  @JsonProperty("bytes")
  public byte[] getBytes() {
    return data;
  }

  @JsonProperty("size")
  public long getSize() {
    return (data == null) ? 0L : data.length;
  }
}