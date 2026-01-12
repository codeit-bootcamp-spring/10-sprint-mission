package com.sprint.mission;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

//TIP 코드를 <b>실행</b>하려면 <shortcut actionId="Run"/>을(를) 누르거나
// 에디터 여백에 있는 <icon src="AllIcons.Actions.Execute"/> 아이콘을 클릭하세요.
public class JavaApplication {
    public static void main(String[] args) {

        UserService userService = new JCFUserService();
        ChannelService channelService = new JCFChannelService(userService);
        MessageService messageService = new JCFMessageService(userService,channelService);
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
        userService.updateName(userId,"코드잇");
        System.out.println(userService.getUserById(userId));

        //채널 만들기
        System.out.println("------------채널 생성----------------");
        UUID channelId = channelService.addChannel("공지사항","백엔드 공지를 하는 채널입니다.",userId, true).getId();
        UUID channelId2 = channelService.addChannel("수다방","수다 채널입니다.",userId, true).getId();
        UUID channelId3 = channelService.addChannel("비밀방","비공개 방입니다.",userId2,false).getId();
        UUID channelId4 = channelService.addChannel("비밀방2","비공개 방입니다.",userId4,false).getId();
        System.out.println("------------중복된 이름의 채널 생성----------------");
        try{
            UUID chanelId5 = channelService.addChannel("수다방","수다 채널입니다~~~",userId, true).getId();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        //채널 정보 조회
        System.out.println("-----------특정 채널 정보 조회----------------");
        System.out.println(channelService.getChannelByIdAndMemberId(channelId,userId));
        //모든 채널 조회
        System.out.println("------------모든 채널 조회-------------");
        System.out.println(channelService.findAllChannels());
        System.out.println("------------특정 유저의 바뀐 참여채널 정보-----------------");
        System.out.println(userService.findAllUsers());
        System.out.println("-----------특정 채널에 속한 유저 조회------------------");
        System.out.println(channelService.findAllMembers(channelId3,userId2));
        System.out.println("-------외부 사용자가 채널 유저 조회------------");
        try{
            System.out.println(channelService.findAllMembers(channelId3,userId));
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        //업데이트
        System.out.println("---------------채널 정보 수정------------------");
        System.out.println("-----------이미 존재하는 채널이름으로 수정----------------");
        try {
            channelService.updateChannelName(channelId2, "공지사항", userId);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        System.out.println("-----------채널 소유자가 아닌 사용자가 채널 정보 수정------------------");
        try{
            channelService.updateChannelName(channelId2,"친목방", userId2);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        channelService.updateChannelName(channelId2,"친목방", userId);
        channelService.updateChannelDescription(channelId2, "구성원들과 친해지기 위한 채널입니다.",userId);
        System.out.println("-----------수정된 채널 조회----------------");
        System.out.println(channelService.getChannelByIdAndMemberId(channelId2,userId2));
        System.out.println("-----------채널 멤버 추가-----------");
        System.out.println(channelService.addMembers(channelId3,userId2, List.of(userId,userId2,userId4)));
        System.out.println(channelService.findAllMembers(channelId3,userId2));
        System.out.println("---------채널 멤버가 아닌 사용자가 해당 채널 조회--------------");
        try {
            System.out.println(channelService.getChannelByIdAndMemberId(channelId3,userId3));
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        //메세지
        //생성
        User user1 = userService.getUserById(userId);
        User user2 = userService.getUserById(userId2);
        User user3 = userService.getUserById(userId3);
        Channel channel1 = channelService.getChannelByIdAndMemberId(channelId3,userId);
        Channel channel2 = channelService.getChannelByIdAndMemberId(channelId3,userId2);
        Channel channel3 = channelService.getChannelByIdAndMemberId(channelId2,userId3);
        UUID messageId = messageService.send(user1, channel1, "안녕!!!").getId();
        UUID messageID2 = messageService.send(user2, channel2, "그래, 안녕@").getId();
        UUID messageID3 = messageService.send(user3, channel3, "안녕십니까").getId();
        UUID messageID4 = messageService.send(user3, channel3, "안녕십니wkl").getId();
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
            System.out.println(messageService.getMessagesByChannelIdAndMemberId(channelId3,userId4));
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

        //채널 삭제

        System.out.println("-----------채널 삭제----------------");
        channelService.deleteChannelById(channelId4, userId4);
        System.out.println("-----------삭제된 채널 조회----------------");
        try {
            System.out.println(channelService.getChannelByIdAndMemberId(channelId4,userId4));
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
        System.out.println(channelService.findAllMembers(channelId3, userId2));
        System.out.println("------------채널 정보 확인-----------");
        System.out.println(channelService.getChannelByIdAndMemberId(channelId3,userId2));
        System.out.println("-----------채널 메세지 확인-----------");
        System.out.println(messageService.getMessagesByChannelIdAndMemberId(channelId3,userId2));

    }
}