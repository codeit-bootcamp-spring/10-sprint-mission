package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileChannelRepository implements ChannelRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    private final ReadStatusRepository readStatusRepository;

    public FileChannelRepository(
            @Value("${discodeit.repository.file-directory:.discodeit}") String directoryName,
            ReadStatusRepository readStatusRepository) {
        DIRECTORY = Paths.get(System.getProperty("user.dir"), directoryName, Channel.class.getSimpleName());
        this.readStatusRepository = readStatusRepository;
        if(Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException("디렉토리 생성 실패: " + DIRECTORY, e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id + EXTENSION);
    }

    @Override
    public Channel save(Channel channel) {
        Path path = resolvePath(channel.getId());
        try (
                FileOutputStream fos = new FileOutputStream(path.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException("채널 저장 실패: " + channel.getId(), e);
        }
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Channel channelNullable = null;
        Path path = resolvePath(id);

        if (Files.exists(path)) {
            try (
                    FileInputStream fis = new FileInputStream(path.toFile());
                    ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
                channelNullable = (Channel) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException("채널 읽기 실패: " + id, e);
            }
        }
        return Optional.ofNullable(channelNullable);
    }

    @Override
    public List<Channel> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            return (Channel) ois.readObject();
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("파일 읽기 중 오류 발생: " + path, e);
                        }
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("채널 목록 조회 실패", e);
        }
    }

    @Override
    public boolean existsById(UUID id) {
        Path path = resolvePath(id);
        return Files.exists(path);
    }

    @Override
    public void deleteById(UUID id) {
        Path path = resolvePath(id);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("채널 삭제 실패: " + id, e);
        }
    }

    @Override
    public boolean existsByName(String channelName) {
        return findAll().stream()
                .anyMatch(ch -> channelName.equals(ch.getName())); // 순서 변경 (NPE 방지)
    }

    @Override
    public Optional<Channel> findPrivateChannelByParticipants(Set<UUID> participantIds) {
        // 중요: 파일 I/O 성능 이슈 방지를 위해 ReadStatus 전체 목록을 한 번만 로드합니다.
        // 루프 안에서 readStatusRepository.findAll()을 호출하면 끔찍하게 느려집니다.
        List<ReadStatus> allReadStatuses = readStatusRepository.findAll();

        return findAll().stream()
                .filter(channel -> channel.getType() == ChannelType.PRIVATE)
                .filter(channel -> {
                    Set<UUID> currentParticipants = allReadStatuses.stream()
                            .filter(rs -> rs.getChannelId().equals(channel.getId()))
                            .map(ReadStatus::getUserId)
                            .collect(Collectors.toSet());
                    return currentParticipants.equals(participantIds);
                })
                .findFirst();
    }
}
