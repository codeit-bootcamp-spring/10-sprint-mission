package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.service.jcf.CommonDelete;

//도메인 별 서비스 구현체 테스트
public class JavaApplication {
    public static void main(String[] args){


        UserService userService = new FileUserService();
        ChannelService channelService = new JCFChannelService(userService);
        MessageService messageService = new JCFMessageService(userService,channelService);
        CommonDelete userDelete = new CommonDelete(userService,channelService,messageService);

        //FileUserService 테스트
        User user1 = userService.create("곽인성","kis2690@naver.com","online");
        User user2 = userService.create("조은비","enbi@naver.com","online");

        System.out.println("---유저 목록---");
        System.out.println(userService.findAll());
        System.out.println();

        System.out.println("---곽인성 출력---");
        System.out.println(userService.findById(user1.getId()));
        System.out.println();

        System.out.println("---조은비 조안비로 수정---");
        System.out.println(userService.update(user2.getId(),"조안비",null,null));
        System.out.println();

        System.out.println("---조안비 삭제---");
        System.out.println("삭제되는 유저:" + userService.delete(user2.getId()) + "/ 유저 목록:" +userService.findAll() );


    }
}
