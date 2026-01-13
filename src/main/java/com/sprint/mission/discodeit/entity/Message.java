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

        // 내용이 같으면 수정되지 않음.
        // 내용이 다르다면 수정함.
        if (!this.content.equals(newContent)){
            this.content = newContent;
            isEdited = true;
            updateTimestamp();
        }
    }

    // Logic
    private void validationContent(String content){
        if (content == null) throw new NullPointerException("content is null");
    }

    // Convenience Method
    public User getAuthor() {
        return author;
    }

    public Channel getChannel() {
        return channel;
    }

}