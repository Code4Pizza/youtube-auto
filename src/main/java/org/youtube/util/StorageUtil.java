package org.youtube.util;

import org.jdbi.v3.core.Jdbi;
import org.youtube.configuration.CircuitBreakerConfiguration;
import org.youtube.storage.FaultTolerantDatabase;
import org.youtube.storage.YoutubeDatabases;

import static org.youtube.util.Constants.*;
import static org.youtube.util.Constants.DB_NAME;

public class StorageUtil {

    public static YoutubeDatabases getAccDatabase() {
        Jdbi jdbi = Jdbi.create(BASE_URL, USER_NAME, PASSWORD);
        FaultTolerantDatabase accDatabase = new FaultTolerantDatabase(DB_NAME, jdbi, new CircuitBreakerConfiguration());
        return new YoutubeDatabases(accDatabase);
    }
}
