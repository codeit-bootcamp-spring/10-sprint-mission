package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ClearMemory;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileMessageService extends AbstractFileService implements MessageService,  ClearMemory {

    private final UserService userService;
    private final ChannelService channelService;

    // path : 파일이름 - 메인에서 저장 위치 지정
    public FileMessageService(String path, UserService userService, ChannelService channelService) {
        super(path);
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
        if(!data.containsKey(id)){
            throw new NoSuchElementException("조회 실패 : 해당 ID의 메시지를 찾을 수 없습니다.");
        }
        return data.get(id);
    }

    @Override
    public List<Message> readAll() {
        Map<UUID,Message> data = load();
        return List.copyOf(data.values());    // 값들만 뽑아서 불변 리스트로 반환, 조회용이라 불변
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
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("삭제 실패 : 존재하지 않는 메시지 ID입니다.");
        }
        data.remove(id);
        writeToFile(data);
    }

    @Override
    public void clear() {
        writeToFile(new HashMap<UUID, Message>());
    }

}
