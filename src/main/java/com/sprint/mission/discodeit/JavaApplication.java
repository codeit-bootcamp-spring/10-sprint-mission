package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileObjectStore;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
    static User setUser(UserService userService) {
        User user = userService.createUser("woody@codeit.com", "woody", "woodyNick", "woody1234", "20000401");
        return user;
    }
    static Channel setChannel(ChannelService channelService, User user) {
        Channel channel = channelService.createChannel(user.getId(), ChannelType.PUBLIC, "공지", "공지 채널입니다");
        return channel;
    }
    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.createMessage(channel.getId(), author.getId(), "안녕하세요.");
        System.out.println("메세지 생성: " + message);

    }

    public static void main(String[] args) {
//        UserRepository userRepo = new JCFUserRepository();
//        ChannelRepository channelRepo = new JCFChannelRepository();
//        MessageRepository messageRepo = new JCFMessageRepository();
        FileObjectStore store = FileObjectStore.loadData();
        UserRepository userRepo = new FileUserRepository(store);
        ChannelRepository channelRepo = new FileChannelRepository(store);
        MessageRepository messageRepo = new FileMessageRepository(store);

        UserService userService = new BasicUserService(userRepo);
        ChannelService channelService = new BasicChannelService(channelRepo, userRepo);
        MessageService messageService = new BasicMessageService(messageRepo, userRepo, channelRepo);

        loadUser();
        loadChannel();
        loadMessage();

        // 셋업
        User user = setUser(userService);
        Channel channel = setChannel(channelService, user);
        // 테스트
        messageCreateTest(messageService, channel, user);

        loadUser();
        loadChannel();
        loadMessage();
    }
    static void loadUser() {
//        FileDataStore fileDataStore = FileDataStore.loadData();
//        UserService userService = new FileUserService(fileDataStore);
        FileObjectStore fileObjectStore = FileObjectStore.loadData(); // 파일에 있는 데이터 로드
        UserRepository userRepo = new FileUserRepository(fileObjectStore);
        UserService userService = new JCFUserService(userRepo);
        System.out.println("\n========== 유저 Load u1, u2, u3 ==========");
        System.out.println("생성된 전체 user ID = " + userService.findAllUsers().stream().map(user -> user.getUserName() + "(" + user.getEmail() + "): " + user.getId()).toList());
    }
    static void loadChannel() {
//        FileDataStore fileDataStore = FileDataStore.loadData();
//        UserService userService = new FileUserService(fileDataStore);
//        ChannelService channelService = new FileChannelService(fileDataStore, userService);
        FileObjectStore fileObjectStore = FileObjectStore.loadData();
        UserRepository userRepo = new FileUserRepository(fileObjectStore);
        ChannelRepository channelRepo = new FileChannelRepository(fileObjectStore);
        UserService userService = new JCFUserService(userRepo);
        ChannelService channelService = new JCFChannelService(channelRepo, userService);
        System.out.println("\n========== 채널 Load c1, c2, c3 ==========");
        System.out.println("생성된 전체 channel ID = " + channelService.findAllChannels().stream().map(channel -> channel.getChannelName() + "(owner=" + channel.getOwner().getId() + "): " + channel.getId()).toList());

//        System.out.println("u1의 참여 채널 리스트 = " + u1.getJoinChannelList().stream().map(c -> c.getChannelName() + ": " + c.getId()).toList());
//        System.out.println("\n========== 채널 정보 수정 테스트: c2 수정 ==========");
//        System.out.println("[전]PUBLIC channel list = " + channelService.findPublicOrPrivateChannel(ChannelType.PUBLIC).stream().map(c -> c.getChannelName() + ": " + c.getId()).toList());
//        System.out.println("[전]PRIVATE channel list = " + channelService.findPublicOrPrivateChannel(ChannelType.PRIVATE).stream().map(c -> c.getChannelName() + ": " + c.getId()).toList());
//        System.out.println("[전]channel2의 기본 정보 = " + channelService.findChannelById(c2.getId()).orElseThrow());
//        channelService.updateChannelInfo(u2.getId(), c2.getId(), ChannelType.PUBLIC, "c2u2PUBLIC", null);
//        System.out.println("[후]channel2의 기본 정보 = " + channelService.findChannelById(c2.getId()).orElseThrow());
//        System.out.println("[후]PUBLIC channel list = " + channelService.findPublicOrPrivateChannel(ChannelType.PUBLIC).stream().map(c -> c.getChannelName() + ": " + c.getId()).toList());
//        System.out.println("[후]PRIVATE channel list = " + channelService.findPublicOrPrivateChannel(ChannelType.PRIVATE).stream().map(c -> c.getChannelName() + ": " + c.getId()).toList());
    }
    static void loadMessage() {
//        FileDataStore fileDataStore = FileDataStore.loadData();
//        UserService userService = new FileUserService(fileDataStore);
//        ChannelService channelService = new FileChannelService(fileDataStore, userService);
//        MessageService messageService = new FileMessageService(fileDataStore, userService, channelService);
        FileObjectStore fileObjectStore = FileObjectStore.loadData();
        UserRepository userRepo = new FileUserRepository(fileObjectStore);
        ChannelRepository channelRepo = new FileChannelRepository(fileObjectStore);
        MessageRepository messageRepo = new FileMessageRepository(fileObjectStore);
        UserService userService = new JCFUserService(userRepo);
        ChannelService channelService = new JCFChannelService(channelRepo, userService);
        MessageService messageService = new JCFMessageService(messageRepo, userService, channelService);
        System.out.println("\n========== 메세지 Load m1, m2, m3 ==========");
        System.out.println("생성된 전체 message ID = " + messageService.findAllMessages().stream().map(message -> message.getId() + "(channel=" + message.getMessageChannel().getId() + "): " + message.getMessageContent()).toList());
    }
}
