package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "messages")
public class Message extends BaseUpdatableEntity {

  @Column(columnDefinition = "TEXT")
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id")
  private User author;

  @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,
      CascadeType.REMOVE}, orphanRemoval = true)
  @JoinTable(
      name = "message_attachments",
      joinColumns = @JoinColumn(name = "message_id", nullable = false),
      inverseJoinColumns = @JoinColumn(name = "attachment_id", nullable = false, unique = true)
  )
  private List<BinaryContent> attachments = new ArrayList<>();

  public Message(String content, Channel channel, User author) {
    this.content = content;
    this.channel = channel;
    this.author = author;
  }

  public void update(String newContent) {
    if (newContent != null && !newContent.equals(this.content)) {
      this.content = newContent;
    }
  }

  public void addAttachments(List<BinaryContent> attachments) {
    if (attachments == null || attachments.isEmpty()) {
      return;
    }
    this.attachments.addAll(attachments);
  }
}