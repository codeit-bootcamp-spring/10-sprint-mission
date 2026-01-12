package com.sprint.mission.discodeit.entity;

import java.util.Optional;

public class Message extends BaseEntity {
    private String content;
    private boolean isEdited;

    private final User author;
    private final Channel channel;

    public Message(String content, User author, Channel channel) {
        super();
        this.content = content;
        this.author = author;
        this.channel = channel;
        this.isEdited = false;
    }

    // Getter
    public String getContent() {
        return content;
    }

    public boolean isEdited() {
        return isEdited;
    }


    // Update
    public void updateContent(String newContent) {
        validationContent(content);

        if (!this.content.equals(newContent)){ // 수정 된 내용이 같지 않다면
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