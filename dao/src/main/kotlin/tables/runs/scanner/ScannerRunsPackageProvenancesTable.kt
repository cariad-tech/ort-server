/*
 * Copyright (C) 2023 The ORT Project Authors (See <https://github.com/oss-review-toolkit/ort-server/blob/main/NOTICE>)
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

package org.ossreviewtoolkit.server.dao.tables.runs.scanner

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

import org.ossreviewtoolkit.server.dao.tables.provenance.PackageProvenancesTable

/**
 * A junction table to link [ScannerRunsTable] with [PackageProvenancesTable].
 */
object ScannerRunsPackageProvenancesTable : Table("scanner_runs_package_provenances") {
    val scannerRunId = reference("scanner_run_id", ScannerRunsTable)
    val packageProvenanceId = reference("package_provenance_id", PackageProvenancesTable)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(scannerRunId, packageProvenanceId, name = "${tableName}_pkey")

    fun insertIfNotExists(scannerRunId: Long, packageProvenanceId: Long) {
        val exists = selectAll().where {
            ScannerRunsPackageProvenancesTable.scannerRunId eq scannerRunId and
                    (ScannerRunsPackageProvenancesTable.packageProvenanceId eq packageProvenanceId)
        }.count() > 0

        if (!exists) {
            insert {
                it[ScannerRunsPackageProvenancesTable.scannerRunId] = scannerRunId
                it[ScannerRunsPackageProvenancesTable.packageProvenanceId] = packageProvenanceId
            }
        }
    }
}
