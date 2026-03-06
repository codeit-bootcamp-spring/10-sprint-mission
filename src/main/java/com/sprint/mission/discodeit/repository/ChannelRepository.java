package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

    boolean existsByName(String name);

    Channel findByName(@NotBlank(message = "채널 이름은 필수입니다.") @NotNull String name);
}
