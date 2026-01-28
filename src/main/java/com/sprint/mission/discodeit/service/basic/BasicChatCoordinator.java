package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.utils.Validation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChatCoordinator {

    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;

    // 파일 반영을 위해 repo도 주입 (Coordinator가 책임)
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;

//    public BasicChatCoordinator(
//            UserService userService,
//            ChannelService channelService,
//            MessageService messageService,
//            UserRepository userRepository,
//            ChannelRepository channelRepository,
//            MessageRepository messageRepository
//    ) {
//        this.userService = userService;
//        this.channelService = channelService;
//        this.messageService = messageService;
//        this.userRepository = userRepository;
//        this.channelRepository = channelRepository;
//        this.messageRepository = messageRepository;
//    }


    // JOIN / LEAVE

    public void joinChannel(UUID userId, UUID channelId) {
        User user = userService.findUserById(userId);
        Channel channel = channelService.findChannelById(channelId);

        user.joinChannel(channel);

        // 파일 반영
        userRepository.save(user);
        channelRepository.save(channel);
    }

    public void leaveChannel(UUID userId, UUID channelId) {
        User user = userService.findUserById(userId);
        Channel channel = channelService.findChannelById(channelId);

        user.leaveChannel(channel);

        // 파일 반영
        userRepository.save(user);
        channelRepository.save(channel);
    }


    // SEND MESSAGE (저장 + 그래프 동기화 + 파일 반영)

    public Message sendMessage(UUID userId, UUID channelId, String content) {
        Validation.notBlank(content, "메세지 내용");

        User user = userService.findUserById(userId);
        Channel channel = channelService.findChannelById(channelId);

        // 메시지 생성/저장 (messages.dat 반영)
        Message message = messageService.createMessage(content, userId, channelId);

        // 동기화 (User.messages, Channel.messages)
        user.addMessage(message);
        channel.addMessages(message);

        // 파일 반영 (users.dat / channels.dat에 messages 리스트가 반영되려면 필요)
        userRepository.save(user);
        channelRepository.save(channel);

        return message;
    }

    public List<Message> getMessagesInChannel(UUID channelId) {
        Channel channel = channelService.findChannelById(channelId);
        return channel.getMessages();
    }

    public List<Channel> getChannelsByUser(UUID userId) {
        User user = userService.findUserById(userId);
        return user.getJoinedChannels();
    }

    public List<User> getUsersInChannel(UUID channelId) {
        Channel channel = channelService.findChannelById(channelId);
        return channel.getParticipants();
    }



    // (repo 삭제 + user/channel 리스트에서도 제거 + 파일 반영)

    public void deleteMessageClean(UUID messageId) {
        Message msg = messageService.getMessageById(messageId);

        // sender/channel이 연결되어 있다고 가정
        User sender = msg.getSender();
        Channel channel = msg.getChannel();

        // repo에서 삭제
        messageService.deleteMessage(messageId);

        // 그래프 정리
        if (sender != null) sender.getMessages().remove(msg);
        if (channel != null) channel.getMessages().remove(msg);

        // 파일 반영
        if (sender != null) userRepository.save(sender);
        if (channel != null) channelRepository.save(channel);
    }


    // 참여 채널 전부 퇴장
    // 본인 메시지 전부 삭제
    // 유저 삭제

    public void deleteUserClean(UUID userId) {
        User user = userService.findUserById(userId);

        // 채널 퇴장(양방향 정리) + 파일 반영
        List<Channel> joined = new ArrayList<>(user.getJoinedChannels());
        for (Channel ch : joined) {
            user.leaveChannel(ch);
            channelRepository.save(ch);
        }
        userRepository.save(user);

        // 본인 메시지 전부 삭제 (repo + 그래프 정리)
        List<Message> myMessages = new ArrayList<>(user.getMessages());
        for (Message m : myMessages) {
            // 채널에서도 제거
            if (m.getChannel() != null) {
                m.getChannel().getMessages().remove(m);
                channelRepository.save(m.getChannel());
            }
            messageRepository.delete(m.getId());
        }

        // 유저 삭제
        userService.deleteUser(userId);
    }

    // 참가자 전부 퇴장
    // 채널 메시지 전부 삭제
    // 채널 삭제

    public void deleteChannelClean(UUID channelId) {
        Channel channel = channelService.findChannelById(channelId);

        //  참가자 전부 퇴장
        List<User> participants = new ArrayList<>(channel.getParticipants());
        for (User u : participants) {
            u.leaveChannel(channel);
            userRepository.save(u);
        }
        channelRepository.save(channel);

        //  채널 메시지 전부 삭제
        List<Message> msgs = new ArrayList<>(channel.getMessages());
        for (Message m : msgs) {
            if (m.getSender() != null) {
                m.getSender().getMessages().remove(m);
                userRepository.save(m.getSender());
            }
            messageRepository.delete(m.getId());
        }
        channel.getMessages().clear();
        channelRepository.save(channel);

        //  채널 삭제
        channelService.deleteChannel(channelId);
    }


    //user 업데이트 및 채널,메세지 영속화
    public User updateUserCascade(UUID userId, String newName, String newAlias) {
        User updated = userService.updateUser(userId, newName, newAlias);
        // 이 유저를 포함하는 채널들.... 저장?
        for(Channel ch: channelService.getChannelAll()) {
            boolean contains = ch.getParticipants().stream()
                    .anyMatch(user-> user.getId().equals(userId));
            if (contains) channelRepository.save(ch);
        }

        // 유저가 sender인 메세지를 저장 (messages.dat 저장)
        for(Message m: messageService.getMessageAll()){
            if(m.getSender() !=null && m.getSender().getId().equals(userId)){
                messageRepository.save(m);
            }
        }
        // 유저 데이터도 한번 더 저장!!
        userRepository.save(updated);
        return updated;
    }

    // Channel 업데이트 + 메세지 영속화
    public Channel updateChannelCascade(UUID channelId, String newName) {
        Channel updated = channelService.updateChannel(channelId, newName);

        // 이 채널을 참조하는 메세지 저장.
        for(Message m: messageService.getMessageAll()) {
            if(m.getChannel() != null && m.getChannel().getId().equals(channelId)){
                messageRepository.save(m);
            }
        }
        channelRepository.save(updated);
        return updated;
    }


}
