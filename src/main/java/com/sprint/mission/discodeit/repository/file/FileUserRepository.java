package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.file.FileUserService;

import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {

    private Map<UUID,User> data;

    public FileUserRepository(){
        this.data = load();
    }

    @Override
    public User save(User user) {
        this.data = load();
        data.put(user.getId(),user);
        persist();
        return user;
    }

    @Override
    public User findById(UUID userId) {
        this.data = load();
        User user = data.get(userId);
        return user;
    }

    @Override
    public List<User> findAll() {
        this.data = load();
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID userId) {
        this.data = load();
        data.remove(userId);
        persist();

    }

    //CREATE 객체 직렬화
    public void persist(){
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
