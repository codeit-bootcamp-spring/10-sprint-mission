package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "messages")
public class Message extends BaseUpdatableEntity {

  @Column(columnDefinition = "text")
  private String content;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id")
  private User author;

  @BatchSize(size = 50)
  @OneToMany(fetch = FetchType.LAZY)
  @JoinTable(
          name = "message_attachments",
          joinColumns = @JoinColumn(name = "message_id"),
          inverseJoinColumns = @JoinColumn(name = "attachment_id")
  )
  private List<BinaryContent> attachments = new ArrayList<>();

  public Message(String content, Channel channel, User author, List<BinaryContent> attachments) {
    this.content = content;
    this.channel = channel;
    this.author = author;
    if (attachments != null) {
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

  public UUID getChannelId() {
    return channel == null ? null : channel.getId();
  }

  public UUID getAuthorId() {
    return author == null ? null : author.getId();
  }

  public List<UUID> getAttachmentIds() {
    if (attachments == null || attachments.isEmpty()) {
      return List.of();
    }
    return Collections.unmodifiableList(
            attachments.stream().map(BinaryContent::getId).toList()
    );
  }
}