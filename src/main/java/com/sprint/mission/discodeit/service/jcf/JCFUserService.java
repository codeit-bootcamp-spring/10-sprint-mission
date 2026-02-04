/*
package com.sprint.mission.discodeit.service.jcf;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.utils.Validation;
import java.util.*;


public class JCFUserService implements UserService {

    //데이터 저장 공간 생성 Map -> uuid 중복없고, 각 id 마다 user 저장하면 됨.
    // final로 설정한 이유는....? -> Map 안의 내용은 바뀌어도 되지만, Map 변수 자체는 다른 객체로 바뀌면 안되므로.
    // ---- 서비스가 한번 만들어지면, 그 안의 저장소(Map) 자체는 바뀌지 않게 하기 위해!!!!

    //private final Map<UUID, User> data;
    // UserRepo로 변경!
    private final UserRepository userRepo;

    //생성자 에서 초기화 완료
    public JCFUserService(UserRepository userRepo) {
        this.userRepo = userRepo;
        //this.data = new HashMap<>();
        //이제 서비스층에선 직접 data 받지 않고, UserRepository에서 직접 HashMap으로 받게 이관!!!
    }
    //JCF UserService 인스턴스마다 자기만의 data 저장소를 가지게 되고,
    // 인터페이스의 약속(UserService의 메서드 정의)은 그대로 따름.


    // 생성
    @Override
    public User createUser(String userName, String alias, String email, String password) {
        // 유효성 검사
        Validation.notBlank(userName, "이름");
        Validation.notBlank(alias, "별명");

        List<User> allUsers = userRepo.findAll();
        Validation.noDuplicate(allUsers,
                user -> user.getAlias().equals(alias),
                "이미 존재하는 별명입니다: " + alias);
//        Validation.noDuplicate(
//                data.values(),
//                user -> user.getAlias().equals(alias),
//                "이미 존재하는 별명입니다: " + alias
//        );
        User user = new User(userName, alias, email, password);
        userRepo.save(user);
        //data.put(user.getId(), user);
        return user;
    }

    // 조회 (없으면 예외)
    // 전체 유저 조회
    @Override
    public List<User> getUserAll() {
        return userRepo.findAll();
        //return new ArrayList<>(data.values()); // hashmap의 values들을 전부 받아서 배열기반 리스트에 생성!!
    }

    // ID로 유저을 조회
    public void findUserById(UUID uuid) {
        return userRepo.findById(uuid)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 유저가 없습니다: " + uuid));
    }


    // 해당 이름을 갖는 유저리스트 반환으로 변경.(동명이인때문)
    public List<User> getUserByName(String userName) {
        List<User> matches = userRepo.findAll().stream()
                .filter(user -> user.getUserName().equals(userName))
                .toList();
        if (matches.isEmpty()) {
            throw new NoSuchElementException("해당 이름의 유저가 없습니다: " + userName);
        }
        return matches;
    }

    //별명으로 유저 조회
    @Override
    public User getUserByAlias(String alias) {
        return userRepo.findAll().stream() //data.values().stream()
                .filter(user -> user.getAlias().equals(alias))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 별명을 가진 사용자가 없습니다."));
    }

    // 갱신
    @Override
    public User updateUser(UUID uuid, String newName, String newAlias) {


        // 빈칸 or null 검사
        Validation.notBlank(newName, "이름");
        Validation.notBlank(newAlias, "별명");

        User existing = userRepo.findById(uuid)
                .orElseThrow(() -> new NoSuchElementException("수정할 유저가 없습니다: " + uuid));
        // 별명 중복 검사 추가
        List<User> allUsers = userRepo.findAll();
        Validation.noDuplicate(
                allUsers,
                user -> user.getAlias().equals(newAlias),
                "이미 존재하는 별명입니다." + newAlias
        );
        existing.changeUserName(newName);
        existing.changeAlias(newAlias);
        userRepo.save(existing);
        return existing;
    }


    // 삭제
    // 유저 정보 삭제
    @Override
    public void deleteUser(UUID uuid) {
        userRepo.findById(uuid)
                .orElseThrow(() -> new NoSuchElementException("삭제할 유저가 없습니다: " + uuid));
        userRepo.delete(uuid);
    }


}






    // update는 반환을 User타입!! void 가 아닌.
//    @Override
//    public User updateUser(UUID uuid, String newName, String newAlias) {
//        User existing = findUserOrThrow(uuid);
//        existing.userUpdate(newName, newAlias);
//        return existing;
//    }





    // 현재 채널에 참가한 유저 리스트 조회..
//    public List<User> getUsersInChannel(UUID uuid){
//        Channel channel = channelService.findChannelById(uuid);
//        return channel.getParticipants();
//        // 현재 uuid 채널의 참가자 리스트 반환.
//    }

*/
