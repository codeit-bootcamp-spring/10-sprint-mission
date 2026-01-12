package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> channelMap = new HashMap<>();

    private Channel findChannelByIdOrThrow(UUID id) {
        if (!channelMap.containsKey(id)) {
            throw new IllegalArgumentException("해당 ID의 채널이 존재하지 않습니다. ID: " + id);
        }
        return channelMap.get(id);
    }

    private void validateDuplicateName(String name) {
        boolean isDuplicate = channelMap.values().stream()
                .anyMatch(ch -> ch.getName().equals(name));
        if (isDuplicate) {
            throw new IllegalArgumentException("이미 존재하는 채널 이름입니다: " + name);
        }
    }

    @Override
    public Channel createChannel(String name) {
        // [검증] 채널 이름 필수
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("채널 이름은 필수입니다.");
        }

        // [비즈니스 로직] 같은 이름의 채널 중복 생성 방지 (선택 사항)
        boolean isDuplicate = channelMap.values().stream()
                .anyMatch(ch -> ch.getName().equals(name));

        if (isDuplicate) {
            throw new IllegalArgumentException("이미 존재하는 채널 이름입니다: " + name);
        }

        // [생성 및 저장]
        Channel channel = new Channel(name);
        channelMap.put(channel.getId(), channel);

        return Channel;
    }

    @Override
    public Optional<Channel> findOne(UUID id) {
        return Optional.ofNullable(channelMap.get(id));
    }

    @Override
    public List<Channel> findAll() {
        // [방어적 복사] 원본 Map 보호를 위해 새 리스트에 담아 반환
        return new ArrayList<>(channelMap.values());
    }

    @Override
    public Channel updateChannel(UUID id, String newName) {
        // 1. 대상 조회
        Channel channel = findOne(id)
                .orElseThrow(() -> new IllegalArgumentException("수정할 채널을 찾을 수 없습니다."));

        // 2. 유효성 검사 (빈 이름 불가)
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("변경할 채널 이름이 비어있습니다.");
        }

        // 3. 중복 이름 체크 (자기 자신 제외)
        boolean isDuplicate = channelMap.values().stream()
                .anyMatch(ch -> !ch.getId().equals(id) && ch.getName().equals(newName));

        if (isDuplicate) {
            throw new IllegalArgumentException("이미 존재하는 채널 이름입니다.");
        }

        // 4. 업데이트 (엔티티의 메소드 호출 -> 내부에서 updatedAt 갱신됨)
        channel.updateName(newName);

        return channel;
    }

    @Override
    public void deleteChannel(UUID id) {
        if (!channelMap.containsKey(id)) {
            throw new IllegalArgumentException("삭제할 채널이 존재하지 않습니다.");
        }

        channelMap.remove(id);
    }
}