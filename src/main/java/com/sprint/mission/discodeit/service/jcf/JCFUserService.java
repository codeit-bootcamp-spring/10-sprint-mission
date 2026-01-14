package com.sprint.mission.discodeit.service.jcf;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;


public class JCFUserService implements UserService {

    //데이터 저장 공간 생성 Map -> uuid 중복없고, 각 id 마다 user 저장하면 됨.
    // 인터페이스는 규약(약속)이지 데이터 저장소가 아님.
    // Map은 인터페이스지만 "데이터를 실제로 담을 공간" -> 후에 타입 정해서 HashMap으로 초기화.
    // private final Map<UUID, User> data = new HashMap<>();
    // final로 설정한 이유는....? -> Map 안의 내용은 바뀌어도 되지만, Map 변수 자체는 다른 객체로 바뀌면 안되므로.
    // ---- 서비스가 한번 만들어지면, 그 안의 저장소(Map) 자체는 바뀌지 않게 하기 위해!!!!

    private final Map<UUID, User> data;

    //생성자!! 에서 초기화 완료....
    public JCFUserService(){
        this.data = new HashMap<>();
    } //JCF UserService 인스턴스마다 자기만의 data 저장소를 가지게 되고,
      //인터페이스의 약속(UserService의 메서드 정의)은 그대로 따름.

    @Override
    public User createUser(String userName, String alias){
        // 유효성 검사
        if(alias == null || alias.trim().isEmpty()) {
            throw new IllegalStateException("별명은 비어있을 수 없습니다.");
        }
        // 별명 중복 검사
//        for (User existing: data.values()) {
//            if(existing.getAlias().equalsIgnoreCase(alias)) {
//                throw new IllegalStateException("이미 사용중인 별명입니다.");
//            }
//        }
        boolean aliasExisting = data.values().stream()
                .anyMatch(user->user.getAlias().equalsIgnoreCase(alias));
        if(aliasExisting){
            throw new IllegalStateException("이미 존재하는 별명입니다: " + alias);
        }
        User user = new User(userName, alias);
        data.put(user.getId(), user);
        return user;
    }

    // uuid를 갖고 hashmap에서 가져오기.
    // (관리자용) id로 사용자 조회
//    @Override
//    public User getUserByID(UUID uuid){
//        User user = findUserOrThrow(uuid);
//        return user;
//    }

    // HashMap 에서 value 값 모두 꺼내와서 arrayList<>()에 넣는 방식/
    @Override
    public List<User> getUserAll() {
        return new ArrayList<>(data.values()); // hashmap의 values들을 전부 받아서 배열기반 리스트에 생성!!
    }

//동명이인 문제 -> 해당 이름을 갖는 유저리스트 반환으로 변경.
    public List<User> getUserByName(String userName) {
        List<User> matches =  data.values().stream()
                .filter(user -> user.getUserName().equals(userName))
                .toList();

        if(matches.isEmpty()) {
            throw new NoSuchElementException("해당 이름의 유저가 없습니다: " + userName);
        }
        return matches;
    }

    // Stream 더 활용해서..
    // public List<User> getUserByName(String userName) {
    //    return Optional.ofNullable(
    //            data.values().stream()
    //                    .filter(user -> user.getUserName().equals(userName))
    //                    .toList()
    //    ).filter(list -> !list.isEmpty())
    //     .orElseThrow(() -> new NoSuchElementException("해당 이름의 유저가 없습니다: " + userName));
    //}



    // alias 로 사용자 조회
//    @Override
//    public User getUserByAlias(String alias){
//        for(User user: data.values()) {
//            if(user.getAlias().equals(alias)){
//                return user;
//            }
//        } throw new NoSuchElementException("해당 별명을 가진 사용자가 없습니다: " + alias);
//    }
    @Override
    public User getUserByAlias(String alias){
        return data.values().stream().filter(user->user.getAlias().equals(alias))
                .findFirst()
                .orElseThrow(()->new NoSuchElementException("해당 별명을 가진 사용자가 없습니다."));
    }

    //유저 정보 삭제
    @Override
    public void deleteUser(UUID uuid) {
        findUserOrThrow(uuid);
        data.remove(uuid);
    }


    // 파라미터 id 로
    // update는 반환을 User타입!! void 가 아닌.
    @Override
    public User updateUser(UUID uuid, String newName, String newAlias) {
        User existing = findUserOrThrow(uuid);
        existing.update(newName, newAlias);
        return existing;
    }

    public List<Message> getMessageByUser(UUID uuid){
        User user = data.get(uuid);
        if(user == null) return Collections.emptyList();
        return user.getMessages();
    }

    // ID로 유저을 찾고, 없으면 예외.
    public User findUserOrThrow(UUID id) {
        User user = data.get(id);
        if (user == null) {
            throw new NoSuchElementException("해당 유저가 존재하지 않습니다: " + id);
        }
        return user;
    }

}
