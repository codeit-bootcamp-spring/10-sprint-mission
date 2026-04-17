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
import lombok.AccessLevel;
import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "messages")
public class Message extends BaseUpdatableEntity {

  @Column
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id")
  private User author;

  @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
  @JoinTable(
      name = "message_attachments",
      joinColumns = @JoinColumn(name = "message_id"),
      inverseJoinColumns = @JoinColumn(name = "attachment_id")
  )
  @BatchSize(size = 100)
  private List<BinaryContent> attachments = new ArrayList<>();

  public static Message create(String content, Channel channel, User author,
      List<BinaryContent> attachments) {
    return new Message(content, channel, author, attachments);
  }

  private Message(String content, Channel channel, User author, List<BinaryContent> attachments) {
    if (channel == null || author == null) {
      throw new IllegalArgumentException("채널 또는 작성자가 Null임");
    }
    if ((content == null || content.isEmpty()) && (attachments == null || attachments.isEmpty())) {
      throw new IllegalArgumentException("메세지 내용과 첨부파일이 모두 비어있음");
    }
    this.content = content;
    this.channel = channel;
    this.author = author;
    if (attachments != null && !attachments.isEmpty()) {
      this.attachments.addAll(attachments);
    }
  }

  public void update(String newContent, List<BinaryContent> newAttachments) {

    if (newContent != null && !newContent.equals(this.content)) {
      this.content = newContent;
    }
    if (newAttachments != null) {
      this.attachments.clear();
      this.attachments.addAll(newAttachments);
    }

  }
}
