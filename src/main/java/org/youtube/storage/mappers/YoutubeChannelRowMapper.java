package org.youtube.storage.mappers;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.youtube.entities.YoutubeAccount;
import org.youtube.entities.YoutubeChannel;

import java.sql.ResultSet;
import java.sql.SQLException;

public class YoutubeChannelRowMapper implements RowMapper<YoutubeChannel> {

  @Override
  public YoutubeChannel map(ResultSet resultSet, StatementContext ctx) throws SQLException {
    return new YoutubeChannel(resultSet.getInt("id"),
            resultSet.getString("channel_email"),
            resultSet.getString("channel_name"));
  }
}
