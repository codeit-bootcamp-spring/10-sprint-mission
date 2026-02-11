package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.status.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.status.UserStatusRepository;

public class UserServiceImpl {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    public UserServiceImpl(
            UserRepository userRepository,
            UserStatusRepository userStatusRepository,
            BinaryContentRepository binaryContentRepository
    ) {
        this.userRepository = userRepository;
        this.userStatusRepository = userStatusRepository;
        this.binaryContentRepository = binaryContentRepository;
    }


}
