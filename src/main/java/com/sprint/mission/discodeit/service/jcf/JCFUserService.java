package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    public List<User> data = new ArrayList<>();

    @Override
    public User createUser(String name, String email, String userId) {
        Objects.requireNonNull(name, "유효하지 않은 이름입니다.");
        Objects.requireNonNull(email, "유효하지 않은 이메일입니다.");
        Objects.requireNonNull(userId, "유효하지 않은 ID입니다.");

        if(data.stream()
                .anyMatch(u -> userId.equals(u.getUserId()))){
            throw new IllegalStateException("해당 ID는 이미 존재합니다.");
        }

        User user = new User(name, email, userId);
        data.add(user);
        return user;
    }

    @Override
    public User readUser(String userId) {


        User user = data.stream()
                .filter(u -> userId.equals(u.getUserId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("해당 유저는 존재하지 않습니다."));

        System.out.println(user);
        return user;
    }

    public User readUser(UUID id){
        Objects.requireNonNull(id, "유효하지 않은 식별자입니다.");
        User user = data.stream()
                .filter(u -> id.equals(u.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("해당 유저는 존재하지 않습니다."));
        System.out.println(user);

        return user;
    }



    @Override
    public User updateUser(UUID uuid, String userId, String name, String email) {
        Objects.requireNonNull(uuid, "유효하지 않은 유저 아이디입니다.");

        if(data.stream()
                .anyMatch( u -> userId.equals(u.getUserId()))){
            throw new IllegalAccessError("해당 유저 ID가 중복됩니다.");
        }

        User user = data.stream()
                .filter(u -> uuid.equals(u.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("해당 유저는 존재하지 않습니다."));

        System.out.println(user);

        Optional.ofNullable(userId)
                .ifPresent(user::updateUserId);
        Optional.ofNullable(name)
                .ifPresent(user::updateName);
        Optional.ofNullable(email)
                .ifPresent(user::updateEmail);

        return user;
    }

    

    @Override
    public User deleteUser(UUID uuid) {

        Objects.requireNonNull(uuid, "유효하지 않은 식별자입니다.");

        User user = data.stream()
                .filter(u -> uuid.equals(u.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("해당 유저는 존재하지 않습니다."));

        data.remove(user);
        return user;
    }


    @Override
    public ArrayList<User> readAllUsers() {
        System.out.println("-".repeat(20) + " 전체 조회 " + "-".repeat(20) );
        data.forEach(System.out::println);
        System.out.println("-".repeat(50) );
        return (ArrayList<User>) data;

    }


}