package com.sprint.mission.discodeit.entity;

public class Main extends Basic {
    public static void main(String[] args) {
        User user1 = new User("최종인", "Jongin");

//        System.out.println(user1.getUserName());
//        System.out.println(user1.getUpdatedAt());
//        System.out.println("정보 생성 시간:" + user1.getCreatedAt().longValue());
//
//        System.out.println("");
//        System.out.println("user 정보 변경");
//        System.out.println("");
//
//        user1.update("김수빈", null,null,null);
//        System.out.println("정보 변경 시간: " + user1.getUpdatedAt().longValue());
//        System.out.println(user1.getUserName());
//        System.out.println(user1.getAlias());


//        User user = new User("홍길동", "hong@example.com", "길동이");
//        Channel channel = new Channel("자유채팅방");
//        Message message = new Message("안녕!", user, channel);
//
//// 자동으로 연결됨
//        System.out.println(user.getMessages().contains(message)); // true
//        System.out.println(message.getSender().getUserName()); // "홍길동"

        System.out.println("=== Channel ↔ Message 연결 테스트 ===");

        // 1️⃣ 유저와 채널 생성
        User user = new User("홍길동", "길동이");
        Channel channel = new Channel("자유채널");

        // 2️⃣ 메시지 생성 (Message 생성자에서 setSender, setChannel 자동 연결)
        Message message1 = new Message("안녕하세요~", user, channel);
        Message message2 = new Message("오늘 날씨 좋네요!", user, channel);

        // 3️⃣ User ↔ Message 관계 확인
        System.out.println("\n[User ↔ Message 테스트]");
        System.out.println("User가 보낸 메시지 수: " + user.getMessages().size());
        for (Message msg : user.getMessages()) {
            System.out.println(" - " + msg.getContent());
        }

        // 4️⃣ Channel ↔ Message 관계 확인
        System.out.println("\n[Channel ↔ Message 테스트]");
        System.out.println("채널에 등록된 메시지 수: " + channel.getMessages().size());
        for (Message msg : channel.getMessages()) {
            System.out.println(" - [" + msg.getSender().getUserName() + "] " + msg.getContent());
        }

        // 5️⃣ Message → Channel / User 확인
        System.out.println("\n[Message → Channel / User 테스트]");
        System.out.println("message1의 채널: " + message1.getChannel().getChannelName());
        System.out.println("message1의 보낸 사람: " + message1.getSender().getUserName());
    }
}
