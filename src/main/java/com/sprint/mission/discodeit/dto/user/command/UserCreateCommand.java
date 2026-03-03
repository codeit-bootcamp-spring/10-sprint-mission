package com.sprint.mission.discodeit.dto.user.command;

import com.sprint.mission.discodeit.dto.user.UserServiceDTO.UserProfileImageDto;
import com.sprint.mission.discodeit.dto.user.UserServiceDTO.UserUniquenessDto;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import jakarta.annotation.Nullable;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Getter
public class UserCreateCommand {
    private final String username;
    private final String email;
    private final String password;
    private final UserProfileImageDto profile;

    public UserCreateCommand(UserCreateRequest request, @Nullable MultipartFile profile) {
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
