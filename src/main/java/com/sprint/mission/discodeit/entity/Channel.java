package com.sprint.mission.discodeit.entity;

import java.util.HashSet;
import java.util.Set;


public class Channel extends DiscordEntity {

    public enum CHANNEL_TYPE{
        PUBLIC,
        PRIVATE
    }
    private CHANNEL_TYPE channelType;
    private String channelName;
    private String content;
    private Set<User> userList;


    public Channel(CHANNEL_TYPE channelType, String name, String content){
        this.channelType = channelType;
        this.channelName = name;
        this.content = content;
        this.userList = new HashSet<>();
        updateTime();
    }

    public String getName(){
        return this.channelName;
    }

    public CHANNEL_TYPE getType(){
        return this.channelType;
    }

    public String getDesc(){
        return this.content;
    }

    public Set<User> getUsers(){
        //System.out.printf("%s 채널의 유저 리스트입니다.%n",this.channelName);
        //userList.forEach(System.out::println);
        updateTime();
        return this.userList;
    }



    public void updateType(CHANNEL_TYPE channelType){
       this.channelType = channelType;
       updateTime();
    }

    public void updateContent(String content){
        this.content = content;
        updateTime();
    }


    public void updateName(String name){
        updateTime();
        this.channelName = name;
    }

    public void addUser(User user) {
        this.userList.add(user);
        updateTime();
    }
    public void kickUser(User user){
        if(this.userList.contains(user)){
            this.userList.remove(user);
            updateTime();
            System.out.printf("%s 님이 %s 채널에서 퇴장하였습니다.%n", user.getUserName(), this.channelName);
        }
    }

    public String toString(){
        return
                String.format("[Channel] name: %s | type: %s | description: %s", this.channelName, this.channelType, this.content);
    }





}
