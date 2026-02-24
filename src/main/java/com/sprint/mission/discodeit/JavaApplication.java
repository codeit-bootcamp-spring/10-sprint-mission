package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.service.jcf.CommonDelete;

//도메인 별 서비스 구현체 테스트
public class JavaApplication {
    public static void main(String[] args){


        UserService userService = new FileUserService();
        ChannelService channelService = new FileChannelService(userService);
        MessageService messageService = new FileMessageService(userService,channelService);
        CommonDelete commonDelete = new CommonDelete(userService,channelService,messageService);

        //File*Service 테스트
        User user1 = userService.create("곽인성","kis2690@naver.com","online");
        User user2 = userService.create("조은비","enbi@naver.com","online");

        Channel channel1 = channelService.create("롤","음성",user1.getId());
        Channel channel2 = channelService.create("코딩스터디","텍스트",user2.getId());

        Message message1 = messageService.create("롤채널 안녕하세요",user1.getId(),channel1.getId());
        Message message2 = messageService.create("코딩스터디 안녕하세요",user2.getId(),channel2.getId());
        Message message3 = messageService.create("롤채널분들 뭐하세요?",user1.getId(),channel1.getId());

        System.out.println("[전체 메시지 목록]");
        System.out.println(messageService.findAll());
        System.out.println();

        System.out.println("[곽인성의 메시지 목록]");
        System.out.println(messageService.findByUser(user1.getId()));
        System.out.println();

        System.out.println("[곽인성의 롤채널 안녕하세요 메시지 삭제]");
        System.out.println(messageService.delete(message1.getId()));
        System.out.println();

        System.out.println("[곽인성의 삭제후 메시지 목록]");
        System.out.println(messageService.findByUser(user1.getId()));
        System.out.println();
//        ----------------------------------------------------
//        System.out.println("[채널 목록]");
//        System.out.println(channelService.findAll());
//        System.out.println();
//
//        System.out.println("[코딩스터디 채널정보 출력]");
//        System.out.println(channelService.findById(channel2.getId()));
//        System.out.println();
//
//        System.out.println("[코딩스터디 채널명 코스로 수정]");
//        System.out.println(channelService.update(channel2.getId(),"코스"));
//        System.out.println();
//
//        System.out.println("[곽인성의 채널목록]");
//        System.out.println(user1.getChannelList());
//        System.out.println();
//
//        System.out.println("롤 채널 삭제");
//        commonDelete.channelDelete(channel1.getId());
//        System.out.println("곽인성의 채널목록");
//        System.out.println(user1.getChannelList());
//        ----------------------------------------------------

//        System.out.println("---유저 목록---");
//        System.out.println(userService.findAll());
//        System.out.println();
//
//        System.out.println("---곽인성 출력---");
//        System.out.println(userService.findById(user1.getId()));
//        System.out.println();
//
//        System.out.println("---조은비 조안비로 수정---");
//        System.out.println(userService.update(user2.getId(),"조안비",null,null));
//        System.out.println();
//
//        System.out.println("---조안비 삭제---");
//        System.out.println("삭제되는 유저:" + userService.delete(user2.getId()) + "/ 유저 목록:" +userService.findAll() );


    }
}
