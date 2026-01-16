package org.example.entity;

public class Message extends BaseEntity {

    private String content;
    private User sender;
    private Channel channel;
    private boolean editedAt;
    private boolean deletedAt;

    public Message(String content, User sender, Channel channel) {
        super();
        this.content = content;
        this.sender = sender;
        this.channel = channel;
        this.editedAt = false;
        this.deletedAt = false;
    }
    //연관관계 편의 메서드 - 메시지 삭제 시 채널, 유저에서도 삭제
    public void addToChannelAndUser(){
        if (this.channel != null && !this.channel.getMessages().contains(this)) {
            this.channel.getMessages().add(this);
        }
        if (this.sender != null && !this.sender.getMessages().contains(this)) {
            this.sender.getMessages().add(this);
        }
    }
    public void removeFromChannelAndUser() {
        if (this.channel != null) {
            this.channel.getMessages().remove(this);
        }
        if (this.sender != null) {
            this.sender.getMessages().remove(this);
        }
    }

    // Setters
    public void updateContent(String content) {
        this.content = content;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateSender(User sender) {
        this.sender = sender;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateChannel(Channel channel) {
        this.channel = channel;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateEditedAt(boolean editedAt) {
        this.editedAt = editedAt;
        this.updatedAt = System.currentTimeMillis();
    }

    public void updateDeletedAt(boolean deletedAt) {
        this.deletedAt = deletedAt;
        this.updatedAt = System.currentTimeMillis();
    }
    //getter
    public String getContent() {
        return content;
    }

    public User getSender() {
        return sender;
    }

    public Channel getChannel() {
        return channel;
    }
    public boolean isDeletedAt() {
        return deletedAt;
    }

    public boolean isEditedAt() {
        return editedAt;
    }
}
