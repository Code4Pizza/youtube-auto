package org.youtube.storage.mappers;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.youtube.entities.ClickLikeModel;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClickLikeModelRowMapper implements RowMapper<ClickLikeModel> {

    @Override
    public ClickLikeModel map(ResultSet resultSet, StatementContext ctx) throws SQLException {
        return new ClickLikeModel(resultSet.getInt("id"),
                resultSet.getString("user_id"),
                resultSet.getString("video_url"),
                resultSet.getDate("click_time"));
    }
}
