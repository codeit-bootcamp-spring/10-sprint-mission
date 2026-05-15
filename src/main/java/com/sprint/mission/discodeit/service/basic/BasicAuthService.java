package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.mapper.AuthMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BasicAuthService implements AuthService {
    private final UserRepository userRepository;

    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;

}
