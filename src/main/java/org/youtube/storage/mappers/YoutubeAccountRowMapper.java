package org.youtube.storage.mappers;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.youtube.entities.YoutubeAccount;

import java.sql.ResultSet;
import java.sql.SQLException;

public class YoutubeAccountRowMapper implements RowMapper<YoutubeAccount> {

  @Override
  public YoutubeAccount map(ResultSet resultSet, StatementContext ctx) throws SQLException {
    return new YoutubeAccount(resultSet.getInt("id"),
            resultSet.getString("email"),
            resultSet.getString("password"),
            resultSet.getString("backup_email"));
  }
}
