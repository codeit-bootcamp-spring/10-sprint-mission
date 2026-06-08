package com.sprint.mission.discodeit.support.inspector;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.engine.jdbc.internal.FormatStyle;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class QueryInspector implements StatementInspector {

  private final ThreadLocal<Integer> count = ThreadLocal.withInitial(() -> 0);
  private final ThreadLocal<List<String>> queries = ThreadLocal.withInitial(ArrayList::new);

  @Override
  public String inspect(String sql) {
    count.set(count.get() + 1);
    queries.get().add(sql);
    return sql;
  }

  public int getCount() {
    return count.get();
  }

  public void clear() {
    count.remove();
    queries.remove();
  }

  public List<String> getQueries() {
    return queries.get();
  }

  public void logQueries() {
    List<String> collectedQueries = queries.get();
    if (collectedQueries.isEmpty()) {
      log.info("실행된 쿼리가 없습니다.");
      return;
    }

    log.info("========== 총 실행 쿼리 수: {} ==========", count.get());
    for (int i = 0; i < collectedQueries.size(); i++) {
      // 💡 3. Hibernate 내부 포매터를 이용해 SQL을 들여쓰기/줄바꿈 처리 (가장 핵심!)
      String formattedSql = FormatStyle.BASIC.getFormatter().format(collectedQueries.get(i));
      log.info("Query [{}]: {}", i + 1, formattedSql);
    }
    log.info("=========================================");
  }
}
