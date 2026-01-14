package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
/*
여러 유저를 한 그룹으로 묶어 권한 관리를 용이하게 하기 위한 그룹 도메인입니다.
디스코드 서버의 '역할'과 유사한 기능을 합니다.
*/

public class RoleGroup extends DefaultEntity{
    private String groupName;//그룹 이름.
    private Set<User> users;// 그룹에 속하는 유저들의 목록.
    private Set<Channel> allowedChannels;//그룹 전원이 사용 가능한 채널.

    public RoleGroup(String groupName) {
        this.groupName = groupName;
        this.users = new HashSet<>();
        this.allowedChannels = new HashSet<>();
    }

    public String getGroupName() {
        return groupName;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void updateGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void addUser(User user) { //그룹에 속하는 이용자 추가, 그룹의 권한 유저에 이양
        users.add(user);
        for(Channel channel : this.allowedChannels){
            channel.addAllowedUser(user);
            user.addAllowedChannel(channel);
        }


        
    }

    public void removeUser(UUID id) { //그룹에서 제외, 권한도 회수
        User user = users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);

        for(Channel channel : this.allowedChannels){
            channel.removeAllowedUser(user);
            user.removeAllowedChannel(channel);
        }

        users.remove(user);

    }

    public void addAllowedChannel(Channel channel) {
        allowedChannels.add(channel);
        users.forEach(user -> user.addAllowedChannel(channel));
        users.forEach(channel::addAllowedUser);
    }

    public void removeAllowedChannel(Channel channel){
        allowedChannels.remove(channel);
        users.forEach(channel::removeAllowedUser);
        users.forEach(user -> user.removeAllowedChannel(channel));
    }

    public Set<Channel> getAllowedChannels() {
        return allowedChannels;
    }

    public String toString() {
        return "역할 이름: " + groupName + ", 구성 유저: " + users + ", 사용 가능한 채널: " + allowedChannels;
    }

}
