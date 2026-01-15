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
        ChannelService channelService = new JCFChannelService(userService);
        MessageService messageService = new JCFMessageService(userService,channelService);

        //등록
        System.out.println("----------등록 시작----------");
        User user1 = userService.create("인성","kis2690@naver.com","online");
        User user2 = userService.create("은비","enbi@naver.com","online");
        User user3 = userService.create("재형","jhp@naver.com","online");
        User user4 = userService.create("승택","nst@naver.com","online");
        Channel channel1 = channelService.create("코드잇 벡엔드","텍스트",user1.getId());
        Channel channel2 = channelService.create("코드잇 프론트엔드","음성",user2.getId());
        Message message1 = messageService.create("안녕하세요 SB 곽인성입니다.",user1.getId(),channel1.getId());
        Message message2 = messageService.create("안녕하세요 FB 조은비입니다.",user2.getId(),channel1.getId());


//        //조회(다건)
//        System.out.println("사용자 목록: " + userService.findAll());
//        System.out.println("채널목록: " + channelService.findAll());
//        System.out.println("메세지 목록:" + messageService.findAll());
//
//        //조회(단건)
//        System.out.println("사용자2: " + userService.findById(user2.getId()));
//        System.out.println("벡엔드 채널: " + channelService.findById(channel1.getId()));
//        System.out.println("안녕하세요 FB 조은비입니다 메시지: " + messageService.findById(message2.getId()));

        //수정
        System.out.println(userService.update(user2.getId(),null,"choenbi@gmail.com","offline"));

//        //삭제
//        System.out.println(userService.delete(user3.getId()));
//        System.out.println(userService.findAll());

//
//        //채널 참가
//        channelService.enter(channel1.getId(),user4.getId());
//        channelService.enter(channel2.getId(),user4.getId());

//       //채널 나가기
//        channelService.leave(channel1.getId(),user4.getId());
//
//        //방장이 나가려고 할때
//       channelService.leave(channel1.getId(),user1.getId());
//
//        //특정 유저가 참가돼있는 채널 목록
//        System.out.println("user4의 채널목록: " + channelService.findByUser(user4.getId()));
//
//        System.out.println("user1의 메시지 리스트: " + messageService.findByUser(user1.getId()));

    }
}
