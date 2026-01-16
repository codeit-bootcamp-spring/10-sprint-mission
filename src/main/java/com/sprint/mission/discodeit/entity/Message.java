package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Message extends Common {

    private String content;
    private User user;
    private Channel channel;


    public Message(String content,User user,Channel channel){

        this.content = content;
        this.user = user;
        this.channel = channel;
    }

    public String getContent() {
        return content;
    }

    public User getUser(){
        return user;
    }

    public Channel getChannel(){
        return channel;
    }

    //내용 수정일때 update시간 필요
    public void setContent(String content) {
        this.content = content;
        setUpdatedAt();
    }


    @Override
    public String toString() {
        return String.format(
                "Message{id=%s, 내용='%s', userId='%s', channelId='%s', 생성일자=%d, 수정일자=%d}",
                getId(),
                content,
                user.getId(),
                channel.getId(),
                getCreatedAt(),
                getUpdatedAt()
        );
    }


}
