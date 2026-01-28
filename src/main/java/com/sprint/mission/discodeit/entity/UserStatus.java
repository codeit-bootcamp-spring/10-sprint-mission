package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

// 사용자 별 마지막 접속 시간
public class UserStatus extends Base{
    private final UUID userID;
    private Instant lastLogin;

    public UserStatus(UUID userID) {
        super();
        this.userID = userID;
        lastLogin = Instant.now();
    }

    public void updatelastLogin() {
        lastLogin = Instant.now();
    }

    // 5분 이내면 online
    public boolean isOnline(){
        return lastLogin.isAfter(Instant.now().minusSeconds(300));
    }

}
