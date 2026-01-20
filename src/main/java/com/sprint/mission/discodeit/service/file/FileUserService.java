package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService {


    private final Map<UUID, User> data;

    //역직렬화의 값을 data로
    public FileUserService(){
        this.data = load();
    }

    @Override
    public User create(String userName,String email, String status){
        User user = new User(userName,email,status);
        data.put(user.getId(),user);
        save();//생성된 객체가 추가되어 파일로 만들어진다.

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

        save();

        return user;
    }

    @Override
    public User delete(UUID id){
        User user = findById(id);
        data.remove(id);
        save();
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
        save();

    }

    //CREATE 객체 직렬화
    public void save(){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("user.ser"))){
            oos.writeObject(data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Map<UUID, User> load(){
        File file = new File("user.ser");

        //파일이 없을때 error 방지
        if (!file.exists()) {

            return new HashMap<>();
        }
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("user.ser"))){

            return (Map<UUID, User>) ois.readObject();

        }catch (Exception e){
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
