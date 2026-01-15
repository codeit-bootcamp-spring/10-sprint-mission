package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.FileIo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    public static final Path CHANNEL_DIRECTORY = Paths.get(System.getProperty("user.dir"), "data", "channels");

    private static ChannelService instance;
    private final FileIo<Channel> channelFileIo;
    private final FileIo<User> userFileIo;
    private final UserService userService;

    private FileChannelService() {
        channelFileIo = new FileIo<>(CHANNEL_DIRECTORY);
        this.channelFileIo.init();
        userFileIo = new FileIo<>(FileUserService.USER_DIRECTORY);
        this.userFileIo.init();

        userService = FileUserService.getInstance();
    }

    public static ChannelService getInstance() {
        if (instance == null) instance = new FileChannelService();
        return instance;
    }

    @Override
    public Channel create(String name) {
        Channel newChannel = new Channel(name);

        List<Channel> channels = channelFileIo.load();
        channels.add(newChannel);
        channelFileIo.save(newChannel.getId(), newChannel);

        return newChannel;
    }

    @Override
    public Channel findById(UUID channelId) {
        return channelFileIo.load().stream()
            .filter(ch -> ch.getId().equals(channelId))
            .findFirst()
            .orElseThrow(
                () -> new NoSuchElementException("id가 " + channelId + "인 채널을 찾을 수 없습니다.")
            );
    }

    @Override
    public List<Channel> findAllChannel() {
        return channelFileIo.load();
    }

    @Override
    public Channel updateName(UUID channelId, String name) {
        Channel updateChannel = this.findById(channelId);

        updateChannel.updateName(name);
        updateChannel.updateUpdatedAt(System.currentTimeMillis());
        channelFileIo.save(channelId, updateChannel);

        return updateChannel;
    }

    @Override
    public Channel addUser(UUID channelId, UUID userId) {
        Channel channel = this.findById(channelId);
        User user = userService.findById(userId);

        channel.addUser(user);
        channelFileIo.save(channelId, channel);
        userFileIo.save(userId, user);

        return channel;
    }

    @Override
    public boolean deleteUser(UUID channelId, UUID userId) {
        boolean result = false;
        Channel channel = this.findById(channelId);
        User user = userService.findById(userId);

        // 해당 채널에 유저가 속해있는지 확인한 후 내보낸다.
        // 이때 유저 쪽도 채널 정보를 제거해야 한다.
        if (this.isUserInvolved(channelId, userId)) {
            result = channel.getUserList().removeIf(u -> u.getId().equals(userId));
            result = result && user.getChannelList().removeIf(ch -> ch.getId().equals(channelId));

            channelFileIo.save(channelId, channel);
            userFileIo.save(userId, user);
        }

        return result;
    }

    @Override
    public void delete(UUID channelId) {
        try {
            channelFileIo.delete(channelId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public boolean isUserInvolved(UUID channelId, UUID userId) {
        Channel channel = this.findById(channelId);
        User user = userService.findById(userId);

        return channel.getUserList().contains(user);
    }
}
