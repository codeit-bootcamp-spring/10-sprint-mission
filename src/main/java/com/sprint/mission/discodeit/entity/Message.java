package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "messages")
@Getter
@ToString(callSuper = true, exclude = {"author", "channel", "attachments"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseUpdatableEntity {

  @Column(columnDefinition = "TEXT")
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id")
  private User author;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;


  @Getter(AccessLevel.NONE)
  @ManyToMany
  @JoinTable(
      name = "message_attachments",
      joinColumns = @JoinColumn(name = "message_id"),
      inverseJoinColumns = @JoinColumn(name = "attachment_id")
  )
  private List<BinaryContent> attachments = new ArrayList<>();

  public Message(String content, User author, Channel channel, List<BinaryContent> attachments) {
    super();
    this.content = content;
    this.author = author;
    this.channel = channel;
    this.attachments =
        (attachments != null) ? new ArrayList<>(attachments) : new ArrayList<>();
  }

  public void updateContent(String content) {
    this.content = content;
  }

  public void updateAttachments(List<BinaryContent> attachmentIds) {
    this.attachments =
        (attachmentIds != null) ? new ArrayList<>(attachments) : new ArrayList<>();
  }

  // --- getter ---
  public List<BinaryContent> getAttachments() {
    return Collections.unmodifiableList(attachments);
  }
}
