//package com.sprint.mission.discodeit.service.jcf;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.entity.StatusType;
//import com.sprint.mission.discodeit.repository.ChannelRepository;
//import com.sprint.mission.discodeit.repository.MessageRepository;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.util.*;
//
//public class JCFUserService implements UserService {
//    UserRepository userRepository;
//    ChannelRepository channelRepository;
//    MessageRepository messageRepository;
//
//    public JCFUserService(UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository) {
//        this.userRepository = userRepository;
//        this.channelRepository = channelRepository;
//        this.messageRepository = messageRepository;
//    }
//
//    @Override
//    public User create(String name, StatusType status) {
//        userRepository.findByName(name).ifPresent(u -> {
//            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
//        });
//        User newUser = new User(name, status);
//        return userRepository.save(newUser);
//    }
//
//    @Override
//    public User findById(UUID id) {
//        User user = userRepository.findById(id).orElseThrow(()
//                -> new NoSuchElementException("실패 : 존재하지 않는 사용자 ID입니다."));
//        return user;
//    }
//
//    @Override
//    public List<User> findAll() {
//        return userRepository.readAll();
//    }
//
//    @Override
//    public User update(UUID id, String newName, StatusType newStatus) {
//        User user = findById(id);   // 예외 검사
//
//        user.updateName(newName);
//        user.updateStatus(newStatus);
//
//        return userRepository.save(user);
//    }
//
//    @Override
//    public List<Message> getUserMessages(UUID id) {
//        User user = findById(id);
//        return user.getMessages();
//    }
//
//    @Override
//    public List<Channel> getUserChannels(UUID id) {
//        User user = findById(id);
//        return user.getChannels();
//    }
//
//
//    @Override
//    public void delete(UUID id) {
//        User user = findById(id);   // 예외 검사
//
//        List<Channel> joinedChannels = channelRepository.readAll().stream()
//                        .filter(ch -> ch.getUsers().contains(user))
//                                .toList();
//    /*
//    JCF 서비스에서는 주소값이 공유되므로 contains(user) 같은 간결한 표현이 가능하지만,
//    이를 파일 저장소 환경으로 옮길 때는 반드시 ID 기반 비교와 저장(save) 로직을 추가해야 데이터 불일치를 막을 수 있다.
//    */
//        for (Channel ch : joinedChannels) {
//            // 내가 방장인 채널 - 채널 자체 삭제
//            if (ch.getOwner().getId().equals(id)) {
//                channelRepository.delete(ch.getId());
//            }
//            // 내가 참여한 채널 - 멤버 명단에서 나만 삭제
//            else{
//                ch.getUsers().removeIf(u -> u.equals(user));
//            }
//        }
//
//        // 사용자가 작성한 메시지 삭제
//        List<Message> sendedMessages = messageRepository.readAll().stream()
//                .filter(msg -> msg.getSender().getId().equals(id))
//                .toList();
//
//        for (Message msg : sendedMessages) {
//            messageRepository.delete(msg.getId());
//        }
//
//        userRepository.delete(id);
//    }
//
//}
