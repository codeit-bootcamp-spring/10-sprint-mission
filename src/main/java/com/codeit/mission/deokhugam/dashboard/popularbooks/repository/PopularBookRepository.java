package com.codeit.mission.deokhugam.dashboard.popularbooks.repository;

import com.codeit.mission.deokhugam.dashboard.PeriodType;
import com.codeit.mission.deokhugam.dashboard.popularbooks.dto.response.PopularBookDto;
import com.codeit.mission.deokhugam.dashboard.popularbooks.entity.PopularBook;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PopularBookRepository extends JpaRepository<PopularBook, UUID> {


  @Query("""
      select pb
          from PopularBook pb
              where pb.snapshotId = :snapshotId
          order by pb.score desc, pb.createdAt asc, pb.id asc
      """)
  List<PopularBook> findBySnapshotIdDescByScore(
      @Param("snapshotId") UUID snapshotId
  );

  @Query("""
      select new com.codeit.mission.deokhugam.dashboard.popularbooks.dto.response.PopularBookDto(
          pb.id,
          b.id,
          b.title,
          b.author,
          pb.periodType,
          pb.rank,
          pb.score,
          pb.reviewCount,
          pb.avgRating,
          pb.createdAt
      )
      from PopularBook pb
      join com.codeit.mission.deokhugam.book.entity.Book b on b.id = pb.bookId
      where pb.snapshotId = :snapshotId
        and (:cursor is null
              or pb.rank > :cursor
              or (pb.rank = :cursor and pb.createdAt > :after))
      order by pb.rank asc, pb.createdAt asc, pb.id asc
      """)
  List<PopularBookDto> findRankingDtosBySnapshotIdAsc(
      @Param("snapshotId") UUID snapshotId,
      @Param("cursor") Long cursor,
      @Param("after") Instant after,
      Pageable pageable);

  @Query("""
      select new com.codeit.mission.deokhugam.dashboard.popularbooks.dto.response.PopularBookDto(
          pb.id,
          b.id,
          b.title,
          b.author,
          pb.periodType,
          pb.rank,
          pb.score,
          pb.reviewCount,
          pb.avgRating,
          pb.createdAt
      )
      from PopularBook pb
      join com.codeit.mission.deokhugam.book.entity.Book b on b.id = pb.bookId
      where pb.snapshotId = :snapshotId
        and (:cursor is null
              or pb.rank < :cursor
              or (pb.rank = :cursor and pb.createdAt < :after))
      order by pb.rank desc, pb.createdAt desc, pb.id desc
      """)
  List<PopularBookDto> findRankingDtosBySnapshotIdDesc(
      @Param("snapshotId") UUID snapshotId,
      @Param("cursor") Long cursor,
      @Param("after") Instant after,
      Pageable pageable);

  @Query("""
      select count(pb.id)
      from PopularBook pb
      where pb.snapshotId = :snapshotId
      """)
  long countRankingsBySnapshotId(@Param("snapshotId") UUID snapshotId);

  @Modifying
  @Query("""
    DELETE FROM PopularBook pb
    WHERE pb.snapshotId IN :snapshotIds
    """)
  void deleteBySnapshotIdIn(@Param("snapshotIds") Collection<UUID> snapshotIds);


}
