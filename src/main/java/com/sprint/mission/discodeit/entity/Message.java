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
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Entity
@Table(name = "messages")
@Getter
public class Message extends BaseUpdatableEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "author_id")
  private User author;

  @Column(name = "content")
  private String content;

  @OneToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "message_attachments",
      joinColumns = @JoinColumn(name = "message_id"),
      inverseJoinColumns = @JoinColumn(name = "attachment_id")
  )
  private List<BinaryContent> attachments = new ArrayList<>();

  protected Message() {
  }

  public Message(Channel channel, User author, String content, List<BinaryContent> attachments) {
    this.channel = channel;
    this.author = author;
    this.content = content;
    if (attachments != null) {
      this.attachments.addAll(attachments);
    }
  }


  public UUID getChannelId() {
    return channel == null ? null : channel.getId();
  }

  public UUID getUserId() {
    return author == null ? null : author.getId();
  }

  public List<UUID> getAttachmentIds() {
    return attachments.stream()
        .map(BinaryContent::getId)
        .toList();
  }

  public void updateContent(String content) {
    this.content = content;
    touch();
  }
}