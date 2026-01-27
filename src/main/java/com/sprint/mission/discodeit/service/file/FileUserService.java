package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    public FileUserService(UserRepository userRepository,
                           ChannelRepository channelRepository,
                           MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public User createUser(String nickname, String email) {
        // 유저 생성
        User user = new User(nickname, email);

        // 유저 저장
        return userRepository.saveUser(user);
    }

    @Override
    public User findUserById(UUID userId) {
        return userRepository.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User updateUserNickname(UUID userId, String newNickname) {
        // 수정 대상 유저가 존재하는지 검색 및 검증
        User user = userRepository.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));
        // 유저 닉네임 수정
        user.updateUserNickname(newNickname);

        // 수정된 유저 저장
        userRepository.saveUser(user);
        // 유저의 가입 채널과 작성 메시지에 수정 내용 반영
        user.getChannels().forEach(channelRepository::saveChannel);
        user.getMessages().forEach(messageRepository::saveMessage);

        return user;
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

        // 유저의 가입 채널 목록과 메시지 목록 조회
        List<Channel> channels = new ArrayList<>(user.getChannels());
        List<Message> messages = new ArrayList<>(user.getMessages());

        // 유저가 가입한 채널 탈퇴 처리
        for (Channel channel : channels) {
            channel.removeUser(user);
            channelRepository.saveChannel(channel);
        }
        // 유저가 작성한 메시지 삭제 처리
        for (Message message : messages) {
            message.removeFromChannelAndUser();
            messageRepository.deleteMessage(message.getId());
        }

        // 유저 삭제
        userRepository.deleteUser(user.getId());
    }
}
