package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "messages")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseUpdatableEntity {
    // 필드
    @Column(nullable = false, columnDefinition = "text")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false, columnDefinition = "uuid")
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false, columnDefinition = "uuid")
    private User author;
    @BatchSize(size = 100)
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinTable(
            name = "message_attachments",
            joinColumns = @JoinColumn(name="message_id"),
            inverseJoinColumns = @JoinColumn(name="attachment_id")
    )
    private List<BinaryContent> attachments = new ArrayList<>();

    public Message(String content, Channel channel, User author, List<BinaryContent> attachments) {
        this.content = content;
        this.channel = channel;
        this.author = author;
        this.attachments = attachments;
    }

    // Setter
    public void updateContents(String contents) {
        this.content = contents;
        updateUpdatedAt();
    }

    public void updateAttachments(List<BinaryContent> attachments) {
        // JPA는 내부 변경을 감지하지만, 컬렉션의 참조 변경은 감지하지 못한다!
        this.attachments.clear();
        // 참조 교체
        this.attachments = attachments;
    }
}
