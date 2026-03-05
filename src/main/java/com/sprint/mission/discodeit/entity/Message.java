package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Entity
@Table(name="messages")
@NoArgsConstructor
public class Message extends BaseUpdatableEntity {

//  private static final long serialVersionUID = 1L;
//
//  private UUID id;
//  private Instant createdAt;
//  private Instant updatedAt;
  @Column
  private String content;

  // Channel 연결 단방향 , 메세지 주인쪽 일대다
  @ManyToOne(optional = false)
  @JoinColumn(name = "channel_id", nullable = false)
  private Channel channel;

  // User 연결 단방향 , 메세지가 주인 일대다
  @ManyToOne(optional = true)
  @JoinColumn(name = "author_id")
  private User author;

  // message_attahments 다대다 중간 다리 -> 조인 사용
  @ManyToMany
  @JoinTable(
          name = "message_attachments",
          joinColumns = @JoinColumn(name="message_id"),
          inverseJoinColumns = @JoinColumn(name = "attachment_id")
  )
  private List<BinaryContent> attachments = new ArrayList<>();

  public Message(String content, Channel channel, User author, List<BinaryContent> attachments) {
//    this.id = UUID.randomUUID();
//    this.createdAt = Instant.now();
    this.content = content;
    this.channel = channel;
    this.author = author;
    this.attachments = attachments;
  }

  public void update(String newContent) {
    if (newContent != null && !newContent.equals(this.content)) {
      this.content = newContent;
    }
  }

//  // 신규 추가
//  public void addAttachment(BinaryContent attachment) {
//    if (attachment != null) {
//      this.attachments.add(attachment);
//    }
//  }
//
//  public void removeAttachment(BinaryContent attachment) {
//    if (attachment != null) {
//      this.attachments.remove(attachment);
//    }
//  }

}
