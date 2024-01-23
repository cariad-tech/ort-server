/*
 * Copyright (C) 2022 The ORT Project Authors (See <https://github.com/oss-review-toolkit/ort-server/blob/main/NOTICE>)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */

package org.ossreviewtoolkit.server.dao

import org.ossreviewtoolkit.server.config.ConfigManager
import org.ossreviewtoolkit.server.config.Path
import org.ossreviewtoolkit.server.utils.config.getStringOrNull

data class DatabaseConfig(
    /** The host of the database, for example 'localhost'. */
    val host: String,

    /** The port of the database, for example '5432'. */
    val port: Int,

    /** The name of the database, for example 'postgres'. */
    val name: String,

    /** The schema to use, for example 'public'. */
    val schema: String,

    /** The username used for connecting to the database. */
    val username: String,

    /** The password used for connecting to the database. */
    val password: String,

    /**
     * The maximum size of the connection pool. For details see the
     * [Hikari documentation](https://github.com/brettwooldridge/HikariCP#frequently-used).
     */
    val maximumPoolSize: Int,

    /**
     * The SSL mode to use. For available modes see the
     * [PostgreSQL documentation](https://www.postgresql.org/docs/current/libpq-ssl.html#LIBPQ-SSL-PROTECTION).
     */
    val sslMode: String,

    /**
     * The location of the file containing the SSL certificates. For details see the
     * [PostgreSQL documentation](https://www.postgresql.org/docs/current/libpq-ssl.html#LIBPQ-SSL-CLIENTCERT).
     */
    val sslCert: String?,

    /**
     * The location of the file containing the SSL keys. For details see the
     * [PostgreSQL documentation](https://www.postgresql.org/docs/current/libpq-ssl.html#LIBPQ-SSL-CLIENTCERT).
     */
    val sslKey: String?,

    /**
     * The location of the root certificate file. For details see the
     * [PostgreSQL documentation](https://www.postgresql.org/docs/current/libpq-ssl.html#LIBQ-SSL-CERTIFICATES).
     */
    val sslRootCert: String?,
) {
    companion object {
        /**
         * Create a [DatabaseConfig] object from the provided [config].
         */
        fun create(config: ConfigManager) = DatabaseConfig(
            host = config.getString("database.host"),
            port = config.getInt("database.port"),
            name = config.getString("database.name"),
            schema = config.getString("database.schema"),
            username = config.getSecret(Path("database.username")),
            password = config.getSecret(Path("database.password")),

            maximumPoolSize = config.getInt("database.maximumPoolSize"),

            sslMode = config.getString("database.sslMode"),
            sslCert = config.getStringOrNull("database.sslCert"),
            sslKey = config.getStringOrNull("database.sslKey"),
            sslRootCert = config.getStringOrNull("database.sslRootCert")
        )
    }
}
