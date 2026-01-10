package com.sprint.mission.discodeit;
import com.sprint.mission.discodeit.entity.*;
public class JavaApplication {
    public static void main(String[] args) {
        System.out.println("디스코드 도메인 테스트");

        //UserTest 완료
          //User user = new User("최종인", "jongin98@naver.com", "뚱토리");
//        System.out.println("[User 생성 완료]");
//        System.out.println("이름: " + user.getUserName());
//        System.out.println("이메일: " + user.getUserEmail());
//        System.out.println("전화번호: " + user.getUserPhoneNumber());
//        System.out.println("별명: " + user.getAlias());
//        System.out.println("생성 시각: " + user.getCreatedAt());
//        user.update("김수빈", null, "010-7279-82", null);
//        System.out.println("정보 마지막 업데이트: " + user.getUpdatedAt());
//        System.out.println(user.getUpdatedAt());
//        System.out.println();
//        user.update("최종인", null, "010-279-82", null);
//        System.out.println("정보 마지막 업데이트: " + user.getUpdatedAt());
//        System.out.println(user.getUpdatedAt());
        // 업데이트 시간 같게 나오는데. 너무 세분화되어 실행될때 차이가 거의 안나서 같게 보여짐. 실제 코드값은 다름.

        User user = new User("최종인", "jongin98@naver.com", "뚱토리");

        Channel channel1 = new Channel("종인_채널");

        Message message = new Message("안녕하세요.", user, channel1 );

//        System.out.println("내용 " + message.getContent());
//        System.out.println("보낸 사람 " + message.getSender());
//        System.out.println("채널: " + message.getChannel());
//        System.out.println("메세지 생성 시각: " + message.getCreatedAt());
        //메세지 테스트 완료/

        // Channel 테스트
        Channel channel = new Channel("일반 채팅방");
        System.out.println("[Channel 생성 완료]");
        System.out.println("채널 이름: " + channel.getChannelName());
        System.out.println("채널 생성 시각: " + channel.getCreatedAt());
        System.out.println();
        // 수정 테스트
        channel.update("프라이빗 채팅방");
        System.out.println("[Channel 수정]");
        System.out.println("수정된 채널 이름: " + channel.getChannelName());
        System.out.println("수정 시각: " + channel.getUpdatedAt());
        System.out.println();


    }
}
