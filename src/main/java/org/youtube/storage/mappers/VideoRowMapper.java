package org.youtube.storage.mappers;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.youtube.entities.ChannelVideo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VideoRowMapper implements RowMapper<ChannelVideo> {

  @Override
  public ChannelVideo map(ResultSet resultSet, StatementContext ctx) throws SQLException {
    return new ChannelVideo(resultSet.getInt("id"),
            resultSet.getInt("channel_id"),
            resultSet.getString("video_url"),
            resultSet.getInt("duration"));
  }
}
