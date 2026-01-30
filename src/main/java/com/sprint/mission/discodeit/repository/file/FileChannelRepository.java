package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.AlreadyJoinedChannelException;
import com.sprint.mission.discodeit.exception.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.exception.UserNotInChannelException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class FileChannelRepository implements ChannelRepository {

    private static final String FILE_PATH = "channels.dat";
    private final UserService userService;
    private final UserRepository userRepository;

    // 파일에서 Map 로드
    private Map<UUID, Channel> loadChannelFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new LinkedHashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 파일에 Map 저장
    private void saveChannelFile(Map<UUID, Channel> channels) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void userAddChannel(UUID channelId, UUID userId) {
        Map<UUID, Channel> channels = loadChannelFile();
        Channel channel = channels.get(channelId);
        if (channel == null) throw new ChannelNotFoundException();


        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        if (channel.getChannelUsers().contains(user)) throw new AlreadyJoinedChannelException();

        channel.getChannelUsers().add(user);
        saveChannelFile(channels);
    }

    // 초기화
    public void resetChannelFile() {
        saveChannelFile(new LinkedHashMap<>());
    }

    @Override public Channel createChannel(Channel channel) {
        Map<UUID, Channel> channels = loadChannelFile();

        // 맵에 저장 후 파일에 저장
        channels.put(channel.getId(), channel);
        saveChannelFile(channels);

        return channel;
    }

    @Override
    public Channel findChannel(UUID channelId) {
        Channel channel = loadChannelFile().get(channelId);
        if (channel == null) throw new ChannelNotFoundException();
        return channel;
    }

    @Override
    public List<Channel> findAllChannel() {
        return new ArrayList<>(loadChannelFile().values());
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Map<UUID, Channel> channels = loadChannelFile();
        if (channels.remove(channelId) == null) throw new ChannelNotFoundException();
        saveChannelFile(channels);
    }


    @Override
    public boolean existsByNameChannel(String channelName) {
        return loadChannelFile().values().stream()
                .anyMatch(c -> c.getChannelName().equals(channelName));
    }


    @Override
    public Channel findByUserId(UUID userId) {
        userService.find(userId);
        return loadChannelFile().values().stream()
                .filter(channel -> channel.getChannelUsers().stream()
                        .anyMatch(user -> user.getId().equals(userId)))
                .findFirst()
                .orElseThrow(ChannelNotFoundException::new);
    }

    @Override
    public String findAllUserInChannel(UUID channelId) {
        Channel channel = findChannel(channelId);
        List<User> users = channel.getChannelUsers();
        if (users.isEmpty()) throw new UserNotInChannelException();
        StringBuilder sb = new StringBuilder();
        for (User user : users) {
            sb.append(user.getName()).append(", ");
        }
        return sb.substring(0, sb.length() - 2); // 마지막 ", " 제거
    }
}
