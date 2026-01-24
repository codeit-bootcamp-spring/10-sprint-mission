package com.sprint.mission.discodeit.entity;

/*
여러 유저를 한 그룹으로 묶어 권한 관리를 용이하게 하기 위한 그룹 도메인입니다.
디스코드 서버의 '역할'과 유사한 기능을 합니다.
*/

import java.util.UUID;

public class Role extends DefaultEntity{
    private static final long serialVersionUID = 1L;
    private PermissionLevel roleName;//그룹 이름.
    private final UUID user;
    private final UUID channel;

    public Role(PermissionLevel roleName, UUID userID, UUID channelID) {
        this.roleName = roleName;
        this.user = userID;
        this.channel = channelID;
    }

    public PermissionLevel getRoleName() {
        return roleName;
    }

    public UUID getUserID() {
        return user;
    }

    public UUID getChannelID() {
        return channel;
    }

    public void updateGroupName(PermissionLevel roleName) {
        this.updatedAt = System.currentTimeMillis();
        this.roleName = roleName;
    }

    public String toString(){
        return roleName.toString();
    }
}
