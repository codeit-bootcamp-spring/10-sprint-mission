package com.sprint.mission.discodeit.entity;

import java.util.*;

public class User extends BaseEntity {
    private String username;
    // TODO: User 식별자를 username으로 할 수는 없음 (이름은 중복 가능하기 때문) -> email 필드 추가해서 식별자 생성
    private final List<ChannelUserRole> channelUserRoles = new ArrayList<>();

    public User(String username){
        super();
        validateUsername(username);
        this.username = username;
    }

    private void validateUsername(String username){
        if(username==null || username.trim().isEmpty()){
            throw new IllegalArgumentException("username은 null 이거나 비어있을 수 없습니다.");
        }
    }

    public String getUsername() { return this.username; }
    public List<ChannelUserRole> getChannelUserRoles() { return new ArrayList<>(channelUserRoles); }

    public void updateUsername(String username){
        validateUsername(username);
        this.username = username;
        this.updateTimestamp();
    }
    public void addChannelUserRole(ChannelUserRole role) { this.channelUserRoles.add(role); }
    public void removeChannelUserRole(ChannelUserRole role) { this.channelUserRoles.remove(role); }
}