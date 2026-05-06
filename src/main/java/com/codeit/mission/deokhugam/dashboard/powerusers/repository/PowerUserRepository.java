package com.codeit.mission.deokhugam.dashboard.powerusers.repository;

import com.codeit.mission.deokhugam.dashboard.powerusers.dto.response.PowerUserDto;
import com.codeit.mission.deokhugam.dashboard.powerusers.entity.PowerUser;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PowerUserRepository extends JpaRepository<PowerUser, UUID> {

  @Query(
      """
          select new com.codeit.mission.deokhugam.dashboard.powerusers.dto.response.PowerUserDto(
              pu.userId,
              u.nickname,
              pu.periodType,
              pu.createdAt,
              pu.rank,
              pu.score,
              pu.reviewScoreSum,
              pu.likeCount,
              pu.commentCount
          )
          from PowerUser pu
          join com.codeit.mission.deokhugam.user.entity.User u on u.id = pu.userId
          where pu.snapshotId = :snapshotId
            and (:cursor is null
                  or pu.rank > :cursor
                  or (pu.rank = :cursor and pu.createdAt > :after))
          order by pu.rank asc, pu.createdAt asc
          """)
  List<PowerUserDto> findRankingDtosBySnapshotIdAsc(
      @Param("snapshotId") UUID snapshotId,
      @Param("cursor") Long cursor,
      @Param("after") Instant after,
      Pageable pageable);

  @Query(
      """
          select new com.codeit.mission.deokhugam.dashboard.powerusers.dto.response.PowerUserDto(
              pu.userId,
              u.nickname,
              pu.periodType,
              pu.createdAt,
              pu.rank,
              pu.score,
              pu.reviewScoreSum,
              pu.likeCount,
              pu.commentCount
          )
          from PowerUser pu
          join com.codeit.mission.deokhugam.user.entity.User u on u.id = pu.userId
          where pu.snapshotId = :snapshotId
            and (:cursor is null
                  or pu.rank < :cursor
                  or (pu.rank = :cursor and pu.createdAt < :after))
          order by pu.rank desc, pu.createdAt desc
          """)
  List<PowerUserDto> findRankingDtosBySnapshotIdDesc(
      @Param("snapshotId") UUID snapshotId,
      @Param("cursor") Long cursor,
      @Param("after") Instant after,
      Pageable pageable);

  @Query(
      """
          select count(pu.id)
          from PowerUser pu
          where pu.snapshotId = :snapshotId
          """)
  long countRankingsBySnapshotId(@Param("snapshotId") UUID snapshotId);

  @Query(
      """
          select pu
          from PowerUser pu
          where pu.snapshotId = :snapshotId
          order by pu.score desc, pu.createdAt asc
          """)
  List<PowerUser> findBySnapshotIdDescByScore(
      @Param("snapshotId") UUID snapshotId);

  @Modifying
  @Query(
      """
        delete from PowerUser pu
        where pu.snapshotId in :snapshotIds
""")
  void deleteBySnapshotIdIn(@Param("snapshotIds") Collection<UUID> snapshotIds);
}
