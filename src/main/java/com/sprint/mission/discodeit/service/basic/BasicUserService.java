package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    public BasicUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public User signUp(String userName, String email, String password) {
        validateEmail(email);
        return userRepository.save(new User(userName, email, password));
    }

    @Override
    public User signIn(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("유효하지 않은 이메일: "+email));
        if(!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("유효하지 않은 비밀번호");
        }
        return user;
    }

    @Override
    public User updateInfo(UUID id, String userName, String email, String password) {
        User user = findUserById(id);
        //요청한 값이 널 또는 이전과 같은 값들로만 구성된 경우
        boolean unChanged = (userName ==null || userName.equals(user.getUserName()))
                && (email ==null || email.equals(user.getEmail()))
                && (password ==null || password.equals(user.getPassword()));
        if(unChanged){
            throw new IllegalArgumentException("변경사항 없음");
        }
        Optional.ofNullable(userName)
                .filter(n -> !n.equals(user.getUserName()))
                .ifPresent(n->user.setUserName(n));
        Optional.ofNullable(email)
                .filter(n -> !n.equals(user.getEmail()))
                .ifPresent(e ->{
                    validateEmail(email);
                    user.setEmail(e);
                });
        Optional.ofNullable(password)
                .filter(n -> !n.equals(user.getPassword()))
                .ifPresent(p -> user.setPassword(p));
        userRepository.update(user);
        return user;
    }

    @Override
    public User findUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("유효하지 않은 사용자"));
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void removeUserById(UUID id) {
        User user = findUserById(id);
        user.removeAllChannels();
        user.setUserName("[삭제된 사용자]");
        //소유 채널 삭제 선택사항...
        userRepository.deleteById(id);
    }
    private void validateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일: "+email);
        }
    }
}
