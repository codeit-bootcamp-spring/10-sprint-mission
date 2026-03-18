package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@NoArgsConstructor
@Table(name = "messages")
@Getter
public class Message extends BaseUpdatableEntity {
    @Column(nullable = true)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @OneToMany(cascade = { CascadeType.PERSIST, CascadeType.REMOVE }, orphanRemoval = true)
    @JoinTable(
            name = "message_attachments",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private final List<BinaryContent> attachments = new ArrayList<>();

    public Message(Channel channel, User author, String content) {
        super();
        this.channel = channel;
        this.author = author;
        this.content = content;
    }

    public List<BinaryContent> getAttachments() {
        return Collections.unmodifiableList(this.attachments);
    }
    public void addAttachment(BinaryContent attachment) {
        this.attachments.add(attachment);
    }
    public void removeAttachment(BinaryContent attachment) {
        this.attachments.remove(attachment);
    }

    public void updateMessage(String message) {
        this.content = message;
    }
}
