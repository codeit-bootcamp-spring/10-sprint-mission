package com.sprint.mission;

import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.UpdateUserRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.domain.ChannelDomainService;
import com.sprint.mission.discodeit.service.domain.MessageDomainService;
import com.sprint.mission.discodeit.service.domain.UserDomainService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;

public class JavaApplication {
    public static void main(String[] args) {
        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService();

        UserDomainService userDomainService = new UserDomainService(userService, messageService);
        ChannelDomainService channelDomainService = new ChannelDomainService(channelService, messageService, userService);
        MessageDomainService messageDomainService = new MessageDomainService(messageService, userService, channelService);

        userCRUTest(userDomainService);
        channelCRUTest(userDomainService, channelDomainService);
        messageCRUTest(userDomainService, channelDomainService, messageDomainService);


        userService = new JCFUserService();
        channelService = new JCFChannelService();
        messageService = new JCFMessageService();

        userDomainService = new UserDomainService(userService, messageService);
        channelDomainService = new ChannelDomainService(channelService, messageService, userService);
        messageDomainService = new MessageDomainService(messageService, userService, channelService);

        deleteTest(userDomainService, channelDomainService, messageDomainService, userService, channelService, messageService);
    }

    private static void userCRUTest(UserDomainService userDomainService) {
        System.out.println("\n=================User CRU Test=================");
        System.out.println("\n<User 생성 성공>");
        CreateUserRequest createUserRequest1 = new CreateUserRequest("asdf", "asdf", "asdf@gmail.com", "01077777777");
        User user1 = userDomainService.createUser(createUserRequest1);

        System.out.println(user1);

        System.out.println("\n<User 단건 조회 성공>");
        User findByUserId = userDomainService.findUserByUserID(user1.getId());
        System.out.println(findByUserId);


        System.out.println("\n<User 다건 조회 성공>");
        userDomainService.createUser(createUserRequest1);

        List<User> allUsers = userDomainService.findAllUsers();
        System.out.println(allUsers);

        System.out.println("\n<User 수정 성공>");
        UpdateUserRequest updateUserRequest = new UpdateUserRequest("qwer", "qwer", "qwer@gmail.com", "01033333333");
        User updatedUser = userDomainService.updateUser(user1.getId(), updateUserRequest);
        System.out.println(updatedUser);

        System.out.println("\n<User 일부 필드 수정 성공>");
        UpdateUserRequest updateUserRequest2 = new UpdateUserRequest("patch", null, null, null);
        User updatedUser2 = userDomainService.updateUser(user1.getId(), updateUserRequest2);
        System.out.println(updatedUser2);
    }

    private static void channelCRUTest(UserDomainService userDomainService, ChannelDomainService channelDomainService) {
        System.out.println("\n=================Channel CRU Test=================");
        System.out.println("\n<Channel 생성 성공>");
        CreateUserRequest createUserRequest1 = new CreateUserRequest("user1", "user1", "asdf@gmail.com", "01077777777");
        User user1 = userDomainService.createUser(createUserRequest1);

        Channel channel1 = channelDomainService.createChannel(user1.getId(), "채널1 by user1");
        System.out.println(channel1);
        System.out.println(user1);

        System.out.println("\n<Channel 멤버 추가 성공>");
        CreateUserRequest createUserRequest2 = new CreateUserRequest("user2", "user2", "asdf@gmail.com", "01077777777");
        CreateUserRequest createUserRequest3 = new CreateUserRequest("user3", "user3", "asdf@gmail.com", "01077777777");

        User user2 = userDomainService.createUser(createUserRequest2);
        User user3 = userDomainService.createUser(createUserRequest3);

        channelDomainService.addChannelMember(user1.getId(), channel1.getId(), user2.getId());
        channelDomainService.addChannelMember(user1.getId(), channel1.getId(), user3.getId());

        System.out.println(channel1);
        System.out.println(user2);
        System.out.println(user3);

        System.out.println("\n<Channel 멤버 추가 권한 없어서 실패>");
        try {
            channelDomainService.addChannelMember(user2.getId(), channel1.getId(), user3.getId());
        } catch (Exception e) {
            System.out.println(e);
        }

        System.out.println("\n<Channel 단건조회 성공>");
        Channel channelByChannelId = channelDomainService.findChannelByChannelId(channel1.getId());
        System.out.println(channelByChannelId);

        System.out.println("\n<Channel 다건조회 성공>");
        channelDomainService.createChannel(user2.getId(), "채널2 by user2");
        List<Channel> allChannels = channelDomainService.findAllChannels();
        System.out.println(allChannels);

        System.out.println("\n<Channel 수정 성공>");
        Channel updated = channelDomainService.updateChannelName(user1.getId(), channel1.getId(), "채널1 by user1 updated");
        System.out.println(updated);

        System.out.println("\n<Channel 수정 권한 없어서 실패>");
        try {
            channelDomainService.updateChannelName(user2.getId(), channel1.getId(), "채널1 by user2 updated");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void messageCRUTest(UserDomainService userDomainService, ChannelDomainService channelDomainService, MessageDomainService messageDomainService) {
        System.out.println("\n=================Message CRU Test=================");

        System.out.println("\n<Message  생성 성공>");
        CreateUserRequest createUserRequest1 = new CreateUserRequest("user1", "user1", "asdf@gmail.com", "01077777777");
        CreateUserRequest createUserRequest2 = new CreateUserRequest("user2", "user2", "asdf@gmail.com", "01077777777");
        CreateUserRequest createUserRequest3 = new CreateUserRequest("user3", "user3", "asdf@gmail.com", "01077777777");

        User user1 = userDomainService.createUser(createUserRequest1);
        User user2 = userDomainService.createUser(createUserRequest2);
        User user3 = userDomainService.createUser(createUserRequest3);

        Channel channel = channelDomainService.createChannel(user1.getId(), "메세지 테스트용 채널 by user1");

        channelDomainService.addChannelMember(user1.getId(), channel.getId(), user2.getId());
        channelDomainService.addChannelMember(user1.getId(), channel.getId(), user3.getId());

        Message message1 = messageDomainService.createMessage(user1.getId(), channel.getId(), "유저1가 만든 메세지");
        System.out.println(message1);
        System.out.println(channel);
        System.out.println(user1);

        System.out.println("\n<Message 단건 조회 성공>");
        Message findmessage = messageDomainService.findMessageByMessageId(message1.getId());
        System.out.println(findmessage);

        System.out.println("\n<Message 다건 조회 성공>");
        messageDomainService.createMessage(user1.getId(), channel.getId(), "유저1가 만든 메세지");
        messageDomainService.createMessage(user2.getId(), channel.getId(), "유저2가 만든 메세지");
        messageDomainService.createMessage(user2.getId(), channel.getId(), "유저2가 만든 메세지");
        messageDomainService.createMessage(user2.getId(), channel.getId(), "유저3가 만든 메세지");

        List<Message> allMessages = messageDomainService.findAllMessages();
        System.out.println(allMessages);

        System.out.println("\n<Message 수정 성공>");
        Message updatedMessage = messageDomainService.updateMessage(user1.getId(), message1.getId(), "수정된 메세지");
        System.out.println(updatedMessage);

        System.out.println("\n<Message 수정 권한 없어서 실패>");
        try {
            messageDomainService.updateMessage(user2.getId(), message1.getId(), "수정된 메세지");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void deleteTest(UserDomainService userDomainService, ChannelDomainService channelDomainService, MessageDomainService messageDomainService, UserService userService, ChannelService channelService, MessageService messageService) {
        System.out.println("\n=================Delete Test=================");
        CreateUserRequest createUserRequest1 = new CreateUserRequest("user1", "user1", "asdf@gmail.com", "01077777777");
        CreateUserRequest createUserRequest2 = new CreateUserRequest("user2", "user2", "asdf@gmail.com", "01077777777");
        CreateUserRequest createUserRequest3 = new CreateUserRequest("user3", "user3", "asdf@gmail.com", "01077777777");

        User user1 = userDomainService.createUser(createUserRequest1);
        User user2 = userDomainService.createUser(createUserRequest2);
        User user3 = userDomainService.createUser(createUserRequest3);

        Channel channel = channelDomainService.createChannel(user1.getId(), "삭제 테스트용 채널 by user1");

        channelDomainService.addChannelMember(user1.getId(), channel.getId(), user2.getId());
        channelDomainService.addChannelMember(user1.getId(), channel.getId(), user3.getId());

        Message message1 = messageDomainService.createMessage(user1.getId(), channel.getId(), "유저1가 만든 메세지");
        messageDomainService.createMessage(user1.getId(), channel.getId(), "유저1가 만든 메세지");
        messageDomainService.createMessage(user2.getId(), channel.getId(), "유저2가 만든 메세지");
        messageDomainService.createMessage(user2.getId(), channel.getId(), "유저2가 만든 메세지");
        messageDomainService.createMessage(user2.getId(), channel.getId(), "유저2가 만든 메세지");
        messageDomainService.createMessage(user3.getId(), channel.getId(), "유저3가 만든 메세지");
        messageDomainService.createMessage(user3.getId(), channel.getId(), "유저3가 만든 메세지");
        messageDomainService.createMessage(user3.getId(), channel.getId(), "유저3가 만든 메세지");


        System.out.println("\n<Message 삭제 성공>");
        System.out.println("message1 삭제 전: ");
        System.out.println(user1);
        System.out.println(channel);
        System.out.println();
        messageDomainService.deleteMessage(user1.getId(), message1.getId());
        try {
            messageDomainService.findMessageByMessageId(message1.getId());
        } catch (Exception e) {
            System.out.println(e);
            System.out.println();
            System.out.println("message1 삭제 후: ");
            System.out.println(user1);
            System.out.println(channel);
        }

        System.out.println("\n<Message 삭제 권한 없어서 실패>");
        Message message2 = messageDomainService.createMessage(user1.getId(), channel.getId(), "유저1가 만든 메세지");
        try {
            messageDomainService.deleteMessage(user2.getId(), message2.getId());
        } catch (Exception e) {
            System.out.println(e);
        }

        System.out.println("\n<User 삭제 성공>");
        System.out.println("user3 삭제 전: ");
        System.out.println(userService.findAll());
        System.out.println(channelService.findAll());
        System.out.println(messageService.findAll());

        userDomainService.deleteUser(user3.getId());
        System.out.println("user3 삭제 후: ");
        System.out.println(userService.findAll());
        System.out.println(channelService.findAll());
        System.out.println(messageService.findAll());

        System.out.println("\n<User 삭제 채널 주인이어서 실패>");
        try {
            userDomainService.deleteUser(user1.getId());
        } catch (Exception e) {
            System.out.println(e);
        }

        System.out.println("\n<Channel 삭제 성공>");
        System.out.println("channel 삭제 전: ");
        System.out.println(userService.findAll());
        System.out.println(channelService.findAll());
        System.out.println(messageService.findAll());

        channelDomainService.deleteChannel(user1.getId(), channel.getId());
        System.out.println("channel 삭제 후: ");
        System.out.println(userService.findAll());
        System.out.println(channelService.findAll());
        System.out.println(messageService.findAll());

        System.out.println("\n<Channel 삭제 권한 없어서 실패>");
        try {
            channelDomainService.deleteChannel(user1.getId(), channel.getId());
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
