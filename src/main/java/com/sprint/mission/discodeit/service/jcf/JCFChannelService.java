package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private static ChannelService instance;
    private final List<Channel> data;

    private final UserService userService;

    private JCFChannelService() {
        data = new ArrayList<Channel>();
        userService = JCFUserService.getInstance();
    }

    public static ChannelService getInstance() {
        if (instance == null) instance = new JCFChannelService();
        return instance;
    }

    //  [ ] data 필드를 활용해 생성, 조회, 수정, 삭제하는 메소드를 구현하세요.
    @Override
    public Channel create(String name) {
        Channel newChannel = new Channel(name);
        data.add(newChannel);
        return newChannel;
    }

    @Override
    public Channel findById(UUID channelId) {
        return data.stream()
            .filter(ch -> ch.getId().equals(channelId))
            .findFirst()
            .orElseThrow(
                () -> new NoSuchElementException(channelId + " 은(는) 존재하지 않는 채널 id입니다.")
            );
    }

    @Override
    public List<Channel> findAllChannel() {
        return data;
    }

    @Override
    public Channel updateName(UUID channelId, String name) {
        Channel channel = data.stream()
            .filter(ch -> ch.getId().equals(channelId))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("id가 " + channelId + "인 채널은 존재하지 않습니다."));

        channel.updateName(name);
        channel.updateUpdatedAt(System.currentTimeMillis());

        return channel;
    }

    @Override
    public Channel addUser(UUID channelId, UUID userId) {
        Channel channel = data.stream()
            .filter(ch -> ch.getId().equals(channelId))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("id가 " + channelId + "인 채널은 존재하지 않습니다."));

        User user = userService.findById(userId);

        channel.addUser(user);
        channel.updateUpdatedAt(System.currentTimeMillis());

        return channel;
    }

    @Override
    public boolean deleteUser(UUID channelId, UUID userId) {
        // 채널 객체를 찾는다.
        Channel channel = data.stream()
            .filter(ch -> ch.getId().equals(channelId))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("id가 " + channelId + "인 채널은 존재하지 않습니다."));

        // 해당 채널에 유저가 속해있는지 확인한 후 내보낸다.
        if (this.isUserInvolved(channelId, userId)) {
            return channel.getUserList().removeIf(u -> u.getId().equals(userId));
        }

        return false;
    }

    @Override
    public void delete(UUID channelId) {
        if (!data.removeIf(user -> user.getId().equals(channelId)))
            throw new NoSuchElementException("id가 " + channelId + "인 채널은 존재하지 않습니다.");
    }

    @Override
    public boolean isUserInvolved(UUID channelId, UUID userId) {
        // 채널과 유저 객체를 찾는다.
        Channel channel = data.stream()
            .filter(ch -> ch.getId().equals(channelId))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("id가 " + channelId + "인 채널은 존재하지 않습니다."));

        User user = userService.findById(userId);

        return channel.getUserList().contains(user);
    }
}
