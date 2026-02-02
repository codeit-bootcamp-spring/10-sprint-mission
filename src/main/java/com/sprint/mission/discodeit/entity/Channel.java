package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.lang.annotation.Inherited;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Channel extends Basic implements Serializable {
    private static final long serialVersionUID = 1L;


    private final ChannelType channelType;
    private String channelName;
    // 채널에 존재하는 메세지들과 유저들 리스트 생성
    private List<UUID> messageIds = new ArrayList<>();
    private List<User> participants = new ArrayList<>();


    public Channel(String channelName){
        super(); // 채널에도 ID 와 create 일자 생성..?
        this.channelType = ChannelType.PUBLIC;
        this.channelName = channelName;
    }

    public Channel(ChannelType channelType) {
        super();
        if (channelType == null) throw new IllegalArgumentException("type은 null일 수 없습니다.");
        this.channelType = channelType;
    }


//    getter setter
//    public String getChannelName() { return channelName; }
//    public List<Message> getMessages() { return messages; }

    public void update(String channelName) {
        this.channelName = channelName;
        update();
    }

    public void addMessages(UUID messageId) {
        if (messageId == null) throw new IllegalArgumentException("messageId null 불가");
        if (!messageIds.contains(messageId)) {
            messageIds.add(messageId);
            super.update();
        }
    }

    // 유저-채널 관련
    public void addParticipant(User user){
        if(!participants.contains(user)){
            participants.add(user);
            // 채널의 참가자 목록에 유저 넣고 양방향 설정해줘야하므로
            // 유저가 참가한 채널 목록에 지금 내 채널이 없으면 채널 넣어주기.
            if(!user.getJoinedChannels().contains(this)){
                user.getJoinedChannels().add(this);
            }
        }
    }

    public void removeParticipant(User user) {
        if (participants.contains(user)) {
            participants.remove(user);
            if (user.getJoinedChannels().contains(this)) {
                user.getJoinedChannels().remove(this);
            }
        }
    }

    // Main에서 출력했을때 객체 자체를 출력해버려서... 재정의 필요
    @Override
    public String toString(){
        return channelName;
    }
}
