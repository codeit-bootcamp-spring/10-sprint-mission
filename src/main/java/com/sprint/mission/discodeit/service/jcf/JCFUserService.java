package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data = new HashMap<>();

    // 유저 생성
    @Override
    public User create(String name, String nickname, String email, String password){
        User newUser = new User(name, nickname, email, password);
        data.put(newUser.getId(), newUser);
        return newUser;
    }

    // 유저 ID로 조회
    @Override
    public User findById(UUID id){
        User user = data.get(id);
        if (user == null){
            throw new NoSuchElementException("실패: 존재하지 않는 유저 ID입니다.");
        }
        return user;
    }

    // 유저 전부 조회
    @Override
    public List<User> findAll(){
        return new ArrayList<>(data.values());
    }

    // 유저 정보 수정 (이름, 닉네임, 이메일)
    @Override
    public User update(UUID id, String name, String nickname, String email){
        User user = findById(id);
        user.update(name, nickname, email);
        return user;
    }

    // 유저 상태만 따로 수정
    @Override
    public User updateStatus(UUID id, String status){
        User user = findById(id);
        user.updateStatus(status);
        return user;
    }

    // 비밀번호 변경
    @Override
    public User updatePassword(UUID id, String newPassword){
        User user = findById(id);
        user.updatePassword(newPassword);
        return user;
    }

    // 유저 삭제
    @Override
    public void delete(UUID id){
        findById(id);
        data.remove(id);
    }

    // 특정 유저가 참가한 채널 목록 조회
    @Override
    public List<Channel> findJoinedChannelsByUserId(UUID userId){
        User user = findById(userId);
        return user.getJoinedChannels();
    }

    // 특정 유저가 발행한 메시지 목록 조회
    @Override
    public List<Message> findMessagesByUserId(UUID userId){
        User user = findById(userId);
        return user.getMyMessages();
    }
}