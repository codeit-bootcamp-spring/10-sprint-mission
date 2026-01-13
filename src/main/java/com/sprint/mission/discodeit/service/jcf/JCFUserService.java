package com.sprint.mission.discodeit.service.jcf;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;


public class JCFUserService implements UserService {

    //데이터 저장 공간 생성 Map -> uuid 중복없고, 각 id 마다 user 저장하면 됨.
    // 인터페이스는 규약(약속)이지 데이터 저장소가 아님.
    // Map은 인터페이스지만 "데이터를 실제로 담을 공간" -> 후에 타입 정해서 HashMap으로 초기화.
//    private final Map<UUID, User> data = new HashMap<>();
    // final로 설정한 이유는....? -> Map 안의 내용은 바뀌어도 되지만, Map 변수 자체는 다른 객체로 바뀌면 안되므로.
    // ---- 서비스가 한번 만들어지면, 그 안의 저장소(Map) 자체는 바뀌지 않게 하기 위해!!!!
    private final Map<UUID, User> data;

    //생성자!! 에서 초기화 완료....
    public JCFUserService(){
        this.data = new HashMap<>();
    } //JCF UserService 인스턴스마다 자기만의 data 저장소를 가지게 되고,
      //인터페이스의 약속(UserService의 메서드 정의)은 그대로 따름.

    @Override
    public void createUser(User user){
        data.put(user.getId(), user);
    }

    // uuid를 갖고 hashmap에서 가져오기.
    // (관리자용) id로 사용자 조회
    @Override
    public User getUserByID(UUID uuid){
        User user = data.get(uuid);
        if(user == null) {
            throw new NoSuchElementException("해당 ID의 사용자가 존재하지 않습니다: " + uuid);
        }
        return user;
    }

    // HashMap 에서 value 값 모두 꺼내와서 arrayList<>()에 넣는 방식/
    @Override
    public List<User> getUserAll() {
        return new ArrayList<>(data.values()); // hashmap의 values들을 전부 받아서 연결리스트에 생성!!
    }

    // (사용자용)
    // userName으로 사용자 조회
    @Override
    public User getUserByName (String userName){
        for(User user : data.values()){
            if(user.getUserName().equals(userName) ){
                return user;
            }
            // 순회해서 끝까지 못찾으면.... null 반환
        } return null;
    }
    // alias 로 사용자 조회
    @Override
    public User getUserByAlias(String alias){
        for(User user: data.values()) {
            if(user.getAlias().equals(alias)){
                return user;
            }
        } return null;
    }

    //유저 정보 삭제
    @Override
    public void deleteUser(UUID uuid) {
        if(!data.containsKey(uuid)){
            throw new NoSuchElementException("삭제할 사용자가 존재하지 않습니다: " + uuid);
        }
        data.remove(uuid);
    }

    // 유저 정보 수정 (구현 못하는중) -> 다르면 덮어씌우면 되려나
    // 파라미터 id 로
    @Override
    public void updateUser(User user) {
        User existing = data.get(user.getId());
        if(existing == null){
            throw new NoSuchElementException("수정할 사용자가 존재하지 않습니다." + user.getId());
        }
        existing.update(user.getUserName(), user.getAlias());
    }

    public List<Message> getMessageByUser(UUID uuid){
        User user = data.get(uuid);
        if(user == null) return Collections.emptyList();
        return user.getMessages();
    }

}
