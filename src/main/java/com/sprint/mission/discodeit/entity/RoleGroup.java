package com.sprint.mission.discodeit.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
/*
여러 유저를 한 그룹으로 묶어 권한 관리를 용이하게 하기 위한 도메인입니다.
디스코드 서버의 역할과 유사한 기능을 합니다.
*/

public class RoleGroup extends DefaultEntity{
    private String groupName;//그룹 이름.
    private Set<User> users;

    public RoleGroup(String groupName) {
        this.groupName = groupName;
        this.users = new HashSet<>();
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

    public void addUser(User user) {
        users.add(user);
    }

    public void removeUser(UUID id) {
        users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .ifPresent(users::remove);
    }

}
