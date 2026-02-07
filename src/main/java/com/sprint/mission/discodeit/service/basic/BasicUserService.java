package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontentdto.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.userdto.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.userdto.UserResponseDTO;
import com.sprint.mission.discodeit.dto.userdto.UserUpdateDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.mapper.UserDTOMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository; // 아직 인터페이스 구현체가 없어서 bean을 못찾음.
    private final UserStatusRepository userStatusRepository; // 이하 동문
    private final UserDTOMapper userDTOMapper;

    // 유저 생성 요청 DTO를 받아 유저 도메인 객체를 생성하고, 해당 객체 정보를 바탕으로 UserResponseDTO를 만들어 반환한다.
    @Override
    public UserResponseDTO create(UserCreateRequestDTO req) {

        // 유저 레포지토리 내에서 유저 요청으로 들어온 이름과 이메일이 중복되는지 확인하고 중복 시 예외를 던진다.
        if(userRepository.findAll()
                .stream()
                .anyMatch(
                        u -> req.userName().equals(u.getUsername()) ||
                                req.email().equals(u.getEmail()))){
            throw new IllegalStateException("이미 존재하는 이름 or 이메일입니다.");
        }

        // 유저 도메인 객체 생성 시 들어갈 profileID는 null로 초기화한다.
        UUID profileId = null;

        // 유저 생성 요청 DTO에 binaryContent(첨부 파일)에 대한 정보가 들어가있다면...
        if(req.binaryContentDTO() != null){
            // 해당 첨부 파일 정보를 바탕으로 BinaryContent 객체를 생성한다.
            BinaryContent binaryContent = new BinaryContent(req.binaryContentDTO().contentType(), req.binaryContentDTO().file());
            profileId = binaryContent.getId(); // null로 초기화했던 profileID를 해당 객체의 ID로 초기화한다.
            binaryContentRepository.save(binaryContent); // binaryContent 레포지토리를 통해 해당 객체를 영속화한다.
        }

        User user = userDTOMapper.regtoUser(req, profileId); // DTO -> Entity를 통해 생성 요청 DTO에서 유저 객체를 생성한다.
        User savedUser = userRepository.save(user); // user 레포지토리의 save로 해당 유저 객체를 영속화한다.
        UserStatus savedUserStatus = userStatusRepository.save(new UserStatus(savedUser.getId())); // UserStatus 객체를 새로 만들고 레포의 save를 통해 영속화
        return userDTOMapper.userToResponse(savedUser, savedUserStatus); // entities -> DTO
    }

    // 유저 ID로 해당 유저가 레포내에 존재하는지 찾고 UserResponseDTO를 반환하는 메소드
    @Override
    public UserResponseDTO find(UUID userId) {
        // 유저 레포에 해당 유저가 존재하는지 확인하고 미존재시 예외 던짐.
        User user = userRepository.findById(userId)
                .orElseThrow(
                        () -> new IllegalStateException("유저 ID가 존재하지 않습니다.")
                );

        // 유저 상태 레포에서 find
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 User Status 객체는 존재하지 않습니다!"));

        return userDTOMapper.userToResponse(user, userStatus); // entities -> DTO
    }


    // 유저 레포지토리 내에 있는 모든 유저를 찾고 정보를 추출해서 UserResponseDTO를 반환하는 메소드
    @Override
    public List<UserResponseDTO> findAll() {
        return userRepository
                .findAll()
                .stream() // 유저 레포에서 모든 유저를 찾고 리스트를 stream화
                // map으로 각 원소(user)를 UserResponseDTO로 변환함.
                .map(u -> {
                    return userDTOMapper.userToResponse(u, userStatusRepository.findByUserId(u.getId()).orElseThrow(() -> new NoSuchElementException("해당 UserStatus가 존재하지 않습니다!")));
                }).toList(); // List<> 형식의 반환값이 필요하므로 List화하여 리턴.
    }

    // 유저 업데이트 DTO를 받아 해당 객체를 업데이트하고 UserResponseDTO를 반환하는 메소드
    @Override
    public UserResponseDTO update(UserUpdateDTO req) {
        // 유저 레포지토리에서 요청에 담긴 userID를 통해 유저를 찾고 없으면 예외를 던짐
        User user = userRepository.findById(req.id())
                .orElseThrow(
                        () -> new NoSuchElementException("User with id " + req.id() + " not found")
                );

        // 요청에 담긴 유저 이름이나 이메일이 이미 레포지토리 내에 존재할 경우 예외를 던짐.
//        if(userRepository
//                .findAll()
//                .stream()
//                // 유저 ID는 다르지만 (다른 유저지만), 변경하고자 하는 아이디나 이메일이 이미 레포지토리 내에 존재 (중복) 할 시 예외 던짐.
//                .anyMatch(u -> !u.getId().equals(req.id()) && (req.name().equals(u.getUsername()) || req.email().equals(u.getEmail())))){
//            throw new IllegalStateException("중복되는 이름 or 이메일입니다.");
//        }

        // 유저 업데이트에 쓰일 newProfileID (새로운 첨부파일 ID)는 null로 초기화 해줌.
        UUID newProfileId = null;
        // 만약 업데이트 요청 DTO에 newProfile (BinaryContentDTO)가 존재한다면...
        if(req.newProfile() != null){
            // 해당 BinaryContentDTO를 통해 binaryContent를 생성

            BinaryContentDTO binaryContentDTO = req.newProfile();
            BinaryContent binaryContent = new BinaryContent(
                    binaryContentDTO.contentType(),
                    binaryContentDTO.file()
            );

            BinaryContent saved = binaryContentRepository.save(binaryContent); // 해당 BinaryContent를 영속화함.
            newProfileId = saved.getId(); // newProfileId에 영속화된 객체의 id를 담음.
        }

        // 유저 도메인 객체의 update 메소드를 통해 업데이트.
        user.update(req.name(),
                req.email(),
                req.password(),
                newProfileId);

        User savedUser = userRepository.save(user); // 영속화
        userStatusRepository.save(new UserStatus(savedUser.getId()));
        return userDTOMapper.userToResponse(savedUser,
                userStatusRepository.findByUserId(savedUser.getId()).orElseThrow(() -> new NoSuchElementException("해당 UserStatus가 존재하지 않습니다!"))); // Entities -> DTO 후 리
    }

    // 지우고자 하는 유저를 지우면서 관련된 객체(UserStatus)와 정보(채널 가입 여부 및 메시지)도 같이 삭제하는 메소드
    @Override
    public void delete(UUID userId) {
        // 해당 유저 ID가 레포지토리 내에 존재하는지 확인하고 없으면 예외 던짐.
        User user = userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new IllegalStateException("존재하지 않는 id입니다.")
                );

        // 유저 생성 시 쓰였던 profileId를 추출하고, profileId가 null이면(첨부파일 없음) 아무 행동 안함.
        // profileId가 null이 아니면 첨부 파일이 존재하므로 해당 첨부파일 삭제.
        UUID profileID = user.getProfileID();
        if(profileID != null){
            binaryContentRepository.deleteByID(profileID); // 삭제 후 영속화.
        }

        // 해당 유저의 상태 객체도 삭제.
        userStatusRepository.deleteByUserId(userId);

        // 최종적으로 해당 유저의 객체까지 삭제한다.
        userRepository.deleteById(userId);
    }
}
