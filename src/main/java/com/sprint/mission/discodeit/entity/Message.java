package com.sprint.mission.discodeit.entity;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class Message extends BaseEntity {
    private String content;
    private boolean isEdited;

    private final User author;
    private final Channel channel;

    // 메시지 순서 보장을 위해 사용
    // AtomicLong은 멀티쓰레딩의 동시성 문제
    private static final AtomicLong sequenceGenerator = new AtomicLong(0);
    private final long sequence;

    public Message(String content, User author, Channel channel) {
        super();
        validationContent(content);
        if(author == null) throw new IllegalArgumentException("작성자는 필수입니다.");
        if(channel == null) throw new IllegalArgumentException("채널은 필수입니다.");

        this.content = content;
        this.author = author;
        this.channel = channel;
        this.isEdited = false;
        this.sequence = sequenceGenerator.getAndIncrement();
    }

    // Getter
    public String getContent() {
        return content;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public long getSequence() {
        return sequence;
    }


    // Update
    public void updateContent(String newContent) {
        validationContent(content);

        if (!this.content.equals(newContent)){ // 내용이 같으면 수정, 다르면 아무일도 일어나지 않음
            this.content = newContent;
            isEdited = true;
            updateTimestamp();
        }
    }

    // validation
    private void validationContent(String content){
        if (content == null || content.trim().isEmpty()) throw new IllegalArgumentException("메세지가 비어있음");
    }

    // Convenience Method
    public User getAuthor() {
        return author;
    }

    public Channel getChannel() {
        return channel;
    }

    // toString
    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", author=" + (author != null ? author.getNickname() : "null") +
                ", channel=" + (channel != null ? channel.getChannelName() : "null") +
                ", isEdited=" + isEdited +
                ", sequence=" + sequence +
                '}';
    }

}