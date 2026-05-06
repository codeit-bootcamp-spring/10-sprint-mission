package com.codeit.mission.deokhugam.dashboard.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.codeit.mission.deokhugam.dashboard.DirectionEnum;
import com.codeit.mission.deokhugam.dashboard.DomainType;
import com.codeit.mission.deokhugam.dashboard.PeriodType;
import com.codeit.mission.deokhugam.dashboard.StagingType;
import com.codeit.mission.deokhugam.dashboard.popularbooks.dto.response.CursorPageResponsePopularBookDto;
import com.codeit.mission.deokhugam.dashboard.popularbooks.dto.response.PopularBookDto;
import com.codeit.mission.deokhugam.dashboard.popularbooks.repository.PopularBookRepository;
import com.codeit.mission.deokhugam.dashboard.popularbooks.service.PopularBookService;
import com.codeit.mission.deokhugam.dashboard.popularreviews.dto.response.CursorPageResponsePopularReviewDto;
import com.codeit.mission.deokhugam.dashboard.popularreviews.dto.response.PopularReviewDto;
import com.codeit.mission.deokhugam.dashboard.popularreviews.repository.PopularReviewRepository;
import com.codeit.mission.deokhugam.dashboard.popularreviews.service.PopularReviewService;
import com.codeit.mission.deokhugam.dashboard.powerusers.dto.response.CursorPageResponsePowerUserDto;
import com.codeit.mission.deokhugam.dashboard.powerusers.dto.response.PowerUserDto;
import com.codeit.mission.deokhugam.dashboard.powerusers.repository.PowerUserRepository;
import com.codeit.mission.deokhugam.dashboard.powerusers.service.PowerUserService;
import com.codeit.mission.deokhugam.dashboard.snapshot.AggregateSnapshot;
import com.codeit.mission.deokhugam.dashboard.snapshot.AggregateSnapshotRepository;
import com.codeit.mission.deokhugam.dashboard.snapshot.AggregateSnapshotService;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

@SpringJUnitConfig(DashboardCacheTest.TestConfig.class)
class DashboardCacheTest {

  private static final UUID SNAPSHOT_ID =
      UUID.fromString("11111111-1111-1111-1111-111111111111");
  private static final Instant CREATED_AT = Instant.parse("2026-04-27T14:30:00Z");

  private final PopularBookService popularBookService;
  private final PopularReviewService popularReviewService;
  private final PowerUserService powerUserService;
  private final AggregateSnapshotService aggregateSnapshotService;
  private final PopularBookRepository popularBookRepository;
  private final PopularReviewRepository popularReviewRepository;
  private final PowerUserRepository powerUserRepository;
  private final AggregateSnapshotRepository snapshotRepository;
  private final CacheManager cacheManager;

  @Autowired
  DashboardCacheTest(
      PopularBookService popularBookService,
      PopularReviewService popularReviewService,
      PowerUserService powerUserService,
      AggregateSnapshotService aggregateSnapshotService,
      PopularBookRepository popularBookRepository,
      PopularReviewRepository popularReviewRepository,
      PowerUserRepository powerUserRepository,
      AggregateSnapshotRepository snapshotRepository,
      CacheManager cacheManager
  ) {
    this.popularBookService = popularBookService;
    this.popularReviewService = popularReviewService;
    this.powerUserService = powerUserService;
    this.aggregateSnapshotService = aggregateSnapshotService;
    this.popularBookRepository = popularBookRepository;
    this.popularReviewRepository = popularReviewRepository;
    this.powerUserRepository = powerUserRepository;
    this.snapshotRepository = snapshotRepository;
    this.cacheManager = cacheManager;
  }

  @BeforeEach
  void setUp() {
    reset(popularBookRepository, popularReviewRepository, powerUserRepository, snapshotRepository);
    cacheManager.getCache("popularBooks").clear();
    cacheManager.getCache("popularReviews").clear();
    cacheManager.getCache("powerUsers").clear();
  }

  @Test
  @DisplayName("popularBooks cache uses cached result for identical request parameters")
  void popularBooksCacheHit() {
    PopularBookDto book = new PopularBookDto(
        UUID.randomUUID(),
        UUID.randomUUID(),
        "book",
        "author",
        PeriodType.WEEKLY,
        1L,
        10.0,
        2L,
        4.5,
        CREATED_AT);

    when(snapshotRepository.findTopByDomainTypeAndPeriodTypeAndStagingTypeOrderByCreatedAtDesc(
        DomainType.POPULAR_BOOK, PeriodType.WEEKLY, StagingType.PUBLISHED))
        .thenReturn(Optional.of(publishedSnapshot(DomainType.POPULAR_BOOK)));
    when(popularBookRepository.findRankingDtosBySnapshotIdDesc(
        eq(SNAPSHOT_ID), eq(null), eq(null), eq(PageRequest.of(0, 11))))
        .thenReturn(List.of(book));
    when(popularBookRepository.countRankingsBySnapshotId(SNAPSHOT_ID)).thenReturn(1L);

    CursorPageResponsePopularBookDto first =
        popularBookService.get(PeriodType.WEEKLY, DirectionEnum.DESC, null, null, 10);
    CursorPageResponsePopularBookDto second =
        popularBookService.get(PeriodType.WEEKLY, DirectionEnum.DESC, null, null, 10);

    assertThat(second).isEqualTo(first);
    verify(popularBookRepository, times(1))
        .findRankingDtosBySnapshotIdDesc(SNAPSHOT_ID, null, null, PageRequest.of(0, 11));
    verify(popularBookRepository, times(1)).countRankingsBySnapshotId(SNAPSHOT_ID);
  }

  @Test
  @DisplayName("popularReviews cache uses cached result for identical request parameters")
  void popularReviewsCacheHit() {
    PopularReviewDto review = new PopularReviewDto(
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        "book",
        "thumbnail",
        UUID.randomUUID(),
        "user",
        "content",
        4.0,
        PeriodType.WEEKLY,
        CREATED_AT,
        1L,
        10.0,
        3L,
        2L);

    when(snapshotRepository.findTopByDomainTypeAndPeriodTypeAndStagingTypeOrderByCreatedAtDesc(
        DomainType.POPULAR_REVIEW, PeriodType.WEEKLY, StagingType.PUBLISHED))
        .thenReturn(Optional.of(publishedSnapshot(DomainType.POPULAR_REVIEW)));
    when(popularReviewRepository.findRankingDtosBySnapshotIdDesc(
        eq(SNAPSHOT_ID), eq(null), eq(null), eq(PageRequest.of(0, 11))))
        .thenReturn(List.of(review));
    when(popularReviewRepository.countRankingsBySnapshotId(SNAPSHOT_ID)).thenReturn(1L);

    CursorPageResponsePopularReviewDto first =
        popularReviewService.getReviews(PeriodType.WEEKLY, DirectionEnum.DESC, null, null, 10);
    CursorPageResponsePopularReviewDto second =
        popularReviewService.getReviews(PeriodType.WEEKLY, DirectionEnum.DESC, null, null, 10);

    assertThat(second).isEqualTo(first);
    verify(popularReviewRepository, times(1))
        .findRankingDtosBySnapshotIdDesc(SNAPSHOT_ID, null, null, PageRequest.of(0, 11));
    verify(popularReviewRepository, times(1)).countRankingsBySnapshotId(SNAPSHOT_ID);
  }

  @Test
  @DisplayName("powerUsers cache uses cached result for identical request parameters")
  void powerUsersCacheHit() {
    PowerUserDto user = new PowerUserDto(
        UUID.randomUUID(),
        "user",
        PeriodType.WEEKLY,
        CREATED_AT,
        1L,
        10.0,
        8.0,
        3L,
        2L);

    when(snapshotRepository.findTopByDomainTypeAndPeriodTypeAndStagingTypeOrderByCreatedAtDesc(
        DomainType.POWER_USER, PeriodType.WEEKLY, StagingType.PUBLISHED))
        .thenReturn(Optional.of(publishedSnapshot(DomainType.POWER_USER)));
    when(powerUserRepository.findRankingDtosBySnapshotIdDesc(
        eq(SNAPSHOT_ID), eq(null), eq(null), eq(PageRequest.of(0, 11))))
        .thenReturn(List.of(user));
    when(powerUserRepository.countRankingsBySnapshotId(SNAPSHOT_ID)).thenReturn(1L);

    CursorPageResponsePowerUserDto first =
        powerUserService.getLatestRankings(PeriodType.WEEKLY, DirectionEnum.DESC, null, null, 10);
    CursorPageResponsePowerUserDto second =
        powerUserService.getLatestRankings(PeriodType.WEEKLY, DirectionEnum.DESC, null, null, 10);

    assertThat(second).isEqualTo(first);
    verify(powerUserRepository, times(1))
        .findRankingDtosBySnapshotIdDesc(SNAPSHOT_ID, null, null, PageRequest.of(0, 11));
    verify(powerUserRepository, times(1)).countRankingsBySnapshotId(SNAPSHOT_ID);
  }

  @Test
  @DisplayName("publishSnapshot clears only the cache for the published domain after commit")
  void publishSnapshotEvictsDomainCacheAfterCommit() {
    AggregateSnapshot snapshot = stagingSnapshot(DomainType.POPULAR_BOOK);
    cacheManager.getCache("popularBooks").put("cached-key", "cached-value");
    cacheManager.getCache("popularReviews").put("cached-key", "cached-value");
    cacheManager.getCache("powerUsers").put("cached-key", "cached-value");

    when(snapshotRepository.findBySnapshotId(SNAPSHOT_ID)).thenReturn(Optional.of(snapshot));
    when(snapshotRepository.findTopByDomainTypeAndPeriodTypeAndStagingTypeOrderByCreatedAtDesc(
        DomainType.POPULAR_BOOK, PeriodType.WEEKLY, StagingType.PUBLISHED))
        .thenReturn(Optional.empty());

    aggregateSnapshotService.publishSnapshot(DomainType.POPULAR_BOOK, SNAPSHOT_ID);

    assertThat(snapshot.getStagingType()).isEqualTo(StagingType.PUBLISHED);
    assertThat(cacheManager.getCache("popularBooks").get("cached-key")).isNull();
    assertThat(cacheManager.getCache("popularReviews").get("cached-key")).isNotNull();
    assertThat(cacheManager.getCache("powerUsers").get("cached-key")).isNotNull();
  }

  @Test
  @DisplayName("different cache keys are used when request parameters differ")
  void differentParametersUseDifferentCacheEntries() {
    when(snapshotRepository.findTopByDomainTypeAndPeriodTypeAndStagingTypeOrderByCreatedAtDesc(
        DomainType.POPULAR_BOOK, PeriodType.WEEKLY, StagingType.PUBLISHED))
        .thenReturn(Optional.of(publishedSnapshot(DomainType.POPULAR_BOOK)));
    when(popularBookRepository.findRankingDtosBySnapshotIdDesc(
        eq(SNAPSHOT_ID), eq(null), eq(null), any()))
        .thenReturn(List.of());
    when(popularBookRepository.countRankingsBySnapshotId(SNAPSHOT_ID)).thenReturn(0L);

    popularBookService.get(PeriodType.WEEKLY, DirectionEnum.DESC, null, null, 10);
    popularBookService.get(PeriodType.WEEKLY, DirectionEnum.DESC, null, null, 20);

    verify(popularBookRepository, times(1))
        .findRankingDtosBySnapshotIdDesc(SNAPSHOT_ID, null, null, PageRequest.of(0, 11));
    verify(popularBookRepository, times(1))
        .findRankingDtosBySnapshotIdDesc(SNAPSHOT_ID, null, null, PageRequest.of(0, 21));
  }

  private AggregateSnapshot publishedSnapshot(DomainType domainType) {
    return AggregateSnapshot.builder()
        .snapshotId(SNAPSHOT_ID)
        .periodType(PeriodType.WEEKLY)
        .domainType(domainType)
        .aggregatedAt(CREATED_AT)
        .stagingType(StagingType.PUBLISHED)
        .build();
  }

  private AggregateSnapshot stagingSnapshot(DomainType domainType) {
    return AggregateSnapshot.builder()
        .snapshotId(SNAPSHOT_ID)
        .periodType(PeriodType.WEEKLY)
        .domainType(domainType)
        .aggregatedAt(CREATED_AT)
        .stagingType(StagingType.STAGING)
        .build();
  }

  @Configuration
  @EnableCaching
  @EnableTransactionManagement
  static class TestConfig {

    @Bean
    CacheManager cacheManager() {
      return new ConcurrentMapCacheManager("popularBooks", "popularReviews", "powerUsers");
    }

    @Bean
    PlatformTransactionManager transactionManager() {
      return new TestTransactionManager();
    }

    @Bean
    PopularBookRepository popularBookRepository() {
      return mock(PopularBookRepository.class);
    }

    @Bean
    PopularReviewRepository popularReviewRepository() {
      return mock(PopularReviewRepository.class);
    }

    @Bean
    PowerUserRepository powerUserRepository() {
      return mock(PowerUserRepository.class);
    }

    @Bean
    AggregateSnapshotRepository snapshotRepository() {
      return mock(AggregateSnapshotRepository.class);
    }

    @Bean
    PopularBookService popularBookService(
        PopularBookRepository popularBookRepository,
        AggregateSnapshotRepository snapshotRepository
    ) {
      return new PopularBookService(popularBookRepository, snapshotRepository);
    }

    @Bean
    PopularReviewService popularReviewService(
        PopularReviewRepository popularReviewRepository,
        AggregateSnapshotRepository snapshotRepository
    ) {
      return new PopularReviewService(popularReviewRepository, snapshotRepository);
    }

    @Bean
    PowerUserService powerUserService(
        PowerUserRepository powerUserRepository,
        AggregateSnapshotRepository snapshotRepository
    ) {
      return new PowerUserService(powerUserRepository, snapshotRepository);
    }

    @Bean
    AggregateSnapshotService aggregateSnapshotService(
        AggregateSnapshotRepository snapshotRepository,
        CacheManager cacheManager
    ) {
      return new AggregateSnapshotService(snapshotRepository, cacheManager);
    }
  }

  private static class TestTransactionManager extends AbstractPlatformTransactionManager {

    @Override
    protected Object doGetTransaction() throws TransactionException {
      return new Object();
    }

    @Override
    protected void doBegin(
        Object transaction,
        TransactionDefinition definition
    ) throws TransactionException {
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) throws TransactionException {
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) throws TransactionException {
    }

    @Override
    protected boolean isExistingTransaction(Object transaction) throws TransactionException {
      return false;
    }
  }
}
