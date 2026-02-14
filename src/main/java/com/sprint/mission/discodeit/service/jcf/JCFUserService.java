//package com.sprint.mission.discodeit.service.jcf;
//
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//public class JCFUserService implements UserService {
//    private ChannelService channelService;
//    private MessageService messageService;
//    private final List<User> users =  new ArrayList<>();
//
//    public JCFUserService() {
//    }
//
//    public void setChannelService(ChannelService channelService) {
//        this.channelService = channelService;
//    }
//
//    public void setMessageService(MessageService messageService) {
//        this.messageService = messageService;
//    }
//
//    @Override
//    public User createPublicChannel(String userName, String userEmail) {
//        validateDuplicateEmail(userEmail);
//        User newUser = new User(userName, userEmail);
//        users.add(newUser);
//        return newUser;
//    }
//
//    @Override
//    public User findById(UUID uuid) {
//        return users.stream()
//                .filter(u -> u.getId().equals(uuid))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + uuid));
//    }
//
//    @Override
//    public List<User> findAll() {
//        return new ArrayList<>(users);
//    }
//
//    @Override
//    public List<User> findMembers(UUID channelId) {
//        return channelService.findById(channelId).getMemberIds();
//    }
//
//    @Override
//    public User update(UUID id, String userNickname) {
//        User user = findById(id);
//        user.updateNickname(userNickname);
//        return user;
//    }
//
//    @Override
//    public void delete(UUID authorId) {
//        User user = findById(authorId);
//        messageService.deleteMessagesByUserId(authorId);
//        new ArrayList<>(user.getChannels()).forEach(channel -> {
//            channel.removeMember(user);
//        });
//        users.remove(user);
//    }
//
//    // 이메일 중복 시 예외를 던져 가입 중단 (Fail-Fast)
//    private void validateDuplicateEmail(String userEmail) {
//
//        boolean exists = users.stream()
//                .anyMatch(existingUser ->
//                        existingUser.getUserEmail()
//                                .equals(userEmail));
//        if (exists) {
//            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + userEmail);
//        }
//    }
//}
