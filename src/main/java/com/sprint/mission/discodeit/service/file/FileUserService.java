package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class FileUserService implements UserService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    public FileUserService(UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
    }

    // 유저 생성
    @Override
    public User create(String name, String nickname, String email, String password){
        User newUser = new User(name, nickname, email, password);
        return userRepository.save(newUser);
    }

    // 유저 ID로 조회
    @Override
    public User findById(UUID id){
        return userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저 ID입니다."));
    }

    // 유저 전부 조회
    @Override
    public List<User> findAll(){
        return userRepository.findAll();
    }

    // 유저 정보 수정
    @Override
    public User update(UUID id, String name, String nickname, String email, String status, String password) {
        User user = findById(id);

        Optional.ofNullable(name).ifPresent(user::updateName);
        Optional.ofNullable(nickname).ifPresent(user::updateNickname);
        Optional.ofNullable(email).ifPresent(user::updateEmail);
        Optional.ofNullable(status).ifPresent(user::updateStatus);
        Optional.ofNullable(password).ifPresent(user::updatePassword);

        userRepository.save(user);

        for (Channel channel : user.getJoinedChannels()) {
            channelRepository.save(channel);
        }

        for (Message message : user.getMyMessages()) {
            messageRepository.save(message);
        }

        return user;
    }

    // 유저 삭제
    @Override
    public void delete(UUID id){
        User user = findById(id);

        for (Channel channel : user.getJoinedChannels()) {
            channel.removeMember(user);
            channelRepository.save(channel);
        }

        for (Message message : user.getMyMessages()) {
            messageRepository.delete(message);
        }

        userRepository.delete(user);
    }

    // 특정 유저가 참가한 채널 목록 조회
    @Override
    public List<Channel> findJoinedChannelsByUserId(UUID userId){
        User user = findById(userId);
        return user.getJoinedChannels();
    }

    // 특정 유저가 발행한 메시지 목록 조회
    @Override
    public List<Message> findMessagesByUserId(UUID userId){
        User user = findById(userId);
        return user.getMyMessages();
    }
}
