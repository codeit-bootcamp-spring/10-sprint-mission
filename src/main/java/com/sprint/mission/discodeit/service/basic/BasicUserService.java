package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentRequestDto;
import com.sprint.mission.discodeit.dto.user.UserRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;
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
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public User create(UserRequestDto usercreateDto) {

        //username과 email은 다른 유저와 같으면 안된다.
        if(!userRepository.existsByUsername(usercreateDto.userName())){
            throw new IllegalArgumentException("이미 있는 이름입니다.");
        }
        if(!userRepository.existsByEmail(usercreateDto.email())){
            throw new IllegalArgumentException("이미 있는 이메일입니다.");
        }

        User user = new User(
                usercreateDto.userName(),
                usercreateDto.email(),
                usercreateDto.password()
        );

        //UserStatus를 같이 생성한다.
        UserStatus userStatus =  new UserStatus(user.getId());
        userStatusRepository.save(userStatus);

        //선택적으로 프로필 이미지를 같이 등록할 수 있다.
        if(usercreateDto.binaryContentDto() != null){

            BinaryContentRequestDto binaryContentDto = usercreateDto.binaryContentDto();

            //BinaryContent 생성&저장(프로필 등록)
            BinaryContent binaryContent = new BinaryContent(
                    user.getId(),
                    null,
                    binaryContentDto.data(),
                    binaryContentDto.contentType(),
                    binaryContentDto.fileName()
            );
            binaryContentRepository.save(binaryContent);
            user.setProfileId(binaryContent.getId());           //프로필로 지정한 이미지의 id를 user의 profileId로
        }

        return userRepository.save(user);
    }

    @Override
    public UserResponseDto find(UUID userId) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId).orElseThrow(() -> new NoSuchElementException("사용자가 없습니다."));
        User user = userRepository.findById(userId).orElseThrow(() -> new NoSuchElementException("사용자가 없습니다."));

        UserResponseDto userFind = new UserResponseDto(user.getId(),user.getUserName(),user.getEmail(),userStatus.CheckOnline());

        return userFind;
    }

    @Override
    public List<UserResponseDto> findAll() {

        List<User> users = userRepository.findAll();

        //user를 DTO로 변환
        return users.stream().map(user ->{
            UserStatus userStatus = userStatusRepository.findByUserId(user.getId()).orElse(null);
            boolean online =  userStatus != null && userStatus.CheckOnline();

            return new UserResponseDto(user.getId(),user.getUserName(),user.getEmail(),online);

        }).toList();

    }

    @Override
    public User update(UUID userId,UserRequestDto userUpdateDto) {

        //선택적으로 프로필 이미지를 할수도 있고 안할 수 도 있는거임.
        //BinaryContent 자체는 수정이 안됨.
        //user에서 profileId를 수정하는식으로 해야됨.

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User with id " + userId + " not found"));

        //유저의 이미지,이름,비밀번호 이런 모든걸 한번에 다 바꾼다.
        user.update(userUpdateDto.userName(), userUpdateDto.email(), userUpdateDto.password(),user.getProfileId());

        if (userUpdateDto.binaryContentDto() != null) {

            BinaryContentRequestDto dto = userUpdateDto.binaryContentDto();
            BinaryContent newProfile = new BinaryContent(
                    userId,//프로필 소유자 id
                    null,
                    dto.data(),
                    dto.contentType(),
                    dto.fileName()
            );
            binaryContentRepository.save(newProfile);

            user.setProfileId(newProfile.getId());
        }

        return userRepository.save(user);
    }

    @Override
    public void delete(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User with id " + userId + " not found");
        }
        //유저가 삭제되면, 해당 BinaryContent와 UserStatus가 삭제된다.
        User user = userRepository.findById(userId).orElse(null);
        binaryContentRepository.deleteById(user.getProfileId());            //user의 profileId로 삭제
        userStatusRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }
}
