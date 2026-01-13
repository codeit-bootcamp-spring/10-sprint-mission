package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public User create(String name, UserStatus status) {
        boolean isDuplicate = data.values().stream()
                .anyMatch(user -> user.getName().equals(name));
        if (isDuplicate) {
            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
        }
        User user = new User(name, status);
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        validateExistence(data, id);
        return data.get(id);
    }

    @Override
    public List<User> readAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(UUID id) {
        validateExistence(data, id);
        User user = findById(id);
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public void printUserMessages(UUID id) {
        User user = findById(id);
        if(!user.getMessages().isEmpty()) {
            String allMessages = user.getMessages().stream()
                    .map(msg -> String.format("- [%s] %s",
                            msg.getChannel().getName(),
                            msg.getContent()))
                    .collect(Collectors.joining("\n"));
            System.out.println("[" + user.getName() + "님이 보낸 메시지 내역]\n" + allMessages);
        }
        else{
            System.out.println(user.getName() + "님이 보낸 메시지가 없습니다.");
        }
    }

    @Override
    public void delete(UUID id) {
        validateExistence(data, id);
        data.remove(id);
    }

    private void validateExistence(Map<UUID, User> data, UUID id){
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("실패 : 존재하지 않는 사용자 ID입니다.");
        }
    }
}
