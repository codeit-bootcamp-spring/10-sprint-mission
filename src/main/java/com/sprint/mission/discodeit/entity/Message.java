package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class Message extends Base {

  private String content;
  private UUID channelId;
  private UUID authorId;
  private List<UUID> attachmentIds;

  public Message(UUID authorId, UUID channelId, String content, List<UUID> attachmentIds) {
    this.authorId = authorId;
    this.channelId = channelId;
    this.content = content;
    this.attachmentIds = attachmentIds != null ? attachmentIds : new ArrayList<>();
  }

  public void updateContent(String content) {
    this.content = content;
    updateUpdatedAt(Instant.now());
  }

  public void addAttachmentId(UUID attachmentId) {
    attachmentIds.add(attachmentId);
  }

  @Override
  public String toString() {
    return "{" +
        channelId + ">" +
        authorId + ": " +
        content +
        "(" + getUpdatedAt() + ")}";
  }
}
