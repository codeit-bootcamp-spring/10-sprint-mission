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
  @JoinColumn(name = "CHANNEL_ID")
  private Channel channel;

  @ManyToOne
  @JoinColumn(name = "AUTHOR_ID")
  private User author;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinTable(
      name = "MESSAGE_ATTACHMENTS",
      joinColumns = @JoinColumn(name = "MESSAGE_ID"),
      inverseJoinColumns = @JoinColumn(name = "ATTACHMENT_ID")
  )
  private List<BinaryContent> attachments = new ArrayList<>();

  public Message(String content, Channel channel, User author) {
    this.content = content;
    this.channel = channel;
    this.author = author;
  }

  public void update(String content) {
    updateIfChanged(this.content, content, val -> this.content = val);
  }

  public void addAttachment(BinaryContent attachment) {
    this.attachments.add(attachment);
  }
}
