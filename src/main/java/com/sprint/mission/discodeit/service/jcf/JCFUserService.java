package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> data;

    public JCFUserService(){
        this.data = new HashMap<>();
    }

    @Override
    public User create(String userName,String email, String status){
        User user = new User(userName,email,status);
        data.put(user.getId(),user);

        return user;
    }

    @Override
    public User findById(UUID id){
        if(data.get(id) == null){
            throw new IllegalArgumentException("해당 id의 유저가 없습니다.");
        }
        return data.get(id);
    }

    @Override
    public List<User> findAll(){
        return new ArrayList<>(data.values());
    }


    @Override
    public User update(UUID userId,String userName,String email,String status){
        //Optional은 값이 없을수도 있다의 의미라서 userId는 Optional을 쓰지않는다.
        if (userId == null) {
            throw new IllegalArgumentException("수정할 유저ID를 입력해주세요");
        }
        User user = findById(userId);
        Optional.ofNullable(userName).ifPresent(user :: setUserName);//userName값이 있으면 setter
        Optional.ofNullable(email).ifPresent(user :: setEmail);
        Optional.ofNullable(status).ifPresent(user :: setStatus);

        return user;
    }

    @Override
    public void save() {

    }

    @Override
    public User delete(UUID id){
        User user = findById(id);
        data.remove(id);
        return user;
    }

    public void removeChannel(UUID channelId){
        if(channelId == null){
            throw new IllegalArgumentException("삭제하려는 채널이 없습니다.");
        }

        data.values().stream()
                .forEach(user ->
                        user.getChannelList()
                                .removeIf(Channel ->
                                        Channel.getId().equals(channelId)));

        data.values().stream()
                .forEach(user ->
                        user.getMessageList()
                                .removeIf(message ->
                                        message.getChannel().getId().equals(channelId)
                                )
                );

    }

}