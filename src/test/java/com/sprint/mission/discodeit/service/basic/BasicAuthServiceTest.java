package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BasicAuthServiceTest {

    Instant now;
    Instant nowMinus10;

    @BeforeEach
    void setUp() {
        now = Instant.now();
        nowMinus10 = now.minus(10, ChronoUnit.MINUTES);
    }

    private User createUser(String email, String username, String password, BinaryContent profile) {
        User user = new User(email, username, password, profile);
        ReflectionTestUtils.setField(user, "id", UUID.randomUUID());

        return user;
    }

    private UserDto createUserDto(User user) {
        BinaryContent profile = user.getProfile() == null ? null : user.getProfile();
        BinaryContentDto profileDto = null;
        if (profile != null) {
            profileDto = new BinaryContentDto(profile.getId(), profile.getFileName(), profile.getSize(), profile.getContentType(), BinaryContentStatus.SUCCESS);
        }

        return new UserDto(user.getId(), user.getUsername(), user.getEmail(), profileDto, true, user.getRole());
    }
}