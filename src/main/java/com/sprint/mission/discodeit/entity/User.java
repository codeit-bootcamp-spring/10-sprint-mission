package com.sprint.mission.discodeit.entity;


import java.util.HashSet;
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
        if(!this.channelList.contains(channel)){
            this.channelList.add(channel);
            channel.addUser(this);
            updateTime();
            System.out.printf("%s 님이 %s 채널에 입장했습니다.%n", this.userId, channel.getName());
        }
    }

    public void exitChannel(Channel channel){
        if(this.channelList.contains(channel)){
            this.channelList.remove(channel);
            channel.kickUser(this);
            updateTime();
        }
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
