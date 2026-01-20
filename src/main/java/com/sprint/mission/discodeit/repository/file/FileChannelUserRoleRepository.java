package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.ChannelUserRole;
import com.sprint.mission.discodeit.repository.ChannelUserRoleRepository;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileChannelUserRoleRepository implements ChannelUserRoleRepository {
    private Map<UUID, ChannelUserRole> channelUserRoleDB = new HashMap<>();
    private final File file = new File("channel_user_roles.dat");

    public FileChannelUserRoleRepository() {
        if (file.exists()) load(); else persist();
    }

    @SuppressWarnings("unchecked")
    private void load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            this.channelUserRoleDB = (Map<UUID, ChannelUserRole>) ois.readObject();
        } catch (Exception e) {
            System.err.println("ChannelUserRole 데이터 로드 실패: " + e.getMessage());
            this.channelUserRoleDB = new HashMap<>();
        }
    }

    private void persist() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this.channelUserRoleDB);
        } catch (IOException e) {
            System.err.println("ChannelUserRole 데이터 저장 실패: " + e.getMessage());
        }
    }

    @Override
    public ChannelUserRole save(ChannelUserRole role) {
        channelUserRoleDB.put(role.getId(), role);
        persist();
        return role;
    }

    @Override
    public Optional<ChannelUserRole> findById(UUID id) {
        return Optional.ofNullable(channelUserRoleDB.get(id));
    }

    @Override
    public Optional<ChannelUserRole> findByChannelIdAndUserId(UUID channelId, UUID userId) {
        return channelUserRoleDB.values().stream()
                .filter(cu -> cu.getChannel().getId().equals(channelId) && cu.getUser().getId().equals(userId))
                .findFirst();
    }

    @Override
    public List<ChannelUserRole> findAllByChannelId(UUID channelId) {
        return channelUserRoleDB.values().stream()
                .filter(cu -> cu.getChannel().getId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ChannelUserRole> findAllByUserId(UUID userId) {
        return channelUserRoleDB.values().stream()
                .filter(cu -> cu.getUser().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        channelUserRoleDB.remove(id);
        persist();
    }

    @Override
    public void deleteAllByUserId(UUID userId) {
        boolean changed = channelUserRoleDB.values().removeIf(role -> role.getUser().getId().equals(userId));
        if(changed) persist();
    }

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        boolean changed = channelUserRoleDB.values().removeIf(role -> role.getChannel().getId().equals(channelId));
        if(changed) persist();
    }

    @Override
    public boolean existsByChannelIdAndUserId(UUID channelId, UUID userId) {
        return channelUserRoleDB.values().stream()
                .anyMatch(cu -> cu.getChannel().getId().equals(channelId) && cu.getUser().getId().equals(userId));
    }
}