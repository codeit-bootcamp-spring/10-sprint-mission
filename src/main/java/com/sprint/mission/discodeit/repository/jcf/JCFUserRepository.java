package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data;
//    private ChannelRepository channelRepository;
//    private MessageRepository messageRepository;
//    다른 레포지토리리를 의존하면 안됨

    public JCFUserRepository() {
        data = new HashMap<UUID, User>();
    }
    
    // 단순 저장만
    @Override
    public User save(User user) {
        data.put(user.getId(), user);
        return user;
    }
    
    // 검증과 예외는 서비스에서 작성
    // 서비스에서 id가 null이거나 조회 결과가 null이라면 예외처리를 할지 없으면 생성할지 정책이 바뀔 수 있음
    // id null(입력 검증), 조회 결과 null(예외) 이런식의 정책은 서비스가 결정
    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

//    @Override
//    public User updateById(UUID id, User userName) {
//        return null;
//    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

    // 해당 channel Id를 가진 유저 목록을 반환
//    @Override
//    public List<User> getUsersByChannelId(UUID channelId) {
//        return data.values()
//                .stream()
//                .filter(user ->
//                        user.getChannels().
//                                stream().
//                                anyMatch(channel -> channel.getId().equals(channelId)))
//                .toList();
//    }
    // 서비스 영역으로
    // 서비스로 옮기면 지금은 data.values()~~ 이런식으로 레포지토리의 필드인 data를 이용하는데
    // 서비스로 옮기면 findAll()을 이용해서 전체 리스트를 반환받아서 이용?

//    다른 레포지토리 의존 X
//    @Override
//    public void setChannelRepository(ChannelRepository channelRepository) {
//        this.channelRepository = channelRepository;
//    }
//
//    @Override
//    public void setMessageRepository(MessageRepository messageRepository) {
//        this.messageRepository = messageRepository;
//    }

    // JCF 레포지토리에는 필요하지 않은 기능
//    @Override
//    public void saveData() {
//
//    }
//
//    @Override
//    public void loadData() {
//
//    }
}
