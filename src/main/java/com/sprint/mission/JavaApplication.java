package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserCoordinatorService;
import com.sprint.mission.discodeit.service.UserService;
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

        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService(userService);
        UserCoordinatorService userCoordinatorService = new JCFUserCoordinatorService(userService, channelService);
        MessageService messageService = new JCFMessageService(userService,userCoordinatorService);
        //사용자
        //생성
        System.out.println("-----------------사용자 생성-------------------");
        UUID userId = userService.signUp("김코드","ab123@codeit.com", "ab123").getId();
        UUID userId2 = userService.signUp("이코드","cd123@codeit.com", "ab123").getId();
        UUID userId3 = userService.signUp("박코드","ef123@codeit.com", "ab123").getId();
        UUID userId4 = userService.signUp("최코드","gh123@codeit.com", "ab123").getId();

        System.out.println("------------동일한 이메일로 계정 생성----------------");
        try{
            UUID userId5 = userService.signUp("강코드","ab123@codeit.com", "ab123").getId();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        // 사용자1의 정보 확인
        System.out.println("---------------특정 유저 조회--------------------- ");
        System.out.println(userService.getUserById(userId));
        System.out.println("---------------모든 유저 조회--------------------- ");
        System.out.println(userService.findAllUsers());

        //유저 기본 설정
        System.out.println("------------유저 정보 수정-----------------");
        userService.updateInfo(userId, "코드잇",null,null);//이름만
        System.out.println(userService.getUserById(userId));
        userService.updateInfo(userId, null,"abv123@codeit.com",null);//이메일만
        System.out.println(userService.getUserById(userId));
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
        System.out.println(userCoordinatorService.getChannelByIdAndMemberId(channelId,userId));

        //모든 채널 조회
        System.out.println("------------모든 채널 조회-------------");
        System.out.println(channelService.findAllChannels());
        System.out.println("------------특정 유저의 바뀐 참여채널 정보-----------------");
        System.out.println(userService.findAllUsers());

        System.out.println("-----------특정 채널에 속한 유저 조회------------------");
        System.out.println(userCoordinatorService.findAllMembers(channelId3,userId2));

        System.out.println("-------외부 사용자가 채널 유저 조회------------");
        try{
            System.out.println(userCoordinatorService.findAllMembers(channelId3,userId));
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
        System.out.println(userCoordinatorService.getChannelByIdAndMemberId(channelId2,userId2));
        System.out.println("-----------채널 멤버 추가-----------");
        System.out.println(userCoordinatorService.addMembers(channelId3,userId2, List.of(userId,userId2,userId4)));
        System.out.println(userCoordinatorService.findAllMembers(channelId3,userId2));
        System.out.println("---------채널 멤버가 아닌 사용자가 해당 채널 조회--------------");
        try {
            System.out.println(userCoordinatorService.getChannelByIdAndMemberId(channelId3,userId3));
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println("------4번 유저의 참여 채널 정보--------");
        System.out.println(userCoordinatorService.getChannels(userId4));
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
        System.out.println(messageService.getMessagesByChannelIdAndMemberId(channelId3, userId));
        //메세지 수정
        System.out.println("---------메세지 수정---------------------");
        messageService.updateMessage(userId, messageId, "굿바이!!!!");
        System.out.println(messageService.getMessagesByChannelIdAndMemberId(channelId3,userId));
        System.out.println("---------작성자가 아닌 사용자가 메세지 수정------------");
        try {
            messageService.updateMessage(userId2, messageId, "다시 안녕!!");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        System.out.println("----------멤버가 아닌 사용자가 채널의 메세지 확인-------");
        try {
            System.out.println(messageService.getMessagesByChannelIdAndMemberId(channelId3,userId3));
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        //메세지삭제
        System.out.println("--------------메세지 삭제------------");
        System.out.println(messageService.getMessagesByChannelIdAndMemberId(channelId2,userId));
        messageService.delete(messageID4, userId3);
        System.out.println(messageService.getMessagesByChannelIdAndMemberId(channelId2,userId));
        System.out.println("-------존재하지 않는 메세지 삭제------------");
        try {
            messageService.delete(messageID4, userId3);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println("-------------작성자가 아닌 사용자가 메세지 삭제------------");
        try {
            messageService.delete(messageID3, userId2);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

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
        System.out.println(userCoordinatorService.findAllMembers(channelId3, userId2));
        System.out.println("---------------제외된 멤버의 채널 조회--------------");
        System.out.println(userCoordinatorService.getChannels(userId));
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
        channelService.deleteChannelById(channelId4, userId4);
        System.out.println("-----------삭제된 채널 조회----------------");
        try {
            System.out.println(userCoordinatorService.getChannelByIdAndMemberId(channelId4,userId4));
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println("-----------채널 모두 조회----------------");
        System.out.println(channelService.findAllChannels());
        System.out.println("-------------원래 멤버였던 사용자의 정보 보기---------------");
        System.out.println(userService.getUserById(userId4));
        System.out.println("-----------삭제된 채널의 메세지 조회----------------");
        try {
            System.out.println(messageService.getMessagesByChannelIdAndMemberId(channelId4,userId4));
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        //사용자 탈퇴
        System.out.println("------------사용자 탈퇴----------------");
        userService.removeUserById(userId);

        System.out.println("-----------탈퇴한 사용자 조회----------------");
        try{
            System.out.println(userService.getUserById(userId).toString());
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println("-------모든 사용자 조회----------");
        System.out.println(userService.findAllUsers());
        System.out.println("-----------채널 멤버 확인-----------");
        System.out.println(userCoordinatorService.findAllMembers(channelId3, userId2));
        System.out.println("------------채널 정보 확인-----------");
        System.out.println(userCoordinatorService.getChannelByIdAndMemberId(channelId3,userId2));
        System.out.println("-----------채널 메세지 확인-----------");
        System.out.println(messageService.getMessagesByChannelIdAndMemberId(channelId3,userId2));



    }
}