package com.sprint.mission;

import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserCoordinatorService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserCoordinatorService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;


import java.util.List;
import java.util.UUID;

//TIP 코드를 <b>실행</b>하려면 <shortcut actionId="Run"/>을(를) 누르거나
// 에디터 여백에 있는 <icon src="AllIcons.Actions.Execute"/> 아이콘을 클릭하세요.
public class JavaApplication {
    public static void main(String[] args) {

        //UserService userService = new JCFUserService();
        //UserService userService = new FileUserService();
        //ChannelService channelService = new JCFChannelService(userService);
        //ChannelService channelService = new FileChannelService(userService);
        //UserCoordinatorService userCoordinatorService = new JCFUserCoordinatorService(userService, channelService);
        //MessageService messageService = new FileMessageService(userService,userCoordinatorService);

        //jcf레포버전
        /*
        UserRepository userRepository = new JCFUserRepository();
        ChannelRepository channelRepository = new JCFChannelRepository();
        MessageRepository messageRepository = new JCFMessageRepository();
        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository,userService,messageRepository);
        UserCoordinatorService userCoordinatorService = new JCFUserCoordinatorService(userService, channelService);
        MessageService messageService = new BasicMessageService(messageRepository, userCoordinatorService, userService);
        */
        //file레포 버전
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();
        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository,userService,messageRepository);
        UserCoordinatorService userCoordinatorService = new JCFUserCoordinatorService(userService, channelService);
        MessageService messageService = new BasicMessageService(messageRepository, userCoordinatorService, userService);

        //사용자
        //생성

        System.out.println("-----------------사용자 생성-------------------");
        try{
            userService.signUp("김코드","ab123@codeit.com", "ab123");
            userService.signUp("이코드","cd123@codeit.com", "ab123");
            userService.signUp("박코드","ef123@codeit.com", "ab123");
            userService.signUp("최코드","gh123@codeit.com", "ab123");

        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        UUID userId;
        try{
            userId = userService.signIn("ab123@codeit.com", "ab123").getId();
        }catch(Exception e){
            System.out.println(e.getMessage());
            userId = userService.signIn("abv123@codeit.com", "ab123").getId();
        }
        UUID userId2 = userService.signIn("cd123@codeit.com", "ab123").getId();
        UUID userId3 = userService.signIn("ef123@codeit.com", "ab123").getId();
        UUID userId4 = userService.signIn("gh123@codeit.com", "ab123").getId();

        System.out.println("------------동일한 이메일로 계정 생성----------------");
        try{
            UUID userId5 = userService.signUp("강코드","ab123@codeit.com", "ab123").getId();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        // 사용자1의 정보 확인
        System.out.println("---------------특정 유저 조회--------------------- ");
        System.out.println(userService.findUserById(userId));
        System.out.println("---------------모든 유저 조회--------------------- ");
        System.out.println(userService.findAllUsers());

        //유저 기본 설정
        try{
            System.out.println("------------유저 정보 수정-----------------");
            userService.updateInfo(userId, "코드잇",null,null);//이름만
            System.out.println(userService.findUserById(userId));
            userService.updateInfo(userId, null,"abv123@codeit.com",null);//이메일만
            System.out.println(userService.findUserById(userId));
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        try {//null과 이전값의 조합으로 변경 요청하는 경우
            System.out.println("----------null과 이전값의 조합으로 변경 요청하는 경우---------------");
            userService.updateInfo(userId, null, "abv123@codeit.com", null);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }



        //채널 만들기
        System.out.println("------------채널 생성----------------");
        UUID channelId = channelService.addChannel("공지사항","백엔드 공지를 하는 채널입니다.",userId, ChannelType.PUBLIC).getId();
        UUID channelId2 = channelService.addChannel("수다방","수다 채널입니다.",userId, ChannelType.PUBLIC).getId();
        UUID channelId3 = channelService.addChannel("비밀방","비공개 방입니다.",userId2,ChannelType.PRIVATE).getId();
        UUID channelId4 = channelService.addChannel("비밀방2","비공개 방입니다.",userId4,ChannelType.PRIVATE).getId();
        System.out.println("------------중복된 이름의 채널 생성----------------");
        try{
            UUID chanelId5 = channelService.addChannel("수다방","수다 채널입니다~~~",userId, ChannelType.PUBLIC).getId();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }


        //채널 정보 조회
        System.out.println("-----------특정 채널 정보 조회----------------");
        System.out.println(userCoordinatorService.findAccessibleChannel(channelId,userId));

        //모든 채널 조회
        System.out.println("------------모든 채널 조회-------------");
        System.out.println(channelService.findAllChannels());
        System.out.println("------------특정 유저의 바뀐 참여채널 정보-----------------");
        System.out.println(userService.findAllUsers());

        System.out.println("-----------특정 채널에 속한 유저 조회------------------");
        System.out.println(userCoordinatorService.findAllMembersByChannelIdAndMemberId(channelId3,userId2));

        System.out.println("-------외부 사용자가 채널 유저 조회------------");
        try{
            System.out.println(userCoordinatorService.findAllMembersByChannelIdAndMemberId(channelId3,userId));
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        //업데이트
        System.out.println("---------------채널 정보 수정------------------");
        System.out.println("-----------공개채널을 이미 존재하는 채널이름으로 수정----------------");
        try {
            channelService.updateChannelInfo(channelId2,userId,"공지사항",null);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println("-----------채널 소유자가 아닌 사용자가 채널 정보 수정------------------");
        try{
            channelService.updateChannelInfo(channelId2,userId2,"친목방",null);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        channelService.updateChannelInfo(channelId2, userId,"친목방",null);
        channelService.updateChannelInfo(channelId2, userId,null,"구성원들과 친해지기 위한 채널입니다.");
        System.out.println("-----------수정된 채널 조회----------------");
        System.out.println(userCoordinatorService.findAccessibleChannel(channelId2,userId2));
        System.out.println("-----------채널 멤버 추가-----------");
        System.out.println(userCoordinatorService.addMembers(channelId3,userId2, List.of(userId,userId2,userId4)));
        System.out.println(userCoordinatorService.findAllMembersByChannelIdAndMemberId(channelId3,userId2));
        System.out.println("---------채널 멤버가 아닌 사용자가 해당 채널 조회--------------");
        try {
            System.out.println(userCoordinatorService.findAccessibleChannel(channelId3,userId3));
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println("------4번 유저의 참여 채널 정보--------");
        System.out.println(userCoordinatorService.findChannelsByUserId(userId4));

        //메세지
        //생성
        UUID messageId = messageService.send(userId, channelId3, "안녕!!!").getId();
        UUID messageID2 = messageService.send(userId2, channelId3, "끝말잇기 시작!").getId();
        messageService.send(userId, channelId3, "나부터");
        messageService.send(userId, channelId3, "기러기");
        messageService.send(userId2, channelId3, "기차");
        messageService.send(userId, channelId3, "차력");
        UUID messageID3 = messageService.send(userId3, channelId2, "안녕십니까").getId();
        UUID messageID4 = messageService.send(userId3, channelId2, "안녕십니wkl").getId();
        System.out.println("------------메세지 조회---------------");
        //메세지 조회
        System.out.println(messageService.findMessagesByChannelIdAndMemberId(channelId3, userId));
        //메세지 수정
        System.out.println("---------메세지 수정---------------------");
        messageService.updateMessage(userId, messageId, "굿바이!!!!");
        System.out.println(messageService.findMessagesByChannelIdAndMemberId(channelId3,userId));
        System.out.println("---------작성자가 아닌 사용자가 메세지 수정------------");
        try {
            messageService.updateMessage(userId2, messageId, "다시 안녕!!");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        System.out.println("----------멤버가 아닌 사용자가 채널의 메세지 확인-------");
        try {
            System.out.println(messageService.findMessagesByChannelIdAndMemberId(channelId3,userId3));
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        //메세지삭제
        System.out.println("--------------메세지 삭제------------");
        System.out.println(messageService.findMessagesByChannelIdAndMemberId(channelId2,userId));
        messageService.deleteByIdAndSenderId(messageID4, userId3);
        System.out.println(messageService.findMessagesByChannelIdAndMemberId(channelId2,userId));
        System.out.println("-------존재하지 않는 메세지 삭제------------");
        try {
            messageService.deleteByIdAndSenderId(messageID4, userId3);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println("-------------작성자가 아닌 사용자가 메세지 삭제------------");
        try {
            messageService.deleteByIdAndSenderId(messageID3, userId2);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        //다이렉트 메세지
        System.out.println("-----------다이렉트 메세지 발송---------------");
        System.out.println(messageService.sendDirectMessage(userId,userId2,"디엠을 시작하자~~"));
        System.out.println(messageService.sendDirectMessage(userId2,userId,"그래 좋아"));
        System.out.println(messageService.sendDirectMessage(userId,userId2,"가나다"));
        System.out.println(messageService.sendDirectMessage(userId,userId2,"라마바사!!"));
        UUID dmid = messageService.sendDirectMessage(userId2,userId,"아자차카타파하~~").getId();
        //디엠 한번에 조회
        System.out.println("-----------다이렉트 메세지 조회---------------");
        System.out.println(messageService.findDirectMessagesBySenderIdAndReceiverId(userId,userId2));
        //디엠 수정
        System.out.println("-----------다이렉트 메세지 수정---------------");
        System.out.println(messageService.updateMessage(userId2,dmid,"아자차카!!!까지로 바꿀게~~"));
        System.out.println("-----------수정 다이렉트 메세지 목록 조회---------------");
        System.out.println(messageService.findDirectMessagesBySenderIdAndReceiverId(userId,userId2));
        //채널 멤버 제외
        try{
            System.out.println("------소유자가 자신을 제외----------");
            userCoordinatorService.removeMembers(channelId3,userId2,List.of(userId2));
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println("---------------소유자가 비밀 채널의 멤버를 제외--------------");
        System.out.println(userCoordinatorService.removeMembers(channelId3, userId2, List.of(userId,userId4)));
        System.out.println("---------------변경된 비밀 채널의 멤버 조회--------------");
        System.out.println(userCoordinatorService.findAllMembersByChannelIdAndMemberId(channelId3, userId2));
        System.out.println("---------------제외된 멤버의 채널 조회--------------");
        System.out.println(userCoordinatorService.findChannelsByUserId(userId));
        //다시 멤버 복구
        userCoordinatorService.addMembers(channelId3, userId2, List.of(userId,userId4));
        //채널에서 나가기
        System.out.println("------------------채널에서 나가기(gh123)-------------");
        System.out.println(userCoordinatorService.removeMember(channelId3, userId4));
        try{
            System.out.println("------------------소유자가 채널에서 나가기-------------");
            System.out.println(userCoordinatorService.removeMember(channelId3, userId2));
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        try{
            System.out.println("------------------멤버가 아닌 사용자가 채널에서 나가기-------------");
            System.out.println(userCoordinatorService.removeMember(channelId3, userId3));
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        //채널 삭제

        System.out.println("-----------채널 삭제----------------");
        channelService.deleteChannelByIdAndOwnerId(channelId4, userId4);
        System.out.println("-----------삭제된 채널 조회----------------");
        try {
            System.out.println(userCoordinatorService.findAccessibleChannel(channelId4,userId4));
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println("-----------채널 모두 조회----------------");
        System.out.println(channelService.findAllChannels());
        System.out.println("-------------원래 멤버였던 사용자의 정보 보기---------------");
        System.out.println(userService.findUserById(userId4));
        System.out.println("-----------삭제된 채널의 메세지 조회----------------");
        try {
            System.out.println(messageService.findMessagesByChannelIdAndMemberId(channelId4,userId4));
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        //사용자 탈퇴
        System.out.println("------------사용자 탈퇴----------------");
        userService.removeUserById(userId);

        System.out.println("-----------탈퇴한 사용자 조회----------------");
        try{
            System.out.println(userService.findUserById(userId).toString());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println("-------모든 사용자 조회----------");
        System.out.println(userService.findAllUsers());
        System.out.println("-----------채널 멤버 확인-----------");
        System.out.println(userCoordinatorService.findAllMembersByChannelIdAndMemberId(channelId3, userId2));
        System.out.println("------------채널 정보 확인-----------");
        System.out.println(userCoordinatorService.findAccessibleChannel(channelId3,userId2));
        System.out.println("-----------채널 메세지 확인-----------");
        System.out.println(messageService.findMessagesByChannelIdAndMemberId(channelId3,userId2));
        System.out.println("-----------다이렉트 메세지 조회(삭제된 유저의 디엠 상대방이 조회)---------------");
        try{
            System.out.println(messageService.findDirectMessagesBySenderIdAndReceiverId(userId2,userId));//user2는 탈퇴 안하니까 메세지 확인가능
            System.out.println("-----------다이렉트 메세지 조회(삭제 당사자가 조회)---------------");
            System.out.println(messageService.findDirectMessagesBySenderIdAndReceiverId(userId,userId2));//user1은 불가
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }
}