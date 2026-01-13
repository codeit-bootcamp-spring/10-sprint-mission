package org.example.service.jcf;

import org.example.entity.Channel;
import org.example.entity.Status;
import org.example.entity.User;
import org.example.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public User create(String username, String email, String password, String nickname) {
        User user = new User(username, email, password, nickname);
        data.put(user.getId(),user);


        return user;
    }

    @Override
    public User findById(UUID id) {
//        if (data.get(id)== null) { 이렇게 작성하면 결국 get으로 데이터 접근을 두번해야 함. 이는 오히려 더 자원소모
//            throw new NoSuchElementException("User not found with id: " + id);
//        }
//        return data.get(id);
        User user = data.get(id);
        if (user == null) {
            throw new NoSuchElementException("User not found with id: " + id);
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User updateProfile(UUID id, String username, String email, String nickname) {
        User user = findById(id); // findById를 재활용함으로서 예외 메시지나 정책 바꿀 때 유지보수성을 높임
        user.setUsername(username);
        user.setEmail(email);
        user.setNickname(nickname);
        return user;
    }

    @Override
    public void changePassword(UUID id, String newPassword) {
        User user = findById(id);
        user.setPassword(newPassword);
    }

    @Override
    public void changeStatus(UUID id, Status status) {
        User user = findById(id);
        user.setStatus(status);
    }


    @Override
    public void delete(UUID id) { //  상태변화의 delete를 의미해야 하나, DB에서의 제거를 의미해야 하나.
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("User not found with id: " + id);
        }
        data.remove(id);
    }


    @Override
    public List<Channel> findChannelByUser(UUID userId) { //채널 추가/ 삭제 로직 생긴 후 다시보기 + 예외처리 메시지 구성 한번 보기
        User user = data.get(userId);
        if(user == null){
            return List.of();
        }
        return user.getChannels();
    }
}
