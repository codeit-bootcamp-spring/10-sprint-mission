package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    private final String FILE_PATH = "Channel.ser";

    private List<Channel> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (List<Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private void saveData(List<Channel> data) {
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Channel create(String name) {
        List<Channel> data = loadData();
        Channel channel = new Channel(name);
        data.add(channel);
        saveData(data);
        return channel;
    }

    @Override
    public Channel read(UUID id) {
        return loadData().stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .orElseThrow( () -> new NotFoundException("해당 ID의 채널을 찾을 수 없습니다"));
    }

    @Override
    public List<Channel> readAll() {
        return new ArrayList<>(loadData());
    }
    /**
     * 특정 유저가 참여 중인 채널 목록을 조회합니다.
     *
     * <p>
     * ⚠️ 주의:
     * 이 메서드는 {@code UserService}와의 순환 참조를 피하기 위해
     * 내부에서 유저 존재 여부를 검증하지 않습니다.
     * 따라서 {@code userId}가 실제로 존재하지 않더라도
     * 예외를 던지지 않고 빈 리스트를 반환할 수 있습니다.
     * </p>
     *
     * <p>
     * 📌 권장 사항:
     * 유저의 채널 목록을 조회하려는 경우,
     * 이 메서드를 직접 호출하기보다는
     * 상위 레이어(예: {@code DiscordService}, {@code ChatCoordinator})에서
     * 먼저 유저 존재 여부를 검증한 뒤 호출하거나,
     * 다음과 같은 대안을 사용하는 것을 권장합니다.
     * </p>
     *
     * <ul>
     *   <li>{@code UserService.find(userId)} 후 {@code user.getJoinedChannels()} 사용</li>
     *   <li>유저를 기준으로 채널을 관리하는 전용 조회 메서드</li>
     * </ul>
     *
     * <p>
     * 본 메서드는 채널 중심 조회가 필요한 내부 로직에서만
     * 제한적으로 사용하는 것을 의도합니다.
     * </p>
     *
     * @param userId 조회할 유저의 ID
     * @return 해당 유저가 참여 중인 채널 목록 (없을 경우 빈 리스트)
     */
    @Override
    public List<Channel> getChannelsByUser(UUID userId) {
        return loadData().stream()
                .filter(channel -> channel.getUserList().stream()
                        .anyMatch(user -> user.getId().equals(userId)))
                .toList();
    }

    @Override
    public Channel update(UUID id, String name) {
        List<Channel> data = loadData();
        Channel channel = data.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("채널 없음"));

        channel.update(name);
        saveData(data);
        return channel;
    }

    @Override
    public void delete(UUID id) {
        List<Channel> data = loadData();
        data.removeIf(channel -> channel.getId().equals(id));
        saveData(data);
    }

    @Override
    public void deleteUserInChannels(UUID userId) {
        List<Channel> data = loadData();
        data.forEach(channel ->
                        channel.getUserList().removeIf(user -> user.getId().equals(userId)));
        saveData(data);
    }
}

