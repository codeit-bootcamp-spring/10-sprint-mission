package com.sprint.mission.discodeit.entity;

import java.lang.annotation.Inherited;
import java.util.ArrayList;
import java.util.List;

public class Channel extends Basic {
    private String channelName;
    // 채널에서 리스트로 user 넣어주면.... 채널 누르면...
    // 또 추가할 것..? -> 현재 채널에 있는 메세지 리스트 정의.
    private List<Message> messages = new ArrayList<>();
    // 채널 만들고........뭐하ㅣ.. 채널 이름 변경까지만 구현
    private List<User> participants = new ArrayList<>();

    public Channel(String channelName){
        super(); // 채널에도 ID 와 create 일자 생성..?
        this.channelName = channelName;
    }

    //getter setter
    public String getChannelName() {
        return channelName;
    }
    public List<Message> getMessages() { return messages; }

    public void update(String channelName) {
        this.channelName = channelName;
        update();
    }

    public void addMessages(Message message) {
        if (!messages.contains(message)) {
            messages.add(message);
            message.setChannel(this);
        }
    }

    public List<User> getParticipants() {
        return participants;
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
