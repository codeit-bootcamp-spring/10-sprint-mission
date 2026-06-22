package com.sprint.mission.discodeit.support.base;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.sprint.mission.discodeit.global.infra.storage.download.StorageDownloader;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Transactional
public abstract class BaseIntegrationTest {

  @Autowired
  protected EntityManager em;

  @MockitoBean
  protected StorageDownloader storageDownloader;

  protected void flushAndClear() {
    em.flush();
    em.clear();
  }
}
