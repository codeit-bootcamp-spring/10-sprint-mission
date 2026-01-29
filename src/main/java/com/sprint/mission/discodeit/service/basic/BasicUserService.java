package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;

    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(String name) {
        User user = new User(name);
        this.userRepository.save(user);
        return user;
    }

    @Override
    public User read(UUID id) {
        for (User user : this.userRepository.loadAll()) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    public List<User> readAll() {
        return this.userRepository.loadAll();
    }

    @Override
    public User updateUserName(UUID id, String name) {
        this.userRepository.updateUserName(id, name);
        return this.read(id);
    }

    @Override
    public void delete(UUID id) {
        this.userRepository.delete(id);
    }

    // 특정 채널의 참가한 유저 목록 조회
    public List<User> readChannelUserList(UUID channelId, BasicChannelService channelService) {
        Channel channel = channelService.read(channelId);
        return channel.getUserList();
    }
}
