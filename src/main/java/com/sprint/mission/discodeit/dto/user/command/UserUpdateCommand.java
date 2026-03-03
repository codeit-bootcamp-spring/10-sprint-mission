package com.sprint.mission.discodeit.dto.user.command;

import com.sprint.mission.discodeit.dto.user.UserServiceDTO.UserProfileImageDto;
import com.sprint.mission.discodeit.dto.user.UserServiceDTO.UserUniquenessDto;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import jakarta.annotation.Nullable;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

// todo: error log
@Getter
public class UserUpdateCommand {
    private final UUID id;
    private final String username;
    private final String email;
    private final String password;
    private final UserProfileImageDto profile;

    public UserUpdateCommand(UUID id, UserUpdateRequest request, @Nullable MultipartFile profile) {
        this.id = id;
        this.username = request.username();
        this.email = request.email();
        this.password = request.password();

        if (profile == null) {
            this.profile = null;
        } else {
            try {
                this.profile = new UserProfileImageDto(profile.getOriginalFilename(), profile.getBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public UserUniquenessDto getUserUniquenessDto() {
        return new UserUniquenessDto(username, email);
    }
}
