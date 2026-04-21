package com.sprint.mission.discodeit.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "messages")
public class Message extends BaseUpdatableEntity{
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    // cascade로 전체가 같이 처리되도록하고 orphanRemoval로 만약 리스트에서 객체가 빠진다면 해당 데이터를 해당 테이블에서 지움
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "message_attachments",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private final List<BinaryContent> attachments = new ArrayList<>();

    public Message(User author, String text, Channel channel) {
        this.author = author;
        this.content = text;
        this.channel = channel;
    }

    public void updateMessage(String newText){
        this.content = newText;
    }

    public void addAttachment(BinaryContent attachment){
        attachments.add(attachment);
    }
}
