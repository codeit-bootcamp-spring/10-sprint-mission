//package com.sprint.mission.discodeit.service.file;
//
//import com.sprint.mission.discodeit.entity.*;
//import com.sprint.mission.discodeit.repository.ChannelRepository;
//import com.sprint.mission.discodeit.repository.MessageRepository;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import com.sprint.mission.discodeit.service.ClearMemory;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.util.*;
//
//public class FileUserService implements UserService, ClearMemory {
//    UserRepository userRepository;
//    ChannelRepository channelRepository;
//    MessageRepository messageRepository;
//
//    public FileUserService(UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository) {
//        this.userRepository = userRepository;
//        this.channelRepository = channelRepository;
//        this.messageRepository = messageRepository;
//    }
//
//    @Override
//    public User create(String name, StatusType status) {
//        userRepository.findByName(name)
//                .ifPresent(u -> {
//                    throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
//                });
//        User user = new User(name, status);
//        return userRepository.save(user);
//    }
//
//    @Override
//    public User findById(UUID id) {
//        User user = userRepository.findById(id)
//                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 사용자 ID입니다."));
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
//        User user = findById(id);
//        user.updateName(newName);
//        user.updateStatus(newStatus);
//        return userRepository.save(user);
//    }
//
//    @Override
//    public List<Message> getUserMessages(UUID id) {
//        findById(id);
//        return messageRepository.readAll().stream()
//                .filter(msg -> msg.getSender().getId().equals(id))
//                .sorted(Comparator.comparing(Message::getCreatedAt))
//                .toList();
//    }
//
//    @Override
//    public List<Channel> getUserChannels(UUID id) {
//        findById(id);
//        return channelRepository.readAll().stream()
//                .filter(ch -> ch.getUsers().stream().anyMatch(u -> u.getId().equals(id)))
//                .toList();
//    }
//
//    @Override
//    public void delete(UUID id) {
//        findById(id);
//
//        // 사용자가 등록되어 있는 채널들
//        List<Channel> joinedChannels = channelRepository.readAll().stream()
//                .filter(ch -> ch.getUsers().stream()
//                        .anyMatch(u -> u.getId().equals(id)))
//                .toList();
//
//        for (Channel ch : joinedChannels) {
//            // 내가 방장인 채널 - 채널 자체 삭제
//            if (ch.getOwner().getId().equals(id)) {
//                channelRepository.delete(ch.getId());
//            }
//            // 참여한 채널 - 멤버 명단에서 나만 삭제
//            else {
//                ch.getUsers().removeIf(u -> u.getId().equals(id));
//                channelRepository.save(ch);
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
//
//    @Override
//    public void clear() {
//        userRepository.clear();
//    }
//}
