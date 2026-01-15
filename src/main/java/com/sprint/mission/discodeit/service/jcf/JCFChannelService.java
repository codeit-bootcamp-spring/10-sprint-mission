package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.validation.ValidationMethods;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data; // channel DB 데이터
    private final UserService userService;

    public JCFChannelService(Map<UUID, Channel> data, UserService userService) {
        this.data = data;
        this.userService = userService;
    }

    @Override
    public String toString() {
        return "JCFChannelService{" +
//                "data = " + data + ", " +
                "data key = " + data.keySet() + ", " +
                "data value = " + data.values() +
                '}';
    }

    // C. 생성: Channel 생성 후 Channel 객체 반환
    @Override
    public Channel createChannel(UUID ownerId, Boolean isPrivate, String channelName, String channelDescription) {
        // 로그인 되어있는 user ID null / user 객체 존재 확인
        validateAndGetUserByUserId(ownerId);
        // channelName 검증
        ValidationMethods.validateNullBlankString(channelName, "channelName");

        Channel channel = new Channel(ownerId, isPrivate, channelName, channelDescription);

        data.put(channel.getId(), channel);
        return channel;
    }

    // R. 읽기
    // 특정 채널 정보 읽기
    @Override
    public Optional<Channel> findChannelById(UUID channelId) {
        // Channel ID null 검증
        ValidationMethods.validateId(channelId);

        return Optional.ofNullable(data.get(channelId));
    }

    // R. 모두 읽기
    // 채널 목록 전체
    @Override
    public List<Channel> readAllChannel() {
        return new ArrayList<>(data.values());
    }

    // 비공개 여부에 따른 채널 목록
    @Override
    public List<Channel> readPublicOrPrivateChannel(Boolean isPrivate) {
        if (isPrivate == null) {
            // null이면 전체 조회
            return readAllChannel();
        } else if (isPrivate) {
            return data.values().stream()
//                    .filter(channel -> channel.getChannelType() == true)
                    .toList();
        } else { // !isPrivate
            return data.values().stream()
//                    .filter(channel -> channel.getChannelType() == false)
                    .toList();
        }
    }

    // 특정 채널에 속한 모든 유저
    @Override
    public List<User> readAllUsersByChannelId(UUID channelId) {
        // Channel ID null 검증
        ValidationMethods.validateId(channelId);

        Channel channel = data.get(channelId);
        // channel이 null인지 확인
        if (channel == null) {
            // null이면 빈 리스트
            return Collections.emptyList();
        }

        return channel.getChannelUsersList().stream().toList();
    }

    // 특정 채널 이름이 들어간 채널 검색
    @Override
    public List<Channel> searchChannelByChannelName(String partialChannelName) {
        // partialChannelName 검증
        ValidationMethods.validateNullBlankString(partialChannelName, "partialChannelName");

        return data.values().stream()
                .filter(channel -> channel.getChannelName().contains(partialChannelName))
                .toList();
    }

    // 특정 채널에서 특정 사용자 찾기
    @Override
    public List<User> searchChannelUserByPartialName(UUID channelId, String partialName) {
        // Channel ID null 검증
        ValidationMethods.validateId(channelId);

        Channel channel = data.get(channelId);
        // channel이 null인지 확인
        if (channel == null) {
            // null이면 빈 리스트
            return Collections.emptyList();
        }

        return channel.getChannelUsersList().stream()
                .filter(user -> user.getUserName().contains(partialName) ||
                        user.getNickName().contains(partialName))
                .toList();
    }

    // 특정 채널의 모든 메시지 읽어오기
    @Override
    public List<Message> readChannelMessageByChannelId(UUID channelId) {
        // Channel ID null 검증
        ValidationMethods.validateId(channelId);

        Channel channel = data.get(channelId);
        // channel이 null인지 확인
        if (channel == null) {
            return Collections.emptyList();
        }

        return channel.getChannelMessagesList().stream().toList();
    }

    // 특정 채널에서 원하는 메시지 찾기
    @Override
    public List<Message> searchChannelMessageByChannelIdAndWord(UUID channelId, String partialWord) {
        // Channel ID null 검증
        ValidationMethods.validateId(channelId);

        Channel channel = data.get(channelId);
        // channel이 null인지 확인
        if (channel == null) {
            // null이면 빈 리스트
            return Collections.emptyList();
        }

        return channel.getChannelMessagesList().stream()
                .filter(message -> message.getContent().contains(partialWord))
                        .toList();
    }

    // U. 수정
    // `Duplicated code fragment (12 lines long)`
    // ID null 검증 / req ID와 target ID의 동일한지 확인 / user 객체와 channel 객체 존재 확인
    public Channel validateMethods(Map<UUID, Channel> data, UUID requestId, UUID channelId) {
        // request ID null 검증
        ValidationMethods.validateId(requestId);
        
        // Channel ID null & channel 객체 존재 확인
        Channel channel = validateAndGetChannelByChannelId(channelId);


//        User owner = channel.getOwner();
//        ValidationMethods.existUser(owner);

        // requestId와 owner ID가 동일한지 검증
//        ValidationMethods.validateSameId(requestId, owner.getId());

        return channel;
    }

    // 채널 channelName 수정
    @Override
    public Channel updateChannelName(UUID requestId, UUID channelId, String channelName) {
        // ID null 검증 / req ID와 target ID의 동일한지 확인 / user 객체와 channel 객체 존재 확인
        Channel channel = validateMethods(data, requestId, channelId);
        ValidationMethods.validateNullBlankString(channelName, "channelName");

        channel.updateChannelName(channelName);
        return channel;
    }

    // 채널 isPrivate 수정
    @Override
    public Channel updateChannelIsPrivate(UUID requestId, UUID channelId, Boolean isPrivate) {
        Channel channel = validateMethods(data, requestId, channelId);

        channel.updateIsPrivate(isPrivate);
        return channel;
    }

    // 채널 owner 변경
    @Override
    public Channel updateChannelOwner(UUID requestId, UUID channelId, User owner) {
        Channel channel = validateMethods(data, requestId, channelId);

        // 존재하는 owner인지 확인
//        ValidationMethods.existUser(owner);

//        channel.changeOwner(channel, owner);
        return channel;
    }

    // 채널 description 수정
    @Override
    public Channel updateChannelDescription(UUID requestId, UUID channelId, String channelDescription) {
        Channel channel = validateMethods(data, requestId, channelId);

        channel.updateChannelDescription(channelDescription);
        return channel;
    }

    // D. 삭제
    @Override
    public void deleteChannel(UUID requestId, UUID channelId) {
        Channel channel = validateMethods(data, requestId, channelId);

        data.remove(channelId);
    }

//    // 연관 관계 정리
//    // 해당 채널과 관련된 모든 메시지 삭제 및 채널이 보유한 메세지 리스트 연결 끊기
//        channel.getChannelMessagesList()
//                .forEach(message ->
//            messageService.deleteMessage(message.getAuthor().getId(), message.getId()));
//
//    // 해당 채널에 참여한 user 찾아서 내보내기
//        channel.getChannelUsersList().forEach(user -> user.leaveChannel(channel));
//    // 해당 채널을 소유한 소유주(user)를 찾아서 owner 소유권 제거
//        channel.getOwner().removeChannelOwner(channel);

    //// validation
    // 로그인 되어있는 user ID null & user 객체 존재 확인
    public void validateAndGetUserByUserId(UUID userId) {
        userService.findUserById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 사용자가 없습니다."));
    }

    // Channel ID null & channel 객체 존재 확인
    public Channel validateAndGetChannelByChannelId(UUID channelId) {
        return findChannelById(channelId)
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 없습니다."));
    }
}
