package com.sprint.mission;

import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.UpdateUserRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.utils.FileIOHelper;

import java.util.List;

public class JavaApplication {
    public static void main(String[] args) {
        FileIOHelper.flushData();

        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();

        BasicUserService userService = new BasicUserService(userRepository, channelRepository, messageRepository);
        BasicChannelService channelService = new BasicChannelService(userRepository, channelRepository, messageRepository);
        BasicMessageService messageService = new BasicMessageService(userRepository, channelRepository, messageRepository);

        test(userService, channelService, messageService, userRepository, channelRepository, messageRepository);
    }

    private static void test(BasicUserService userService, BasicChannelService channelService, BasicMessageService messageService, UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository) {
        CreateUserRequest createUserRequest1 = new CreateUserRequest("user1", "user1", "asdf@gmail.com", "01077777777");
        CreateUserRequest createUserRequest2 = new CreateUserRequest("user2", "user2", "asdf@gmail.com", "01077777777");
        CreateUserRequest createUserRequest3 = new CreateUserRequest("user3", "user3", "asdf@gmail.com", "01077777777");

        //유저 생성
        User user1 = userService.createUser(createUserRequest1);
        User user2 = userService.createUser(createUserRequest2);
        User user3 = userService.createUser(createUserRequest3);

        List<User> checkUsers = userRepository.findAll();

        //채널 생성
        Channel channel = channelService.createChannel(user1.getId(), "채널 by user1");

        checkUsers = userRepository.findAll();
        List<Channel> checkChannels = channelRepository.findAll();

        //채널 멤버 추가
        channelService.addChannelMember(user1.getId(), channel.getId(), user2.getId());
        channelService.addChannelMember(user1.getId(), channel.getId(), user3.getId());

        checkUsers = userRepository.findAll();
        checkChannels = channelRepository.findAll();

        //메세지 생성
        Message message1 = messageService.createMessage(user1.getId(), channel.getId(), "유저1가 만든 메세지");
        messageService.createMessage(user1.getId(), channel.getId(), "유저1가 만든 메세지");
        messageService.createMessage(user2.getId(), channel.getId(), "유저2가 만든 메세지");
        messageService.createMessage(user2.getId(), channel.getId(), "유저2가 만든 메세지");
        messageService.createMessage(user3.getId(), channel.getId(), "유저3가 만든 메세지");
        messageService.createMessage(user3.getId(), channel.getId(), "유저3가 만든 메세지");

        checkUsers = userRepository.findAll();
        checkChannels = channelRepository.findAll();
        List<Message> checkMessages = messageRepository.findAll();

        //유저 수정
        UpdateUserRequest updateUserRequest = new UpdateUserRequest("user2 updated", "user2 updated", "qwer@gmail.com", "01033333333");
        User updatedUser = userService.updateUser(user2.getId(), updateUserRequest);

        checkUsers = userRepository.findAll();
        checkChannels = channelRepository.findAll();
        checkMessages = messageRepository.findAll();

        //채널 수정
        Channel updatedChannel = channelService.updateChannelName(user1.getId(), channel.getId(), "채널 이름 수정");

        checkUsers = userRepository.findAll();
        checkChannels = channelRepository.findAll();
        checkMessages = messageRepository.findAll();

        //메세지 수정
        Message updatedMessage = messageService.updateMessage(user1.getId(), message1.getId(), "메세지 내용 수정");

        checkUsers = userRepository.findAll();
        checkChannels = channelRepository.findAll();
        checkMessages = messageRepository.findAll();

        //메세지 삭제
        try {
            messageService.deleteMessage(user1.getId(), message1.getId());
            messageService.findMessageByMessageId(message1.getId());
        } catch (Exception e) {
            System.out.println(e);
        }

        checkMessages = messageRepository.findAll();
        checkChannels = channelRepository.findAll();
        checkUsers = userRepository.findAll();

        //메세지 삭제 권한 실패
        Message message2 =
                messageService.createMessage(user1.getId(), channel.getId(), "삭제 테스트용 메세지");

        try {
            messageService.deleteMessage(user2.getId(), message2.getId());
        } catch (Exception e) {
            System.out.println(e);
        }

        checkMessages = messageRepository.findAll();

        //유저 삭제 성공
        try {
            userService.deleteUser(user3.getId());
            userService.findUserByUserID(user3.getId());
        } catch (Exception e) {
            System.out.println(e);
        }

        checkUsers = userRepository.findAll();
        checkMessages = messageRepository.findAll();
        checkChannels = channelRepository.findAll();

        //유저 삭제 실패
        try {
            userService.deleteUser(user1.getId());
        } catch (Exception e) {
            System.out.println(e);
        }
        checkUsers = userRepository.findAll();

        //채널 삭제
        try {
            channelService.deleteChannel(user1.getId(), channel.getId());
            channelService.findChannelByChannelId(channel.getId());
        } catch (Exception e) {
            System.out.println(e);
        }

        checkChannels = channelRepository.findAll();
        checkMessages = messageRepository.findAll();
        checkUsers = userRepository.findAll();


        //채널 삭제 권한 실패
        Channel channel2 = channelService.createChannel(user1.getId(), "삭제 테스트 채널");

        try {
            channelService.deleteChannel(user2.getId(), channel2.getId());
        } catch (Exception e) {
            System.out.println(e);
        }

        checkChannels = channelRepository.findAll();
    }
}
