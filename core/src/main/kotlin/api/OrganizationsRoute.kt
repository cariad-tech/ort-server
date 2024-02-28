/*
 * Copyright (C) 2022 The ORT Server Authors (See <https://github.com/eclipse-apoapsis/ort-server/blob/main/NOTICE>)
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

package org.eclipse.apoapsis.ortserver.core.api

import io.github.smiley4.ktorswaggerui.dsl.delete
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.patch
import io.github.smiley4.ktorswaggerui.dsl.post

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.route

import org.eclipse.apoapsis.ortserver.api.v1.model.CreateInfrastructureService
import org.eclipse.apoapsis.ortserver.api.v1.model.CreateOrganization
import org.eclipse.apoapsis.ortserver.api.v1.model.CreateProduct
import org.eclipse.apoapsis.ortserver.api.v1.model.CreateSecret
import org.eclipse.apoapsis.ortserver.api.v1.model.PagedResponse
import org.eclipse.apoapsis.ortserver.api.v1.model.UpdateInfrastructureService
import org.eclipse.apoapsis.ortserver.api.v1.model.UpdateOrganization
import org.eclipse.apoapsis.ortserver.api.v1.model.UpdateSecret
import org.eclipse.apoapsis.ortserver.api.v1.model.mapToApi
import org.eclipse.apoapsis.ortserver.core.apiDocs.deleteInfrastructureServiceForOrganizationIdAndName
import org.eclipse.apoapsis.ortserver.core.apiDocs.deleteOrganizationById
import org.eclipse.apoapsis.ortserver.core.apiDocs.deleteSecretByOrganizationIdAndName
import org.eclipse.apoapsis.ortserver.core.apiDocs.getInfrastructureServicesByOrganizationId
import org.eclipse.apoapsis.ortserver.core.apiDocs.getOrganizationById
import org.eclipse.apoapsis.ortserver.core.apiDocs.getOrganizationProducts
import org.eclipse.apoapsis.ortserver.core.apiDocs.getOrganizations
import org.eclipse.apoapsis.ortserver.core.apiDocs.getSecretByOrganizationIdAndName
import org.eclipse.apoapsis.ortserver.core.apiDocs.getSecretsByOrganizationId
import org.eclipse.apoapsis.ortserver.core.apiDocs.patchInfrastructureServiceForOrganizationIdAndName
import org.eclipse.apoapsis.ortserver.core.apiDocs.patchOrganizationById
import org.eclipse.apoapsis.ortserver.core.apiDocs.patchSecretByOrganizationIdAndName
import org.eclipse.apoapsis.ortserver.core.apiDocs.postInfrastructureServiceForOrganization
import org.eclipse.apoapsis.ortserver.core.apiDocs.postOrganizations
import org.eclipse.apoapsis.ortserver.core.apiDocs.postProduct
import org.eclipse.apoapsis.ortserver.core.apiDocs.postSecretForOrganization
import org.eclipse.apoapsis.ortserver.core.authorization.requirePermission
import org.eclipse.apoapsis.ortserver.core.authorization.requireSuperuser
import org.eclipse.apoapsis.ortserver.core.utils.listQueryParameters
import org.eclipse.apoapsis.ortserver.core.utils.requireParameter
import org.eclipse.apoapsis.ortserver.model.authorization.OrganizationPermission
import org.eclipse.apoapsis.ortserver.model.util.OrderDirection
import org.eclipse.apoapsis.ortserver.model.util.OrderField
import org.eclipse.apoapsis.ortserver.services.InfrastructureServiceService
import org.eclipse.apoapsis.ortserver.services.OrganizationService
import org.eclipse.apoapsis.ortserver.services.SecretService

import org.koin.ktor.ext.inject

@Suppress("LongMethod")
fun Route.organizations() = route("organizations") {
    val organizationService by inject<OrganizationService>()
    val secretService by inject<SecretService>()
    val infrastructureServiceService by inject<InfrastructureServiceService>()

    get(getOrganizations) {
        requireSuperuser()

        val paginationParameters = call.listQueryParameters(OrderField("name", OrderDirection.ASCENDING))

        val organizations = organizationService.listOrganizations(paginationParameters)
        val pagedResponse = PagedResponse(
            organizations.map { it.mapToApi() },
            paginationParameters
        )

        call.respond(HttpStatusCode.OK, pagedResponse)
    }

    post(postOrganizations) {
        requireSuperuser()

        val createOrganization = call.receive<CreateOrganization>()

        val createdOrganization =
            organizationService.createOrganization(createOrganization.name, createOrganization.description)

        call.respond(HttpStatusCode.Created, createdOrganization.mapToApi())
    }

    route("{organizationId}") {
        get(getOrganizationById) {
            requirePermission(OrganizationPermission.READ)

            val id = call.requireParameter("organizationId").toLong()

            val organization = organizationService.getOrganization(id)

            organization?.let { call.respond(HttpStatusCode.OK, it.mapToApi()) }
                ?: call.respond(HttpStatusCode.NotFound)
        }

        patch(patchOrganizationById) {
            requirePermission(OrganizationPermission.WRITE)

            val organizationId = call.requireParameter("organizationId").toLong()
            val org = call.receive<UpdateOrganization>()

            val updatedOrg = organizationService.updateOrganization(organizationId, org.name, org.description)

            call.respond(HttpStatusCode.OK, updatedOrg.mapToApi())
        }

        delete(deleteOrganizationById) {
            requirePermission(OrganizationPermission.DELETE)

            val id = call.requireParameter("organizationId").toLong()

            organizationService.deleteOrganization(id)

            call.respond(HttpStatusCode.NoContent)
        }

        get("products", getOrganizationProducts) {
            requirePermission(OrganizationPermission.READ_PRODUCTS)

            val orgId = call.requireParameter("organizationId").toLong()
            val paginationParameters = call.listQueryParameters(OrderField("name", OrderDirection.ASCENDING))

            val productsForOrganization =
                organizationService.listProductsForOrganization(orgId, paginationParameters)
            val pagedResponse = PagedResponse(
                productsForOrganization.map { it.mapToApi() },
                paginationParameters
            )

            call.respond(HttpStatusCode.OK, pagedResponse)
        }

        post("products", postProduct) {
            requirePermission(OrganizationPermission.CREATE_PRODUCT)

            val createProduct = call.receive<CreateProduct>()
            val orgId = call.requireParameter("organizationId").toLong()

            val createdProduct = organizationService.createProduct(createProduct.name, createProduct.description, orgId)

            call.respond(HttpStatusCode.Created, createdProduct.mapToApi())
        }

        route("secrets") {
            get(getSecretsByOrganizationId) {
                requirePermission(OrganizationPermission.READ)

                val orgId = call.requireParameter("organizationId").toLong()
                val paginationParameters = call.listQueryParameters(OrderField("name", OrderDirection.ASCENDING))

                val secretsForOrganization = secretService.listForOrganization(orgId, paginationParameters)
                val pagedResponse = PagedResponse(
                    secretsForOrganization.map { it.mapToApi() },
                    paginationParameters
                )

                call.respond(HttpStatusCode.OK, pagedResponse)
            }

            route("{secretName}") {
                get(getSecretByOrganizationIdAndName) {
                    requirePermission(OrganizationPermission.READ)

                    val organizationId = call.requireParameter("organizationId").toLong()
                    val secretName = call.requireParameter("secretName")

                    secretService.getSecretByOrganizationIdAndName(organizationId, secretName)
                        ?.let { call.respond(HttpStatusCode.OK, it.mapToApi()) }
                        ?: call.respond(HttpStatusCode.NotFound)
                }

                patch(patchSecretByOrganizationIdAndName) {
                    requirePermission(OrganizationPermission.WRITE_SECRETS)

                    val organizationId = call.requireParameter("organizationId").toLong()
                    val secretName = call.requireParameter("secretName")
                    val updateSecret = call.receive<UpdateSecret>()

                    call.respond(
                        HttpStatusCode.OK,
                        secretService.updateSecretByOrganizationAndName(
                            organizationId,
                            secretName,
                            updateSecret.value,
                            updateSecret.description
                        ).mapToApi()
                    )
                }

                delete(deleteSecretByOrganizationIdAndName) {
                    requirePermission(OrganizationPermission.WRITE_SECRETS)

                    val organizationId = call.requireParameter("organizationId").toLong()
                    val secretName = call.requireParameter("secretName")

                    secretService.deleteSecretByOrganizationAndName(organizationId, secretName)

                    call.respond(HttpStatusCode.NoContent)
                }
            }

            post(postSecretForOrganization) {
                requirePermission(OrganizationPermission.WRITE_SECRETS)

                val organizationId = call.requireParameter("organizationId").toLong()
                val createSecret = call.receive<CreateSecret>()

                call.respond(
                    HttpStatusCode.Created,
                    secretService.createSecret(
                        createSecret.name,
                        createSecret.value,
                        createSecret.description,
                        organizationId,
                        null,
                        null
                    ).mapToApi()
                )
            }
        }

        route("infrastructure-services") {
            get(getInfrastructureServicesByOrganizationId) {
                requirePermission(OrganizationPermission.READ)

                val orgId = call.requireParameter("organizationId").toLong()
                val paginationParameters = call.listQueryParameters(OrderField("name", OrderDirection.ASCENDING))

                val infrastructureServicesForOrganization =
                    infrastructureServiceService.listForOrganization(orgId, paginationParameters)
                val pagedResponse = PagedResponse(
                    infrastructureServicesForOrganization.map { it.mapToApi() },
                    paginationParameters
                )

                call.respond(HttpStatusCode.OK, pagedResponse)
            }

            post(postInfrastructureServiceForOrganization) {
                requirePermission(OrganizationPermission.WRITE)

                val organizationId = call.requireParameter("organizationId").toLong()
                val createService = call.receive<CreateInfrastructureService>()

                val newService = infrastructureServiceService.createForOrganization(
                    organizationId,
                    createService.name,
                    createService.url,
                    createService.description,
                    createService.usernameSecretRef,
                    createService.passwordSecretRef,
                    createService.excludeFromNetrc
                )

                call.respond(HttpStatusCode.Created, newService.mapToApi())
            }

            route("{serviceName}") {
                patch(patchInfrastructureServiceForOrganizationIdAndName) {
                    requirePermission(OrganizationPermission.WRITE)

                    val organizationId = call.requireParameter("organizationId").toLong()
                    val serviceName = call.requireParameter("serviceName")
                    val updateService = call.receive<UpdateInfrastructureService>()

                    val updatedService = infrastructureServiceService.updateForOrganization(
                        organizationId,
                        serviceName,
                        updateService.url,
                        updateService.description,
                        updateService.usernameSecretRef,
                        updateService.passwordSecretRef,
                        updateService.excludeFromNetrc
                    )

                    call.respond(HttpStatusCode.OK, updatedService.mapToApi())
                }

                delete(deleteInfrastructureServiceForOrganizationIdAndName) {
                    requirePermission(OrganizationPermission.WRITE)

                    val organizationId = call.requireParameter("organizationId").toLong()
                    val serviceName = call.requireParameter("serviceName")

                    infrastructureServiceService.deleteForOrganization(organizationId, serviceName)

                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
    }
}
