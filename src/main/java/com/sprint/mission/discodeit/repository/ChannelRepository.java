package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChannelRepository extends JpaRepository<Channel, UUID> {

  List<Channel> findAllByType(ChannelType type);

  // PUBLIC 채널이거나, ReadStatus에 해당 유저 ID가 존재하는 채널을 중복 없이(DISTINCT) 조회
  @Query("SELECT DISTINCT c FROM Channel c LEFT JOIN ReadStatus rs ON c = rs.channel WHERE c.type = 'PUBLIC' OR rs.user.id = :userId")
  List<Channel> findAllByUserId(@Param("userId") UUID userId);
}
