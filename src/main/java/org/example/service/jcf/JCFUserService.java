package org.example.service.jcf;

import org.example.entity.Channel;
import org.example.entity.Status;
import org.example.entity.User;
import org.example.exception.NotFoundException;
import org.example.service.MessageService;
import org.example.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> data;
    private MessageService messageService;  // 추가

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    // Setter 추가 (순환 의존성 해결용)
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public User create(String username, String email, String password, String nickname) {
        User user = new User(username, email, password, nickname);
        data.put(user.getId(),user);
        return user;
    }

    @Override
    public User findById(UUID userId) {
        return Optional.ofNullable(data.get(userId))
                .orElseThrow(() -> new NotFoundException("id", "존재하는 유저", userId));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }


    @Override
    public User update(UUID userId, String username, String email, String nickname, String password, Status status) {
        User user = findById(userId);

        // null이 아닌 값만 업데이트
        Optional.ofNullable(username).ifPresent(user::updateUsername);
        Optional.ofNullable(email).ifPresent(user::updateEmail);
        Optional.ofNullable(nickname).ifPresent(user::updateNickname);
        Optional.ofNullable(password).ifPresent(user::updatePassword);
        Optional.ofNullable(status).ifPresent(user::updateStatus);

        return user;
    }

    /*@Override optional로 개선
    public User updateProfile(UUID userId, String username, String email, String nickname) {
        User user = findById(userId);
        user.setUsername(username);
        user.setEmail(email);
        user.setNickname(nickname);
        return user;
    }

    @Override
    public void changePassword(UUID userId, String newPassword) {
        User user = findById(userId);
        user.setPassword(newPassword);
    }

    @Override
    public void changeStatus(UUID userId, Status status) {
        User user = findById(userId);
        user.setStatus(status);
    }*/

    // 탈퇴 (Soft Delete) - 상태값 변경
    @Override
    public void softDelete(UUID userId) {
        User user = findById(userId);
        user.updateStatus(Status.DELETED);
    }

    // 해당 유저 채널에서 제거 후 메시지 데이터 삭제
    @Override
    public void hardDelete(UUID userId) {

        User user = findById(userId);
        // 채널에서 유저 삭제
        user.getChannels().forEach(channel -> channel.getMembers().remove(user));
    //  for (Channel channel : new ArrayList<>(user.getChannels())) { channel.getMembers().remove(user); } 코드 개선

        // 메시지 완전 삭제
        new ArrayList<>(user.getMessages()).forEach(message ->
                messageService.hardDelete(message.getId())
        );

        // 3. data 제거
        data.remove(userId);

    }

    @Override
    public List<Channel> findChannelByUser(UUID userId) {
        return Optional.ofNullable(data.get(userId))
                .map(User::getChannels)
                .orElse(List.of());
        /*User user = data.get(userId); 코드 개선
        if(user == null){
            return List.of();
        }
        return user.getChannels();*/
    }
}
