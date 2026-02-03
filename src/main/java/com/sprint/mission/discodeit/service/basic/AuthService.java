package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.UserLoginClearDTO;
import com.sprint.mission.discodeit.dto.auth.UserLoginDTO;
import com.sprint.mission.discodeit.dto.user.UserDTO;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.NoSuchElementException;

public class AuthService {
    private UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserLoginClearDTO login(UserLoginDTO userLoginDTO) {
        for (User user : this.userRepository.loadAll()) {
            if (user.getName().equals(userLoginDTO.username()) && user.getPassword().equals(userLoginDTO.password())) {
                return new UserLoginClearDTO(
                        user.getId(),
                        user.getName(),
                        user.getEmail()
                );
            }
        }
        throw new NoSuchElementException();
    }
}
