package com.sprint.mission.discodeit.entity;


public class Message extends BaseEntity{
    private User sender;
    private Channel channel;
    private String content;

    private static long nextSequence = 0;//현재 uuid로 정렬이 불가해서 임시 정렬을 위한 시퀀스
    private long sequence;
    public Message(User sender,Channel channel,String content) {
        super();
        this.content = content;
        this.sender = sender;
        this.channel = channel;
        this.sequence = nextSequence++;
    }
    public long getSequence() {
        return sequence;
    }
    public String getContent() {
        return content;
    }
    public User getSender() {
        return sender;
    }
    public Channel getChannel() {
        return channel;
    }
    public void setContent(String content) {
        this.content = content;
        setUpdatedAt();
    }

    @Override
    public String toString() {
        String updated = this.getUpdatedAt() == null ? "수정 이력 없음" : this.getUpdatedAt().toString();
        String message = this.getContent()+"-"
                        +this.getSender().getUserName()
                        +"(생성: "+this.getCreatedAt()
                        +" ,수정: "+updated+")\n";
        return message;
    }
}
