package com.sprint.mission.discodeit.auth.repository;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.web.authentication.rememberme.PersistentRememberMeToken;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RememberMeTokenRepository implements PersistentTokenRepository {

  private final JdbcTemplate jdbcTemplate;

  @Override
  public void createNewToken(PersistentRememberMeToken token) {
    jdbcTemplate.update(
        "INSERT INTO persistent_logins (username, series, token, last_used) VALUES (CAST(? AS UUID), ?, ?, ?)",
        token.getUsername(), token.getSeries(), token.getTokenValue(), token.getDate()
    );
  }

  @Override
  public void updateToken(String series, String tokenValue, Date lastUsed) {
    jdbcTemplate.update(
        "UPDATE persistent_logins SET token = ?, last_used = ? WHERE series = ?",
        tokenValue, lastUsed, series
    );
  }

  @Override
  public PersistentRememberMeToken getTokenForSeries(String seriesId) {
    try {
      return jdbcTemplate.queryForObject(
          "SELECT CAST(username AS VARCHAR), series, token, last_used FROM persistent_logins WHERE series = ?",
          (rs, rowNum) -> new PersistentRememberMeToken(
              rs.getString(1),
              rs.getString(2),
              rs.getString(3),
              rs.getTimestamp(4)
          ),
          seriesId
      );
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  @Override
  public void removeUserTokens(String username) {
    jdbcTemplate.update(
        "DELETE FROM persistent_logins WHERE username = CAST(? AS UUID)",
        username
    );
  }
}
