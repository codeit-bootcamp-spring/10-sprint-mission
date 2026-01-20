package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.helper.ChannelHelper;
import com.sprint.mission.discodeit.service.jcf.ChannelService;
import com.sprint.mission.discodeit.service.jcf.MessageService;
import com.sprint.mission.discodeit.service.jcf.UserService;

import java.util.UUID;

import static com.sprint.mission.discodeit.service.helper.ChannelHelper.safeCreateChannel;
import static com.sprint.mission.discodeit.service.helper.ChannelHelper.safeReadChannel;

public class Application {
    public static void main(String[] args) {

        ChannelService channelService = new FileChannelService(); // 파일 채널 서비스 인스턴스화
        UserService userService = new FileUserService(channelService); // 유저 채널 서비스 인스턴스화 (채널 서비스를 주입)
        channelService.setUserService(userService); // 파일 채널 서비스에 유저 서비스를 주입
        MessageService messageService = new FileMessageService(channelService, userService); // 메시지 채널 서비스 인스턴스화

        Channel ch1 = channelService.createChannel("채널1", "공용채널입니다.", Channel.CHANNEL_TYPE.PUBLIC);
        Channel ch2 = channelService.createChannel("채널2", "비밀채널입니다.", Channel.CHANNEL_TYPE.PRIVATE);

        User u1 = userService.createUser("성경", "tjdrud@naver.com", "tjdrud");

        channelService.userJoin(u1.getUserId(), ch1.getId());
        userService.joinChannel("tjdrud",ch2.getId());

        //Message m1 = messageService.createMessage("안녕하십니까!", ch1.getId(), u1.getUserId());
        //System.out.println(channelService.readChannel(ch1.getId()));


        //channelService.deleteChannelbyName("채널1");
        channelService.readAllChannels();
        userService.readAllUsers();
        System.out.println(userService.readUser("tjdrud").getChannelList());
        userService.exitChannel("tjdrud", ch1.getId());
        messageService.readAllMessage().stream().forEach(m -> System.out.println(m.getId()));

        //messageService.deleteMessage(UUID.fromString("a1f1a210-1d31-4a4e-8cb2-4422fca60556"));




    }
}
