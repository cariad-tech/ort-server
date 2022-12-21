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

package org.ossreviewtoolkit.server.workers.advisor

import com.typesafe.config.ConfigFactory

import org.ossreviewtoolkit.server.dao.connect
import org.ossreviewtoolkit.server.dao.createDataSource
import org.ossreviewtoolkit.server.dao.createDatabaseConfig

/**
 * This is the entry point of the Advisor worker. It calls the Advisor from ORT programmatically by
 * interfacing on its APIs.
 */
fun main() {
    val config = ConfigFactory.load()

    createDataSource(createDatabaseConfig(config)).connect()

    TODO("Implement AdvisorWorker.")
}
