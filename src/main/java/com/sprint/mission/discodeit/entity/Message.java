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
                "\n(내용: %s, 유저명:%s, 채널명: %s)\n",
                content,
                user.getUserName(),
                channel.getChannelName()
        );
    }


}
