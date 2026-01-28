package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.time.LocalDate;
import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> userStore;

    public JCFUserService(){
        this.userStore = new HashMap<>();
    }

    public User registerUser(String name, String email, java.time.LocalDate birthDate, String phoneNumber, String password){
            if(userStore.values().stream()
                    .anyMatch(u -> u.getEmail().equals(email))) {
                throw new IllegalArgumentException("다른 유저가 사용 중인 이메일입니다");
            }

            User newUser = new User(name, email, birthDate, phoneNumber, password);
            userStore.put(newUser.getId(), newUser);
            return newUser;
        }
     public User findUserById(UUID userId) {
         User user = userStore.get(userId);
         if(user == null){
             throw new IllegalArgumentException("해당 유저를 찾을 수 없습니다");
         }

            return user;
        }

    @Override
    public List<User> findAllUser() {
        return new ArrayList<>(userStore.values());
    }

    public void deleteUser(UUID userId){
        User user = findUserById(userId);
        userStore.remove(userId);
        }

    public int userCount () {return userStore.size();}

    public User updateUser(UUID userId, String name, String email, String phoneNumber, String password ){
        User user = findUserById(userId);

        Optional.ofNullable(name)
                .ifPresent(n-> {
                    if(user.getName().equals(n)){
                        throw new IllegalArgumentException("현재 사용 중인 이름입니다");
                    }
                    user.setName(name);
                });

        Optional.ofNullable(email)
                .ifPresent(e -> {
                    if(user.getEmail().equals(e)){
                        throw new IllegalArgumentException("현재 사용 중인 이메일입니다");
                    }

                    if (userStore.values().stream()
                            .anyMatch(u -> !u.getId().equals(userId) && u.getEmail().equals(e))) {
                        throw new IllegalArgumentException("다른 유저가 사용 중인 이메일입니다");
                    }
                    user.setEmail(email);
                });

        Optional.ofNullable(phoneNumber)
                .ifPresent(p -> {
                    if(user.getPhoneNumber().equals(p)){
                        throw new IllegalArgumentException("현재 사용 중인 전화번호입니다");
                    }
                    user.setPhoneNumber(phoneNumber);
                });

        Optional.ofNullable(password)
                .ifPresent(p-> {
                    if(user.getPassword().equals(p)) {
                        throw new IllegalArgumentException("현재 사용 중인 비밀번호입니다");
                    }
                    user.setPassword(password);
                });

        return user;
    }

}
