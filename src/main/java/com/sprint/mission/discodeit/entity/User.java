package com.sprint.mission.discodeit.entity;


import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class User extends DiscordEntity {

    private String userName;
    private String email;
    private String userId;
    private Set<Channel> channelList; // 유저가 현재 가입되어 있는 채널의 목록
    private Set<Message> msgList;

    public User(String userName, String email, String userId){
        this.userName = userName;
        this.email = email;
        this.userId = userId;
        this.channelList = new HashSet<>();
        this.msgList = new HashSet<>();
        updateTime();
    }

    public String getUserName(){
        return this.userName;
    }

    public String getUserEmail(){
        return this.email;
    }

    public String getUserId(){
        return this.userId;
    }

    public Set<Channel> getChannelList(){
        System.out.printf("%s 님이 현재 참가하고 계신 채널 리스트입니다.%n",this.userId);

        this.channelList
                .forEach(System.out::println);
        return this.channelList;
    }

    public void updateName(String name){
        this.userName = name;
        updateTime();
    }


    public void updateEmail(String email){
        this.email = email;
        updateTime();
    }

    public void updateUserId(String userId){
        this.userId = userId;
        updateTime();
    }

    public void joinChannel(Channel channel){
        Objects.requireNonNull(channel, "유효하지 않은 채널입니다.");
        if(channelList.contains(channel)){
            throw new IllegalStateException("이미 가입되어 있는 채널입니다.");
        }
        this.channelList.add(channel);
        channel.addUser(this);
    }

    public void exitChannel(Channel channel){
        Objects.requireNonNull(channel, "유효하지 않은 채널입니다.");
        Channel ch = channelList.stream()
                .filter(channel::equals)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 채널입니다."));

        this.channelList.remove(ch); // 인스턴스의 channelList 필드에서 매개변수로 입력받은 채널을 제거합니다.
        channel.kickUser(this); // 입력받은 채널의 userList에서도 연결을 끊습니다.

        System.out.printf("%s 님이 %s 채널에서 퇴장했습니다.%n", this.userId, channel.getName());
    }

    public Set<Message> getMsgList(){
        System.out.printf("%s 이 작성하신 메세지 목록입니다.%n %s %n", this.userId, this.msgList);
        return this.msgList;
    }

    public void addMsg(Message msg){
        if(!this.msgList.contains(msg)){
            this.msgList.add(msg);
            updateTime();
        }

    }

    public void deleteMsg(Message msg){
        if(this.msgList.contains(msg)){
           this.msgList.remove(msg);
           updateTime();
        }
    }

    public String toString(){
        return String.format("[User] userId: %s | username: %s | email: %s", this.userId, this.userName, this.email);
    }

}
