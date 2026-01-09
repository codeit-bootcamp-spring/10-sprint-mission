package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileMessageService implements MessageService {

    private final File file;    // 클래스가 사용할 파일 저장소 객체 - 경로를 생성자에서 주입해 저장/불러오기 사용
    private FileUserService userService;
    private FileChannelService channelService;

    // path : 파일이름 - 메인에서 저장 위치 지정
    public FileMessageService(String path, FileUserService userService, FileChannelService channelService) {
        this.file = new File(path);
        this.userService = userService;
        this.channelService = channelService;
    }

    // 객체를 파일에 저장 - 직렬화
    private void save(Map<UUID, Message> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);  // 객체 단위로 파일에 저장, 객체의 필드값 전체를 바이트로 변환해서 파일에 저장
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        Map<UUID, Message> data = load();
        data.put(message.getId(), message);
        save(data);
        return null;
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
        Map<UUID, Message> data = load();

        Message found = data.get(message.getId());
        if (found == null) {
            throw new IllegalArgumentException("존재하지 않는 메시지입니다.");
        }

        found.updateContent(message.getContent());
        found.updateUpdatedAt(System.currentTimeMillis());

        data.put(message.getId(), message);
        save(data);
        return message;
    }

    @Override
    public void delete(UUID id) {
        Map<UUID, Message> data = load();
        data.remove(id);
        save(data);
    }

    @Override
    public void clear() {
        save(new HashMap<>());
    }
}
