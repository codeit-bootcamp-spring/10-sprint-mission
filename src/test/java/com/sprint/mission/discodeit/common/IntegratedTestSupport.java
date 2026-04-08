package com.sprint.mission.discodeit.common;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public abstract class IntegratedTestSupport {

  @Autowired
  protected EntityManager em;

  protected void flushAndClear() {
    em.flush();
    em.clear();
  }
}
