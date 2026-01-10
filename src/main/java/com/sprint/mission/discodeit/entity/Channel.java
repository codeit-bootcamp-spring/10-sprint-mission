package com.sprint.mission.discodeit.entity;

import java.util.ArrayList;
import java.util.List;

public class Channel extends DiscodeEntity{

    public enum CHANNEL_TYPE{
        PUBLIC,
        PRIVATE
    }
    private CHANNEL_TYPE channelType;
    private String channelName;
    private String channelDesc;
    private List<User> users;


    public Channel(CHANNEL_TYPE channelType, String name, String channelDesc){
        this.channelType = channelType;
        this.channelName = name;
        this.channelDesc = channelDesc;
        this.users = null;
        updateTime();
    }

    public String getName(){
        return this.channelName;
    }

    public CHANNEL_TYPE getType(){
        return this.channelType;
    }

    public String getDesc(){
        return this.channelDesc;
    }

    public List<User> getUsers(){
        return this.users;
    }



    public void updateType(CHANNEL_TYPE channelType){
       this.channelType = channelType;
       updateTime();
    }

    public void updateDesc(String desc){
        this.channelDesc = desc;
        updateTime();
    }


    public void updateName(String name){
        updateTime();
        this.channelName = name;
    }

    public String toString(){
        return "TYPE: " + this.channelType + " / NAME: " + this.channelName + " / Description: " + this.channelDesc;
    }





}
