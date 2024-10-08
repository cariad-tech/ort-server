/*
 * Copyright (C) 2023 The ORT Server Authors (See <https://github.com/eclipse-apoapsis/ort-server/blob/main/NOTICE>)
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

package org.eclipse.apoapsis.ortserver.core.apiDocs

import io.github.smiley4.ktorswaggerui.dsl.routes.OpenApiRoute

import io.ktor.http.HttpStatusCode

import kotlinx.datetime.Clock

import org.eclipse.apoapsis.ortserver.api.v1.model.OrtRun
import org.eclipse.apoapsis.ortserver.api.v1.model.OrtRunStatus
import org.eclipse.apoapsis.ortserver.logaccess.LogLevel
import org.eclipse.apoapsis.ortserver.logaccess.LogSource

val getOrtRunById: OpenApiRoute.() -> Unit = {
    operationId = "getOrtRunById"
    summary = "Get details of an ORT run."
    tags = listOf("Runs")

    request {
        pathParameter<Long>("runId") {
            description = "The run's ID."
        }
    }

    response {
        HttpStatusCode.OK to {
            description = "Success"
            jsonBody<OrtRun> {
                example("Get ORT run") {
                    value = OrtRun(
                        id = 1,
                        index = 2,
                        organizationId = 1,
                        productId = 1,
                        repositoryId = 1,
                        revision = "main",
                        createdAt = Clock.System.now(),
                        jobConfigs = fullJobConfigurations,
                        resolvedJobConfigs = fullJobConfigurations,
                        jobs = jobs,
                        status = OrtRunStatus.ACTIVE,
                        finishedAt = null,
                        labels = mapOf("label key" to "label value"),
                        issues = emptyList(),
                        jobConfigContext = null,
                        resolvedJobConfigContext = "32f955941e94d0a318e1c985903f42af924e9050"
                    )
                }
            }
        }
    }
}

val getReportByRunIdAndFileName: OpenApiRoute.() -> Unit = {
    operationId = "GetReportByRunIdAndFileName"
    summary = "Download a report of an ORT run."
    tags = listOf("Reports")

    request {
        pathParameter<Long>("runId") {
            description = "The ID of the ORT run."
        }
        pathParameter<String>("fileName") {
            description = "The name of the report file to be downloaded."
        }
    }

    response {
        HttpStatusCode.OK to {
            description = "Success. The response body contains the requested report file."
            header<String>("Content-Type") {
                description = "The content type is set to the media type derived from the report file."
            }
        }

        HttpStatusCode.NotFound to {
            description = "The requested report file or the ORT run could not be resolved."
        }
    }
}

val getLogsByRunId: OpenApiRoute.() -> Unit = {
    operationId = "GetLogsByRunId"
    summary = "Download an archive with selected logs of an ORT run."
    tags = listOf("Logs")

    request {
        pathParameter<Long>("runId") {
            description = "The ID of the ORT run."
        }

        queryParameter<String>("level") {
            description = "The log level; can be one of " +
                    LogLevel.entries.joinToString { "'$it'" } + " (ignoring case)." +
                    "Only logs of this level or higher are retrieved. Defaults to 'INFO' if missing."
        }

        queryParameter<String>("steps") {
            description = "Defines the run steps for which logs are to be retrieved. This is a comma-separated " +
                    "string with the following allowed steps: " + LogSource.entries.joinToString { "'$it'" } +
                    " (ignoring case). If missing, the logs for all steps are retrieved."
        }
    }

    response {
        HttpStatusCode.OK to {
            description = "Success. The response body contains a Zip archive with the selected log files."
        }

        HttpStatusCode.NotFound to {
            description = "The ORT run does not exist."
        }

        HttpStatusCode.BadRequest to {
            description = "Invalid values have been provided for the log level or steps parameters."
        }
    }
}
