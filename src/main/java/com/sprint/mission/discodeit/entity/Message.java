package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "MESSAGES")
public class Message extends BaseUpdatableEntity {

  @Column(nullable = false)
  private String content;

  @ManyToOne
  @JoinColumn(name = "AUTHOR_ID")
  private User author;

  @ManyToOne
  @JoinColumn(name = "CHANNEL_ID")
  private Channel channel;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinTable(
      name = "MESSAGE_ATTACHMENTS",
      joinColumns = @JoinColumn(name = "MESSAGE_ID"),
      inverseJoinColumns = @JoinColumn(name = "ATTACHMENT_ID")
  )
  private List<BinaryContent> attachments = new ArrayList<>();

  public Message(String content, User author, Channel channel) {
    this.content = content;
    this.author = author;
    this.channel = channel;
  }

  public void update(String content) {
    this.content = content;
  }

  public void addAttachment(BinaryContent attachment) {
    this.attachments.add(attachment);
  }
}
