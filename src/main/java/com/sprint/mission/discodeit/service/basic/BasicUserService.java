package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserCreateDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
//    private final UserStatusRepository userStatusRepository;

    @Override
    public User create(UserCreateDto userDto) {

        //username과 email은 다른 유저와 같으면 안된다.
        if(!userRepository.existsByUsername(userDto.username())){
            throw new IllegalArgumentException("이미 있는 이름입니다.");
        }
        if(!userRepository.existsByEmail(userDto.email())){
            throw new IllegalArgumentException("이미 있는 이메일입니다.");
        }

        User user = new User(
                userDto.username(),
                userDto.email(),
                userDto.password()
        );

        //UserStatus를 같이 생성한다.
        UserStatus userStatus =  new UserStatus(user.getId());

        //userStatus 저장로직(필요)


        //선택적으로 프로필 이미지를 같이 등록할 수 있다.
        if(userDto.binaryContentDto() != null){

            BinaryContentDto binaryContentDto = userDto.binaryContentDto();

            //이미 프로필 존재하는지 검증(필요)

            //BinaryContent 생성&저장(프로필 등록)
            BinaryContent binaryContent = new BinaryContent(
                    user.getId(),
                    null,
                    binaryContentDto.data(),
                    binaryContentDto.contentType(),
                    binaryContentDto.fileName()
            );
            binaryContentRepository.save(binaryContent);


        }

        return userRepository.save(user);
    }

    @Override
    public User find(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID userId, String newUsername, String newEmail, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));
        user.update(newUsername, newEmail, newPassword);
        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        userRepository.deleteById(userId);
    }
}
