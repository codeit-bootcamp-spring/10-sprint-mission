//package com.sprint.mission.discodeit.service.jcf;
//
//import com.sprint.mission.discodeit.entity.*;
//import com.sprint.mission.discodeit.repository.MessageRepository;
//import com.sprint.mission.discodeit.service.*;
//import com.sprint.mission.discodeit.utils.Validation;
//
//import java.util.*;
//
//public class JCFMessageService implements MessageService{
//    private final MessageRepository messageRepo;
//    //private final Map<UUID, Message> data;
//
//    // 서비스마다 자기 이외의 서비스 객체 만들어... 의존성 주입..
//    public JCFMessageService(MessageRepository messageRepo) {
//        this.messageRepo = messageRepo;
//        //this.data = new HashMap<>();
//    }
//
//    // 생성
//    @Override
//    public Message createMessage(String content, UUID userId, UUID channelId) {
//        //  입력값 검증
//        Validation.notBlank(content, "메세지 내용");
//        if (userId == null || channelId == null) {
//            throw new IllegalArgumentException("sender나 channel가 null일 수 없습니다.");
//        }
//
//        //  Message 생성 및 저장
//
//        Message message = new Message(content, userId, channelId);
//
//        messageRepo.save(message);
//        //data.put(message.getId(), message);
//
//        return message;
//    }
//
//
//    // 조회
//    // 메세지 전부 조회
//    @Override
//    public List<Message> getMessageAll() {
//        return messageRepo.findAll();
//        //return new ArrayList<>(data.values());
//    }
//    // 메세지의 id로 조회
//    public Message getMessageById(UUID id) {
//        return messageRepo.findAll().stream()
//                .filter(m -> m.getId().equals(id))
//                .findFirst()
//                .orElseThrow(()->new NoSuchElementException("해당 Id의 메세지는 존재하지 않습니다: "+ id ));
//    }
//
//    // <밑에는 리스트 반환>
//    // 이름 중복 허용인데...??? -> 그럼 id로 찾도록 (관리자용)
//    // (사용자용)은 별명으로 조회하도록..?
//    @Override
//    public List<Message> getMsgListSenderId(UUID senderId) {
//        return messageRepo.findAll().stream()
//                .filter(m -> m.getSender() != null
//                        && m.getSender().getId().equals(senderId))
//                .toList();
//    }
//
//
//    //갱신
//    //메세지 내용 수정기능
//    @Override
//    public Message updateMessage(UUID uuid, String newContent){
//        Message existing = getMessageById(uuid);
//        existing.update(newContent);
//        messageRepo.save(existing);
//        return existing;
//    }
//
//
//
//
//    //삭제
//    //삭제시 유효성 검증 필요!!!
//    @Override
//    public void deleteMessage(UUID id) {
//        messageRepo.delete(id);
////        if(!data.containsKey(id)) {
////            throw new NoSuchElementException("삭제할 메세지가 존재하지 않습니다: " + id);
////        }
////        data.remove(id);
//    }
//
//
//
//}
