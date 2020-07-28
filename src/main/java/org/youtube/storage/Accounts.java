/*
 * Copyright (C) 2013 Open WhisperSystems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.youtube.storage;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.jdbi.v3.core.transaction.TransactionIsolationLevel;
import org.youtube.entities.YoutubeAccount;
import org.youtube.storage.mappers.YoutubeAccountRowMapper;
import org.youtube.util.Constants;
import org.youtube.util.SystemMapper;

import java.util.List;

import static com.codahale.metrics.MetricRegistry.name;

public class Accounts {

    public static final String SQL_INSERT = "INSERT INTO yt_bot.youtube_accounts" +
            "(email, password, backup_email)" +
            "VALUES(:email, :password, :backup_email);";

    private static final ObjectMapper mapper = SystemMapper.getMapper();

    private final MetricRegistry metricRegistry = SharedMetricRegistries.getOrCreate(Constants.METRICS_NAME);

    private final Timer getAllAccountsTimer = metricRegistry.timer(name(Accounts.class, "getAllAccounts"));

    private final Timer insertsAccountsTimer = metricRegistry.timer(name(Accounts.class, "insertAccounts"));

    private final FaultTolerantDatabase database;

    public Accounts(FaultTolerantDatabase database) {
        this.database = database;
        this.database.getDatabase().registerRowMapper(new YoutubeAccountRowMapper());
    }

    public List<YoutubeAccount> getAllAccounts() {
        String sql = "SELECT * from yt_bot.youtube_accounts where enable = 0";

        return database.with(jdbi -> jdbi.withHandle(handle -> {
            try (Timer.Context ignored = getAllAccountsTimer.time()) {
                return handle.createQuery(sql)
                        .mapTo(YoutubeAccount.class)
                        .list();
            }
        }));
    }

    public boolean insertAccount(YoutubeAccount account) {
        return database.with(jdbi -> jdbi.withHandle(handle -> {
            try (Timer.Context ignored = getAllAccountsTimer.time()) {
                return handle.createUpdate(SQL_INSERT)
                        .bind("email", account.getEmail())
                        .bind("password", account.getPassword())
                        .bind("backup_email", account.getBackupEmail())
                        .bind("enable", account.getEnable())
                        .execute() > 0;
            }
        }));
    }

    public void bulkInsertAccounts(List<YoutubeAccount> accounts) {
        database.use(jdbi -> jdbi.useTransaction(TransactionIsolationLevel.SERIALIZABLE, handle -> {
            try (Timer.Context ignored = insertsAccountsTimer.time()) {
                PreparedBatch preparedBatch = handle.prepareBatch(SQL_INSERT);

                for (YoutubeAccount account : accounts) {
                    preparedBatch
                            .bind("email", account.getEmail())
                            .bind("password", account.getPassword())
                            .bind("backup_email", account.getBackupEmail())
                            .bind("enable", account.getEnable())
                            .add();
                }

                if (preparedBatch.size() > 0) {
                    preparedBatch.execute();
                }
            }
        }));
    }
}
