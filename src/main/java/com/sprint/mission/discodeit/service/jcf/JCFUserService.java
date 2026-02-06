package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

//public class JCFUserService implements UserService {
//    private final Map<UUID, User> userMap;
//    public JCFUserService(){
//        this.userMap = new HashMap<>();
//    }
//
//    @Override
//    public User createUser(String username, String password, String email) {
//        User newUser = new User(username, password, email);
//        userMap.put(newUser.getId(), newUser);
//        System.out.println(username + "님 회원가입 완료되었습니다.");
//        return newUser;
//    }
//
//    @Override
//    public User findUserById(UUID id) {
//        User user = userMap.get(id);
//        if (user == null) {
//            throw new IllegalArgumentException("해당 유저가 없습니다.");
//        }
//        return user;
//    }
//
//    @Override
//    public List<User> findAllUsers(){
//        return new ArrayList<>(userMap.values());
//    }
//
//    @Override
//    public User updateUserInfo(UUID id, String newUsername, String newEmail) {
//        User targetUser = findUserById(id);
//
//        // username, email 검증
//        if (newUsername == null && newEmail == null) {
//            throw new IllegalArgumentException("둘 중 하나는 입력해야 합니다.");
//        }
//
//        // Username 검증
//        Optional.ofNullable(newUsername)
//                .filter(name -> name.contains(" "))
//                .ifPresent(name -> {
//                    throw new IllegalArgumentException("띄어쓰기는 포함할 수 없습니다.");
//                });
//
//        // Email 검증
//        Optional.ofNullable(newEmail)
//                .filter(email -> !isValidEmail(email))
//                .ifPresent(email -> {
//                    throw new IllegalArgumentException("이메일 형식이 잘못되었습니다.");
//                });
//
//        // 실제 수정 로직
//        Optional.ofNullable(newUsername).ifPresent(name -> {
//            targetUser.updateUsername(name);
//            System.out.println("이름이 변경되었습니다: " + targetUser.getUsername());
//        });
//
//        Optional.ofNullable(newEmail).ifPresent(email -> {
//            targetUser.updateEmail(email);
//            System.out.println("이메일이 변경되었습니다: " + targetUser.getEmail());
//        });
//
//        return targetUser;
//    }
//
//    private boolean isValidEmail(String email){
//        if (email == null || email.trim().isEmpty()){
//            return false;
//        }
//        // 이메일 정규표현식
//        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
//        return email.matches(emailRegex);
//    }
//
//    @Override
//    public void deleteUser(UUID id){
//        User targetUser = findUserById(id);
//
//        for (Channel channel : targetUser.getMyChannels()) {
//            channel.getParticipants().remove(targetUser);
//        }
//
//        userMap.remove(id);
//        System.out.println(targetUser.getUsername() + "님 삭제 완료되었습니다");
//    }
//
//    @Override
//    public User changePassword(UUID id, String newPassword) {
//        User targetUser = findUserById(id);
//        targetUser.updatePassword(newPassword);
//        return targetUser;
//    }
//
//    @Override
//    public List<User> findParticipants(UUID channelID){
//        return userMap.values().stream()
//                .filter(user -> user.getMyChannels().stream()
//                        .anyMatch(channel -> channel.getId().equals(channelID)))
//                .toList();
//    }
//
//}
