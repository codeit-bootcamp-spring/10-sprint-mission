package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

//도메인 별 서비스 구현체 테스트
public class JavaApplication {
    public static void main(String[] args){

        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService();
        MessageService messageService = new JCFMessageService(userService,channelService);

        //등록
        System.out.println("----------등록 시작----------");
        User user1 = userService.create(new User("인성","kis2690@naver.com","online"));
        User user2 = userService.create(new User("은비","enbi@naver.com","online"));
        User user3 = userService.create(new User("재형","jhp@naver.com","online"));
        Channel channel1 = channelService.create(new Channel("코드잇 벡엔드","텍스트",user1.getId()));
        Channel channel2 = channelService.create(new Channel("코드잇 프론트엔드","음성",user2.getId()));
        Message message1 = messageService.create(new Message("안녕하세요 SB 곽인성입니다.",user1.getId(),channel1.getId()));
        Message message2 = messageService.create(new Message("안녕하세요 FB 조은비입니다.",user2.getId(),channel1.getId()));

//        System.out.println("생성된 user객체:" + user1.toString());
//        System.out.println("생성된 Channel객체:" + channel1.toString());
//        System.out.println("생성된 Message객체:" + message1.toString());

        System.out.println("----------조회 시작----------");
        //조회(다건)
        System.out.println("사용자 목록: " + userService.findAll());
        System.out.println("채널목록: " + channelService.findAll());
        System.out.println("메세지 목록:" + messageService.findAll());

        //조회(단건)
        System.out.println("사용자2: " + userService.findById(user2.getId()));
        System.out.println("벡엔드 채널: " + channelService.findById(channel1.getId()));
        System.out.println("안녕하세요 FB 조은비입니다 메시지: " + messageService.findById(message2.getId()));

        System.out.println("----------수정 시작----------");
        //수정
        userService.update(user2.getId(),"조뿡빵","choenbi@gmail.com","offline");

        System.out.println("----------수정된 data 조회----------");
        //수정된 data 조회
        System.out.println(userService.findById(user2.getId()));

        System.out.println("----------삭제 시작----------");
        //삭제
        userService.delete(user3.getId());
        System.out.println("----------삭제된 data 조회----------");
        //조회를 통해 삭제 되었는지.
        System.out.println(userService.findAll());
    }
}
