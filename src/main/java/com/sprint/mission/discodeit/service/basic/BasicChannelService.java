package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class BasicChannelService implements ChannelService {
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

    public BasicChannelService(UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository) {
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    public Channel create(String name) {
        return channelRepository.save(new Channel(name));
    }

    @Override
    public Channel findById(UUID channelId) {
        return channelRepository.findById(channelId).orElseThrow(
            () -> new NoSuchElementException("id가 " + channelId + "인 채널을 찾을 수 없습니다.")
        );
    }

    @Override
    public List<Channel> findAllChannel() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateName(UUID channelId, String name) {
        Channel updateChannel = this.findById(channelId);

        updateChannel.updateName(name);
        channelRepository.save(updateChannel);

        return updateChannel;
    }

    @Override
    public Channel addUser(UUID channelId, UUID userId) {
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(
                () -> new NoSuchElementException("id가 " + channelId + "인 채널을 찾을 수 없습니다.")
            );
        User user = userRepository.findById(userId)
            .orElseThrow(
                () -> new NoSuchElementException("id가 " + userId + "인 유저를 찾을 수 없습니다.")
            );

        channel.addUser(user);

        channelRepository.save(channel);
        userRepository.save(user);

        return channel;
    }

    @Override
    public boolean deleteUser(UUID channelId, UUID userId) {
        // 채널 객체를 찾는다.
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(
                () -> new NoSuchElementException("id가 " + channelId + "인 채널을 찾을 수 없습니다.")
            );
        User user = userRepository.findById(userId)
            .orElseThrow(
                () -> new NoSuchElementException("id가 " + userId + "인 유저를 찾을 수 없습니다.")
            );

        // 해당 채널에 유저가 속해있는지 확인한 후 내보낸다.
        // 유저쪽도 참여한 채녈 목록에서 삭제한다.
        if (this.isUserInvolved(channelId, userId)) {
            channel.getUserList().remove(user);
            user.getChannelList().remove(channel);

            channelRepository.save(channel);
            userRepository.save(user);

            return true;
        }

        return false;
    }

    @Override
    public void delete(UUID channelId) {
        // 채널에 있던 유저 내보내고 유저의 메시지 정보도 삭제
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(
                () -> new NoSuchElementException("id가 " + channelId + "인 채널을 찾을 수 없습니다.")
            );
        channel.getUserList().stream()
            .forEach(
                user -> {
                    user.getChannelList().remove(channel);
                    user.getMessageList().removeIf(msg -> msg.getChannel().getId().equals(channelId));
                    userRepository.save(user);
                }
            );

        // 채널 삭제
        channelRepository.delete(channelId);
    }

    @Override
    public boolean isUserInvolved(UUID channelId, UUID userId) {
        // 채널과 유저 객체를 찾는다.
        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(
                () -> new NoSuchElementException("id가 " + channelId + "인 채널을 찾을 수 없습니다.")
            );
        User user = userRepository.findById(userId)
            .orElseThrow(
                () -> new NoSuchElementException("id가 " + userId + "인 유저를 찾을 수 없습니다.")
            );

        return channel.getUserList().contains(user);
    }
}
