package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelRole;
import com.sprint.mission.discodeit.entity.ChannelUserRole;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ChannelUserRoleService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileChannelUserRoleService implements ChannelUserRoleService {
    private Map<UUID, ChannelUserRole> channelUserRoleDB = new HashMap<>();
    private final File file;

    private final UserService userService;
    private final ChannelService channelService;

    public FileChannelUserRoleService(UserService userservice, ChannelService channelService) {
        this.userService = userservice;
        this.channelService = channelService;

        File dataDir = new File("discodeit.data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        this.file = new File(dataDir, "channel_user_roles.ser");

        if (file.exists()) {
            load();
        } else {
            persist();
        }
    }

    @SuppressWarnings("unchecked")
    private void load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            this.channelUserRoleDB = (Map<UUID, ChannelUserRole>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("관계 데이터 로드 중 오류: " + e.getMessage());
            this.channelUserRoleDB = new HashMap<>();
        }
    }

    private void persist() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this.channelUserRoleDB);
        } catch (IOException e) {
            System.err.println("관계 데이터 저장 중 오류: " + e.getMessage());
        }
    }

    @Override
    public ChannelUserRole addChannelUser(UUID channelId, UUID userId, ChannelRole role) {
        Channel channel = channelService.findChannelById(channelId);
        User user = userService.findUserByUserId(userId);

        boolean isAlreadyJoined = channelUserRoleDB.values().stream()
                .anyMatch(cur -> cur.getChannel().getId().equals(channelId)
                    && cur.getUser().getId().equals(userId));
        if (isAlreadyJoined) {
            throw new IllegalArgumentException("이미 채널에 참여 중인 사용자입니다.");
        }

        ChannelUserRole channelUserRole = new ChannelUserRole(channel, user, role);
        channelUserRoleDB.put(channelUserRole.getId(), channelUserRole);

        // 메모리 객체 동기화 (File 방식의 한계로 인해 완벽하지 않을 수 있음)
        user.addChannelUserRole(channelUserRole);
        channel.addChannelUserRole(channelUserRole);

        persist();
        return channelUserRole;
    }

    @Override
    public List<User> findUsersByChannelId(UUID channelId) {
        channelService.findChannelById(channelId); // 채널 존재 여부 확인

        // 관계 테이블을 기준으로 조회해야 정확함
        return channelUserRoleDB.values().stream()
                .filter(cur -> cur.getChannel().getId().equals(channelId))
                .map(ChannelUserRole::getUser)
                .collect(Collectors.toList());
    }

    @Override
    public ChannelUserRole findChannelUser(UUID channelId, UUID userId) {
        return channelUserRoleDB.values().stream()
                .filter(cur -> cur.getChannel().getId().equals(channelId)
                    && cur.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 채널에 참여하지 않은 사용자입니다. (채널-유저 관계 없음)"));
    }
    @Override
    public List<Channel> findChannelsByUserId(UUID userId) {
        userService.findUserByUserId(userId);

        return channelUserRoleDB.values().stream()
                .filter(cur -> cur.getUser().getId().equals(userId))
                .map(ChannelUserRole::getChannel)
                .collect(Collectors.toList());
    }

    @Override
    public ChannelUserRole updateChannelRole(UUID channelId, UUID userId, ChannelRole newRole) {
        ChannelUserRole channelUserRole = findChannelUser(channelId, userId);

        channelUserRole.updateRole(newRole);

        persist();
        System.out.println("권한 수정 완료되었습니다.");
        return channelUserRole;
    }

    // === Delete ===
    @Override
    public void deleteChannelUserAssociation(UUID channelId, UUID userId) {
        ChannelUserRole channelUserRole = findChannelUser(channelId, userId);

        try {
            channelUserRole.getChannel().removeChannelUserRole(channelUserRole);
            channelUserRole.getUser().removeChannelUserRole(channelUserRole);
        } catch (Exception e) {
            // 이미 로드된 객체와 달라 실패할 수 있음 // TODO: 정리 필요
        }

        channelUserRoleDB.remove(channelUserRole.getId());

        persist();
        System.out.println("채널 탈퇴 완료되었습니다.");
    }
    @Override
    public void deleteAllAssociationsByUserId(UUID userId) {
        List<ChannelUserRole> rolesToDelete = channelUserRoleDB.values().stream()
                .filter(role -> role.getUser().getId().equals(userId))
                .toList();

        // 메모리 상의 채널 객체에서 관계 제거
        for (ChannelUserRole role : rolesToDelete) {
            try {
                role.getChannel().removeChannelUserRole(role);
            } catch (Exception e) {
                // 무시
            }
        }

        channelUserRoleDB.values().removeAll(rolesToDelete);
        persist();
        System.out.println("해당 유저의 채널-유저 관계 삭제 완료 되었습니다.");
    }
    @Override
    public void deleteAllAssociationsByChannelId(UUID channelId) {
        List<ChannelUserRole> rolesToDelete = channelUserRoleDB.values().stream()
                .filter(role -> role.getChannel().getId().equals(channelId))
                .toList();

        for (ChannelUserRole role : rolesToDelete) {
            try {
                role.getUser().removeChannelUserRole(role);
            } catch (Exception e) {
                // 무시
            }
        }

        channelUserRoleDB.values().removeAll(rolesToDelete);

        persist();
        System.out.println("채널 내 모든 참여자 관계 삭제 완료. (File)");
    }
}
