package com.sprint.mission.discodeit.service.basic;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    public BasicUserService(UserRepository userRepository, ChannelRepository channelRepository,
                            MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public User createUser(String username, String password, String email){
        User newUser = new User(username, password, email);
        userRepository.save(newUser);
        System.out.println(username + "님 회원가입 완료되었습니다");
        return newUser;
    }

    @Override
    public User findUserById(UUID id) {
        return userRepository.findById(id)
                             .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다"));
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUserInfo(UUID id, String newUsername, String newEmail) {
        User targetUser = findUserById(id);

        if (newUsername == null && newEmail == null) {
            throw new IllegalArgumentException("둘 중 하나는 입력해야 합니다.");
        }

        Optional.ofNullable(newUsername)
                .filter(name -> name.contains(" "))
                .ifPresent(name -> {
                    throw new IllegalArgumentException("띄어쓰기는 포함할 수 없습니다.");
                });

        Optional.ofNullable(newEmail)
                .filter(email -> !isValidEmail(email))
                .ifPresent(email -> {
                    throw new IllegalArgumentException("이메일 형식이 잘못되었습니다.");
                });

        // 실제 수정 로직
        Optional.ofNullable(newUsername).ifPresent(name -> {
            targetUser.updateUsername(name);
            System.out.println("이름이 변경되었습니다: " + targetUser.getUsername());
        });

        Optional.ofNullable(newEmail).ifPresent(email -> {
            targetUser.updateEmail(email);
            System.out.println("이메일이 변경되었습니다: " + targetUser.getEmail());
        });

        userRepository.save(targetUser);
        targetUser.getMyChannels().stream()
                  .map(channel -> channelRepository.findById(channel.getId())) // ID로 최신 채널 조회
                  .flatMap(Optional::stream)   // 존재하는 채널만 필터링
                  .forEach(realChannel -> {
                      // 작성자 정보 갱신
                      realChannel.getParticipants().replaceAll(p -> p.equals(targetUser) ? targetUser : p);
                      channelRepository.save(realChannel);        // 채널 저장
                  });

        // 작성자 정보 갱신
        messageRepository.findByUserId(id).forEach(msg -> {
            msg.setUser(targetUser);  // 작성자 교체
            messageRepository.save(msg);
        });

        System.out.println("유저 정보 수정 및 연관 데이터 동기화 완료");

        return targetUser;
    }

    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

    @Override
    public User changePassword(UUID id, String newPassword) {
        User targetUser = findUserById(id);
        targetUser.updatePassword(newPassword);

        userRepository.save(targetUser);

        return targetUser;
    }

    @Override
    public void deleteUser(UUID id) {
        User targetUser = findUserById(id);

        for (Channel channel : targetUser.getMyChannels()) {
            channel.getParticipants().remove(targetUser);
            channelRepository.save(channel);
        }
        userRepository.delete(id);

        System.out.println(targetUser.getUsername() + "님 삭제 완료되었습니다");
    }

    @Override
    public List<User> findParticipants(UUID channelID) {
        return userRepository.findByChannelId(channelID);
    }
}
