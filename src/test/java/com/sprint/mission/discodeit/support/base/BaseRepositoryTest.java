package com.sprint.mission.discodeit.support.base;

import com.sprint.mission.discodeit.support.config.HibernateConfig;
import com.sprint.mission.discodeit.global.config.qdsl.QueryDslConfig;
import com.sprint.mission.discodeit.support.generator.TestEntity;
import com.sprint.mission.discodeit.support.inspector.QueryInspector;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Import({QueryDslConfig.class, HibernateConfig.class,
    QueryInspector.class, TestEntity.class})
@DataJpaTest(properties = {
    "spring.jpa.show-sql=false",
    "spring.jpa.properties.hibernate.show_sql=false"
})
@EnableJpaAuditing
@AutoConfigureTestDatabase(replace = Replace.NONE)
public abstract class BaseRepositoryTest {

  @Autowired
  protected EntityManager em;
  @Autowired
  protected QueryInspector queryInspector;

  protected void flushAndClear() {
    em.flush();
    em.clear();
  }

  protected void clear() {
    em.clear();
    queryInspector.clear();
  }

  protected void ensureQueryCount(int count) {
    Assertions.assertThat(queryInspector.getCount()).isEqualTo(count);
  }

  protected boolean compareInstant(Instant a, Instant b) {
    if (a == null && b == null) {
      return true;
    }
    if (a == null || b == null) {
      return false;
    }
    return toTruncated(a).equals(toTruncated(b));
  }

  private Instant toTruncated(Instant time) {
    return time.truncatedTo(ChronoUnit.MILLIS);
  }
}
