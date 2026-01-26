package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private final String FILE_PATH = "Message.ser";
    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService){
        this.userService = userService;
        this.channelService = channelService;
    }

    private List<Message> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (List<Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void saveData(List<Message> data) {
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Message create(UUID channelId, UUID senderId, String text) {
        List<Message> data = loadData();

        Message message = new Message(channelService.read(channelId), userService.read(senderId), text);
        data.add(message);

        saveData(data);
        return message;
    }

    @Override
    public Message read(UUID id) {
        return loadData().stream()
                .filter(message -> message.getId().equals(id))
                .findFirst()
                .orElseThrow( () -> new NotFoundException("해당 ID의 메시지를 찾을 수 없습니다"));
    }

    @Override
    public List<Message> readAll() {
        return new ArrayList<>(loadData());
    }

    @Override
    public List<Message> getMessagesByUser(UUID userId) {
        userService.read(userId);
        return loadData().stream()
                .filter(message -> message.getSender().getId().equals(userId))
                .toList();
    }

    @Override
    public List<Message> getMessagesByChannel(UUID channelId) {
        channelService.read(channelId);
        return loadData().stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .toList();
    }

    @Override
    public Message update(UUID id, String text) {
        List<Message> data = loadData();
        Message message = data.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("해당 ID의 메시지를 찾을 수 없습니다"));

        message.update(text);
        saveData(data);
        return message;
    }

    @Override
    public void delete(UUID id) {
        List<Message> data = loadData();
        boolean removed = data.removeIf(message -> message.getId().equals(id));
        if (removed) { saveData(data); }
    }

    @Override
    public void deleteMessageByUserId(UUID userId) {
        List<Message> data = loadData();
        data.removeIf(message -> message.getSender().getId().equals(userId));
        saveData(data);
    }

    @Override
    public void deleteMessageByChannelId(UUID channelId) {
        List<Message> data = loadData();
        data.removeIf(message -> message.getSender().getId().equals(channelId));
        saveData(data);
    }
}
