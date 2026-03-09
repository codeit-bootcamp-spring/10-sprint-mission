package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "messages")
@NoArgsConstructor
public class Message extends BaseUpdatableEntity {
    // 메시지 내용
    @Column(nullable = false, updatable = true)
    private String content;

    // 유저 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    // 채널 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    // 미디어, 파일 등
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "message_attachments",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private List<BinaryContent> attachments = new ArrayList<>();

    public Message(User author, Channel channel, String content, List<BinaryContent> attachments) {
        // id 자동생성 및 초기화
        super();
        // content 초기화
        this.content = content;
        // sentUserId 초기화는 메시지 전송 시점에 설정
        this.author = author;
        // sentChannelId 초기화는 메시지 전송 시점에 설정
        this.channel = channel;
        this.attachments = attachments;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id='" + super.getId() + '\'' +
                ", createdAt=" + super.getCreatedAt() +
                ", updatedAt=" + super.getUpdatedAt() +
                ", content='" + content + '\'' +
                '}';
    }
}
