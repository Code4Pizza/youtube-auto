package org.youtube.storage.mappers;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.youtube.entities.Banner;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BannerRowMapper implements RowMapper<Banner> {

  @Override
  public Banner map(ResultSet resultSet, StatementContext ctx) throws SQLException {
    return new Banner(resultSet.getString("banner_title"),
            resultSet.getDate("created_time"),
            resultSet.getDate("start_time"),
            resultSet.getDate("end_time"),
            resultSet.getInt("type"));
  }
}
