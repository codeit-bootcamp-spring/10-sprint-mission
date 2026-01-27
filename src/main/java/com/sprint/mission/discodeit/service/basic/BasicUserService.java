package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;

    @Override
    public User create(String name) {
        User user = new User(name);
        save(user);

        return user;
    }

    @Override
    public User findUser(UUID userId) {
        User user = Optional.ofNullable(userRepository.findById(userId))
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));

        return user;
    }

    @Override
    public List<User> findAllUsers() {
        List<User> userList = new ArrayList<>(userRepository.findAll());

        System.out.println("[유저 전체 조회]");
        userList.forEach(System.out::println);

        return userList;
    }

    @Override
    public User addFriend(UUID senderId, UUID receiverId) {
        User sender = findUser(senderId);
        User receiver = findUser(receiverId);
        sender.addFriend(receiverId);
        receiver.addFriend(senderId);
        save(sender);
        save(receiver);

        return receiver;
    }

    @Override
    public List<User> findFriends(UUID userId) {
        User user = findUser(userId);
        List<User> friendList = user.getFriendsList().stream()
                        .map(userRepository::findById).filter(Objects::nonNull).toList();

        System.out.println("[친구 목록 조회]");
        friendList.forEach(System.out::println);
        return friendList;
    }

    @Override
    public User update(UUID userId, String newName) {
        User user = findUser(userId);

        user.updateUser(newName);
        save(user);

        return user;
    }

    @Override
    public void delete(UUID userId) {
        User user = findUser(userId);

        // 유저가 속한 채널에서 유저 id지우기
        List<UUID> channelList = new ArrayList<>(user.getChannelList());
        for (UUID channelId : channelList) {
            Channel channel = channelRepository.findById(channelId);
            if (channel != null) {
                channel.getUserList().remove(userId);
                channelRepository.save(channelId, channel);
            }
        }

        // 유저가 쓴 메시지 삭제
        List<UUID> messageList = new ArrayList<>(user.getMessageList());
        for (UUID messageId : messageList){
            messageRepository.delete(messageId);
        }

        // 친구 목록에서 유저 지우기
        List<UUID> friendList = new ArrayList<>(user.getFriendsList());
        for (UUID friendId : friendList) {
            User friend = userRepository.findById(friendId);
            if (friend != null) {
                friend.getFriendsList().remove(userId); // 친구의 친구 목록에서 나 삭제
                save(friend);   // 변경된 친구 정보 저장
            }
        }

        // 유저를 데이터에서 삭제
        userRepository.delete(userId);
    }

    @Override
    public void save(User user) {
        userRepository.save(user.getId(),user);
    }
}
