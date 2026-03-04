package com.sprint.mission.discodeit.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
@ToString(exclude = {"user", "channel"})
public class Message extends BaseEntity implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  private String content;
  private final User author;
  private final Channel channel;
  private boolean isEdited; // 수정 여부

  @Getter(AccessLevel.NONE)
  private List<UUID> attachmentIds = new ArrayList<>(); // 첨부파일 목록 (BinaryContent 참조 ID)

  public Message(String content, User author, Channel channel, List<UUID> attachmentIds) {
    super();
    this.content = content;
    this.author = author;
    this.channel = channel;
    this.attachmentIds =
        (attachmentIds != null) ? new ArrayList<>(attachmentIds) : new ArrayList<>();
    this.isEdited = false;
  }

  // 메시지 글 수정
  public void updateContent(String content) {
    this.content = content;
    this.isEdited = true;
    this.updated();
  }

  // 메시지 첨부파일 수정
  public void updateAttachmentIds(List<UUID> attachmentIds) {
    this.attachmentIds =
        (attachmentIds != null) ? new ArrayList<>(attachmentIds) : new ArrayList<>();
    this.isEdited = true;
    this.updated();
  }

  // --- getter ---
  public List<UUID> getAttachmentIds() {
    return Collections.unmodifiableList(attachmentIds);
  }
}
