package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data;
//    private UserRepository userRepository;
//    private MessageRepository messageRepository;
    // 다른 레포지토리를 의존하면 안됨

    public JCFChannelRepository() {
        data = new HashMap<>();
    }

    @Override
    public Channel save(Channel channel) {
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

//    // 해당 user Id를 가진 유저가 속한 채널 목록을 반환
//    @Override
//    public List<Channel> getChannelsByUserId(UUID userId) {
//        return data.values().stream()
//                .filter(channel ->
//                        channel.getJoinedUsers().stream()
//                                .anyMatch(user -> user.getId().equals(userId)))
//                .toList();
//    }
    // 서비스 영역으로

//    @Override
//    public void setMessageRepository(MessageRepository messageRepository) {
//        this.messageRepository = messageRepository;
//    }
//
//    @Override
//    public void setUserRepository(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
    // 다른 레포지토리 의존 X

    // JCF 레포지토리에는 필요하지 않은 기능
//    @Override
//    public void loadData() {
//
//    }
//
//    @Override
//    public void saveData() {
//
//    }

}
