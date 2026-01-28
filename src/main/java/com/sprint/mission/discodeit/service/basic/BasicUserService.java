package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepo;
    public BasicUserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public User registerUser(String name, String email, java.time.LocalDate birthDate, String phoneNumber, String password){
        List<User> user = userRepo.findAll();
        if(user.stream()
                .anyMatch(u -> u.getEmail().equals(email))) {
            throw new IllegalArgumentException("다른 유저가 사용 중인 이메일입니다");
        }

        User newUser = new User(name, email, birthDate, phoneNumber, password);
        userRepo.save(newUser);
        return newUser;
    }

    public User findUserById(UUID userId) {
        User user = userRepo.findById(userId);
        if(user == null){
            throw new IllegalArgumentException("해당 유저를 찾을 수 없습니다");
        }
        return user;
    }

    public List<User> findAllUser() {
        return userRepo.findAll();
    }

    public void deleteUser(UUID userId){
        User user = findUserById(userId);
        userRepo.delete(userId);
    }

    public int userCount () {
        return userRepo.findAll().size();
    }

    public User updateUser(UUID userId, String name, String email, String phoneNumber, String password ){
        User user = userRepo.findById(userId);

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

                    if (userRepo.findAll().stream()
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
        userRepo.save(user);
        return user;
    }
}
