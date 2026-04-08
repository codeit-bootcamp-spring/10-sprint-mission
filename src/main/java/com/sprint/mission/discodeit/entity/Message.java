package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import com.sprint.mission.discodeit.exception.message.MessageSenderMismatchException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "messages")
public class Message extends BaseUpdatableEntity {

    @ManyToOne(optional = false)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @OneToMany
    @JoinTable(
            name = "message_attachments",
            joinColumns = @JoinColumn(name = "message_id"),
            inverseJoinColumns = @JoinColumn(name = "attachment_id")
    )
    private List<BinaryContent> attachments;

    public Message(User author, Channel channel, String content) {
        this.content = content;
        this.attachments = new ArrayList<>();
        assignAuthor(author);
        assignChannel(channel);
    }

    public void updateContent(String content) {
        Optional.ofNullable(content)
                .ifPresent(value -> this.content = value);
        markUpdated();
    }

    public void updateAttachments(List<BinaryContent> attachments) {
        this.attachments = attachments;
        markUpdated();
    }

    public void validateSender(UUID userId) {
        if (author == null || !author.getId().equals(userId)) {
            Map<String, Object> details = new java.util.LinkedHashMap<>();
            details.put("userId", userId);
            if (getId() != null) {
                details.put("messageId", getId());
            }
            throw new MessageSenderMismatchException(
                    "메세지의 sender가 아닙니다. userId: " + userId,
                    details
            );
        }
    }

    public void assignAuthor(User author) {
        if (this.author == author) {
            return;
        }

        User previousAuthor = this.author;
        this.author = author;

        if (previousAuthor != null) {
            previousAuthor.removeMessage(this);
        }
        if (author != null && !author.getMessages().contains(this)) {
            author.addMessage(this);
        }
    }

    public void assignChannel(Channel channel) {
        if (this.channel == channel) {
            return;
        }

        Channel previousChannel = this.channel;
        this.channel = channel;

        if (previousChannel != null) {
            previousChannel.removeMessage(this);
        }
        if (channel != null && !channel.getMessages().contains(this)) {
            channel.addMessage(this);
        }
    }

    public void clearAuthor() {
        this.author = null;
    }

    public void clearChannel() {
        this.channel = null;
    }

    @PrePersist
    private void onPrePersist() {
        if (channel != null) {
            channel.updateLastMessageAt(getCreatedAt() == null ? Instant.now() : getCreatedAt());
        }
    }

}
