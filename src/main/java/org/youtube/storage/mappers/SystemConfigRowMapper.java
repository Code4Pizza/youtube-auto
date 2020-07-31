package org.youtube.storage.mappers;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.youtube.entities.SystemConfig;
import org.youtube.entities.YoutubeChannel;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SystemConfigRowMapper implements RowMapper<SystemConfig> {

  @Override
  public SystemConfig map(ResultSet resultSet, StatementContext ctx) throws SQLException {
    return new SystemConfig(resultSet.getString("key_value"),
            resultSet.getString("key_name"),
            resultSet.getString("key_description"));
  }
}
