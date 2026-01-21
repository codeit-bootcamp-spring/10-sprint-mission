package org.example.service.basic;

import org.example.entity.Channel;
import org.example.entity.Status;
import org.example.entity.User;
import org.example.exception.NotFoundException;
import org.example.repository.ChannelRepository;
import org.example.repository.MessageRepository;
import org.example.repository.UserRepository;
import org.example.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;   // 추가
    private final MessageRepository messageRepository;   // 추가

    public BasicUserService(UserRepository userRepository,
                            ChannelRepository channelRepository,
                            MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public User create(String username, String email, String password, String nickname) {
        User user = new User(username, email, password, nickname);
        return userRepository.save(user);
    }

    @Override
    public User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("id", "존재하는 유저", userId));
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User update(UUID userId, String username, String email, String nickname, String password, Status status) {
        User user = findById(userId);

        Optional.ofNullable(username).ifPresent(user::updateUsername);
        Optional.ofNullable(email).ifPresent(user::updateEmail);
        Optional.ofNullable(nickname).ifPresent(user::updateNickname);
        Optional.ofNullable(password).ifPresent(user::updatePassword);
        Optional.ofNullable(status).ifPresent(user::updateStatus);

        return userRepository.save(user);
    }

    @Override
    public void softDelete(UUID userId) {
        User user = findById(userId);
        user.updateStatus(Status.DELETED);
        userRepository.save(user);
    }

    @Override
    public void hardDelete(UUID userId) {
        User user = findById(userId);

        // 채널에서 유저 삭제
        user.getChannels().forEach(channel -> {
            channel.getMembers().remove(user);
            channelRepository.save(channel);  //  추가
        });
        // 메시지 완전 삭제
        new ArrayList<>(user.getMessages()).forEach(message -> {
            Channel channel = message.getChannel();
            message.removeFromChannelAndUser();
            messageRepository.deleteById(message.getId());
            if (channel != null) {
                channelRepository.save(channel);  //  추가
            }
        });

        userRepository.deleteById(userId);
    }

    @Override
    public List<Channel> findChannelByUser(UUID userId) {
        return findById(userId).getChannels();
    }
}
