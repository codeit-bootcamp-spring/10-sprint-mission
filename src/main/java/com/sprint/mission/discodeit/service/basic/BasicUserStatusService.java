package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    @Override
    public UserStatus create() {
        return null;
    }

    @Override
    public UserStatus find(UUID Id) {
        return null;
    }

    @Override
    public UserStatus findAll(UUID Id) {
        return null;
    }

    @Override
    public void update() {

    }

    @Override
    public void delete(UUID userId) {

    }
}
