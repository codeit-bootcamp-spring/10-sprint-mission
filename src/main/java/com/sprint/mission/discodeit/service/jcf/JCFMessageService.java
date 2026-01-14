package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final HashMap<UUID, Message> data;

    public JCFMessageService() {
        this.data = new HashMap<>();
    }
    public JCFMessageService(Message message) {
        this.data = new HashMap<>();
        data.put(message.getId(), message);
    }

    @Override
    public Message create(String msg) {
        Message message = new Message(msg);
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> read(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public ArrayList<Message> readAll() {
        return new ArrayList<>(this.data.values());
    }

    @Override
    public Message update(UUID id, String messageData) {
        try {
            this.data.get(id).updateText(messageData);
            return this.data.get(id);
        } catch (NoSuchElementException e) {
            System.out.println("찾고자 하는 데이터가 없습니다.");
        } catch (Exception e) {
            System.out.println("잘못된 응답입니다.");
        }
        return null;
    }

    @Override
    public void delete(UUID id) {
        try {
            data.remove(id);
        } catch (NoSuchElementException e) {
            System.out.println("삭제할 데이터가 존재하지 않습니다.");
        } catch (Exception e) {
            System.out.println("데이터가 존재하지 않습니다.");
        }
    }
}
