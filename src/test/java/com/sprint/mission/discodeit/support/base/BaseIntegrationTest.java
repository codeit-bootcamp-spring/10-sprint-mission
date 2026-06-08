package com.sprint.mission.discodeit.support.base;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
public abstract class BaseIntegrationTest {

  @Autowired
  protected EntityManager em;

  protected void flushAndClear() {
    em.flush();
    em.clear();
  }
}
