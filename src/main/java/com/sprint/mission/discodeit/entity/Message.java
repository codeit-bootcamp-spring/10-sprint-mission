package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "messages")
public class Message extends BaseUpdatableEntity {

    @Column
    private String content; // 메시지 내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel; // 메시지가 위치한 채널

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author; // 메시지 작성자

    @BatchSize(size = 50)
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    @JoinTable(
            name = "message_attachments",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private List<BinaryContent> attachments; // 메세지 첨부파일

    // 생성자
    public Message(Channel channel, User author, String content) {
        this.channel = channel;
        this.author = author;
        this.content = content;
        this.attachments = new ArrayList<>();
    }

    // getter
    public List<BinaryContent> getAttachments() {
        return attachments.stream().toList();
    }

    public void addAttachment(BinaryContent attachment) {
        this.attachments.add(attachment);
    }

    public void update(String content) {
        if(content != null) this.content = content;
    }
}
