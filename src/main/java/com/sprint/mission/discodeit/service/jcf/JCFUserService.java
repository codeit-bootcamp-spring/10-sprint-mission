package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    public JCFUserService(UserRepository userRepository,
                          MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public User createUser(String nickname, String email) {
        // 새로운 유저 생성
        User user = new User(nickname, email);
        // 유저 저장소에 저장
        return userRepository.saveUser(user);
    }

    @Override
    public User findUserById(UUID userId) {
        // 존재하는 유저인지 검색 및 검증, 존재하지 않으면 예외 발생
        return userRepository.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));
    }

    @Override
    public List<User> findAll() {
        // 전체 유저 목록 반환
        return userRepository.findAll();
    }

    @Override
    public User updateUserNickname(UUID userId, String newNickname) {
        // 수정 대상 유저가 존재하는지 검색 및 검증
        User user = findUserById(userId);
        // 유저 닉네임 수정
        return user.updateUserNickname(newNickname);
    }

    @Override
    public void deleteUser(UUID userId) {
        // 삭제 대상 유저가 존재하는지 검증
        User user = findUserById(userId);

        // 채널을 소유한 상태인지 확인, 채널을 소유한 상태에선 탈퇴 불가
        for (Channel channel : user.getChannels()) {
            if (channel.getOwner().equals(user)) {
                throw new RuntimeException("채널을 소유한 상태에서는 탈퇴를 할 수 없습니다.");
            }
        }

        // 유저가 작성한 메시지 수집
        List<Message> messages = new ArrayList<>(user.getMessages());
        // 메시지를 가지고 있는 채널과 유저의 메시지 목록에서 제거
        messages.forEach(Message::removeFromChannelAndUser);
        // 메시지 저장소에서 메시지 제거
        messages.forEach(m -> messageRepository.deleteMessage(m.getId()));
        // 유저가 가입한 모든 채널에서 탈퇴 처리
        List<Channel> channels = new ArrayList<>(user.getChannels());
        channels.forEach(ch -> ch.removeUser(user));

        // 관계를 정리한 후 유저 삭제, 저장소에서 제거
        userRepository.deleteUser(userId);
    }
}
