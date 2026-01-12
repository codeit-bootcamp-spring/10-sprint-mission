package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileMessageService implements MessageService {

    private final File file;    // 클래스가 사용할 파일 저장소 객체 - 경로를 생성자에서 주입해 저장/불러오기 사용
    private final UserService userService;
    private final ChannelService channelService;

    // path : 파일이름 - 메인에서 저장 위치 지정
    public FileMessageService(String path, UserService userService, ChannelService channelService) {
        this.file = new File(path);
        this.userService = userService;
        this.channelService = channelService;
    }

    // 객체를 파일에 저장 - 직렬화
    private void save(Message message) {
        Map<UUID, Message> data = load();

        if(data.containsKey(message.getId())){
            Message existing = data.get(message.getId());
            existing.updateContent(message.getContent());
            existing.updateUpdatedAt(System.currentTimeMillis());
            data.put(existing.getId(), existing);
        }
        else{
            data.put(message.getId(), message);

        }
        writeToFile(data);
    }

    // 파일에서 로드(없으면 빈 Map) - 역직렬화
    @SuppressWarnings("unchecked")  // Object형을 Map으로 형변환할 때 뜨는 경고 억제
    private Map<UUID, Message> load() {
        if (!file.exists()) {
            return new HashMap<>(); // 파일 없으면 빈 저장소
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Message>) ois.readObject();   // 파일 읽고 변환, 바이트를 메모리 객체로 복원
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Message create(Message message) {
        if ((userService.read(message.getSender().getId())) == null) {
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }
        if ((channelService.read(message.getChannelId())) == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        save(message);
        return message;
    }

    @Override
    public Message read(UUID id) {
        Map<UUID, Message> data = load();
        return data.get(id);
    }

    @Override
    public List<Message> readAll() {
        return List.copyOf(load().values());    // 값들만 뽑아서 불변 리스트로 반환, 조회용이라 불변
    }

    @Override
    public Message update(Message message) {
        if (read(message.getId()) == null) {
            throw new IllegalArgumentException("존재하지 않는 메시지입니다.");
        }
        save(message);
        return message;
    }

    @Override
    public void delete(UUID id) {
        remove(id);
    }

    private void remove(UUID id) {
        Map<UUID, Message> data = load();
        if (data.containsKey(id)) {
            data.remove(id);
            writeToFile(data);
        }
    }

    @Override
    public void clear() {
        writeToFile(new HashMap<UUID, Message>());
    }

    public void writeToFile(Map<UUID, Message> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
