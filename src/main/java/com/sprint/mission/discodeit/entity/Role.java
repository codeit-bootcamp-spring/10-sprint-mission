package com.sprint.mission.discodeit.entity;

import java.util.Set;
/*
여러 유저를 한 그룹으로 묶어 권한 관리를 용이하게 하기 위한 그룹 도메인입니다.
디스코드 서버의 '역할'과 유사한 기능을 합니다.
*/

public class Role extends DefaultEntity{
    private String roleName;//그룹 이름.
    private final User user;
    private final Channel channel;

    public Role(String roleName, User user, Channel channel) {
        this.roleName = roleName;
        this.user = user;
        this.channel = channel;
    }

    public String getRoleName() {
        return roleName;
    }

    public User getUsers() {
        return user;
    }

    public Channel getChannel() {
        return channel;
    }

    public void updateGroupName(String groupName) {
        this.updatedAt = System.currentTimeMillis();
        this.roleName = groupName;
    }

    public String toString(){
        return roleName + " : " + user;
    }
}
