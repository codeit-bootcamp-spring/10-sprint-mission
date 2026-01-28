package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public Channel create(String name) {
        Channel channel = new Channel(name);
        save(channel);
        return channel;
    }

    @Override
    public Channel joinUsers(UUID channelId, UUID... userId) {
        Channel channel = findChannel(channelId);
        Arrays.stream(userId)
                .map(userRepository::findById)
                .forEach(user -> {
                    channel.addUsers(user.getId());
                    user.addChannel(channelId);
                    userRepository.save(user.getId(), user);
                });
        save(channel);
        return channel;
    }

    @Override
    public Channel findChannel(UUID channelId) {
        Channel channel = Optional.ofNullable(channelRepository.findById(channelId))
                .orElseThrow(()->new NoSuchElementException("해당 채널을 찾을 수 없습니다"));
        return channel;
    }

    @Override
    public List<Channel> findAllChannel() {
        List<Channel> channelList = channelRepository.findAll();

        System.out.println("[채널 전체 조회]");
        channelList.forEach(System.out::println);

        return channelList;
    }

    @Override
    public List<Channel> findAllChannelsByUserId(UUID userId) {
        User user = Optional.ofNullable(userRepository.findById(userId))
                        .orElseThrow(() -> new NoSuchElementException());

        System.out.println("[" + user + "가 속한 채널 조회]");
        List<Channel> channelList = user.getChannelList().stream()
                .map(channelRepository::findById).filter(Objects::nonNull).toList();

        channelList.forEach(System.out::println);

        return channelList;
    }

    @Override
    public Channel update(UUID channelId, String newName) {
        Channel channel = findChannel(channelId);
        // 채널 업데이트
        channel.updateChannel(newName);
        // 채널 갱신
        save(channel);

        return channel;
    }

    @Override
    public void delete(UUID channelId) {
        Channel channel = findChannel(channelId);

        // 채널이 삭제될때 이 채널이 속해있는 유저의 채널리스트에서 채널 삭제
        List<UUID> userList = new ArrayList<>(channel.getUserList());
        userList.stream().map(userRepository::findById)
                .filter(Objects::nonNull)
                .forEach(user -> {
                    user.getChannelList().remove(channelId);
                    userRepository.save(user.getId(), user);
                });

        // 채널이 삭제될때 채널에 속해있던 메시지들 전부 삭제
        List<UUID> messageList = new ArrayList<>(channel.getMessageList());
        messageList.stream().map(messageRepository::findById)
                .filter(Objects::nonNull).forEach(message ->{
                messageRepository.delete(message.getId());
                // 유저가 가지고 있던 메시지도 삭제
                User author = userRepository.findById(message.getUserId());
                if(author != null){
                    author.getMessageList().remove(message.getId());
                    // 정보 갱신
                    userRepository.save(author.getId(), author);
                }
                // 정보 갱신
                messageRepository.save(message.getId(), message);
        });

        channelRepository.delete(channelId);
    }

    @Override
    public void save(Channel channel) {
        channelRepository.save(channel.getId(),channel);
    }
}
