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

package org.ossreviewtoolkit.server.clients.keycloak

import io.ktor.client.statement.HttpResponse

/**
 * A client implementing interactions with Keycloak, based on the documentation from
 * https://www.keycloak.org/docs-api/19.0/rest-api/index.html.
 */
@Suppress("TooManyFunctions")
interface KeycloakClient {
    /**
     * Return a set of all [groups][Group], which currently exist in the Keycloak realm.
     */
    suspend fun getGroups(): Set<Group>

    /**
     * Return exactly the [group][Group] with the given [id].
     */
    suspend fun getGroup(id: GroupId): Group

    /**
     * Return the [group][Group] with the given [name].
     */
    suspend fun getGroup(name: GroupName): Group

    /**
     * Add a new [group][Group] to the Keycloak realm with the given [name].
     */
    suspend fun createGroup(name: GroupName): HttpResponse

    /**
     * Update the [group][Group] with the given [id], with the new [name] in the Keycloak realm.
     */
    suspend fun updateGroup(id: GroupId, name: GroupName): HttpResponse

    /**
     * Delete the [group][Group] within the Keycloak realm with the given [id].
     */
    suspend fun deleteGroup(id: GroupId): HttpResponse

    /**
     * Get all client [roles][Role] for the [group][Group] with the given [id].
     */
    suspend fun getGroupClientRoles(id: GroupId): Set<Role>

    /**
     * Add a [role] to the [group][Group] with the given [id].
     */
    suspend fun addGroupClientRole(id: GroupId, role: Role): HttpResponse

    /**
     * Remove a [role] from the [group][Group] with the given [id].
     */
    suspend fun removeGroupClientRole(id: GroupId, role: Role): HttpResponse

    /**
     * Return a set of all [roles][Role] that are currently defined for the configured client.
     */
    suspend fun getRoles(): Set<Role>

    /**
     * Return exactly the client [role][Role] with the given [name].
     */
    suspend fun getRole(name: RoleName): Role

    /**
     * Add a new [role][Role] to the configured client with the given [name] and [description].
     */
    suspend fun createRole(name: RoleName, description: String? = null): HttpResponse

    /**
     * Update the [role][Role] within the configured client with the new [updatedName] and
     * [updatedDescription].
     */
    suspend fun updateRole(name: RoleName, updatedName: RoleName, updatedDescription: String?): HttpResponse

    /**
     * Delete the [role][Role] within the configured client with the given [name].
     */
    suspend fun deleteRole(name: RoleName): HttpResponse

    /**
     * Add the role identified by [compositeRoleId] to the composites of the role identified by [name].
     */
    suspend fun addCompositeRole(name: RoleName, compositeRoleId: RoleId): HttpResponse

    /**
     * Get all composite roles of the [role][Role] with the given [name].
     */
    suspend fun getCompositeRoles(name: RoleName): List<Role>

    /**
     * Remove the role identified by [compositeRoleId] from the composites of the role identified by [name].
     */
    suspend fun removeCompositeRole(name: RoleName, compositeRoleId: RoleId): HttpResponse

    /**
     * Return a set of all [users][User], which currently exist in the Keycloak realm.
     */
    suspend fun getUsers(): Set<User>

    /**
     * Return exactly the [user][User] with the given [id].
     */
    suspend fun getUser(id: UserId): User

    /**
     * Return the [user][User] with the given [username].
     */
    suspend fun getUser(username: UserName): User

    /**
     * Create a new [user][User] in the Keycloak realm with the given [username], [firstName], [lastName] and [email].
     */
    suspend fun createUser(
        username: UserName,
        firstName: String? = null,
        lastName: String? = null,
        email: String? = null
    ): HttpResponse

    /**
     * Update the [user][User] with the given [id] within the Keycloak realm with the new [username], [firstName],
     * [lastName] and [email].
     */
    suspend fun updateUser(
        id: UserId,
        username: UserName? = null,
        firstName: String? = null,
        lastName: String? = null,
        email: String? = null
    ): HttpResponse

    /**
     * Delete the [user][User] with the given [id] from the Keycloak realm.
     */
    suspend fun deleteUser(id: UserId): HttpResponse

    /**
     * Get all client [roles][Role] for the [user][User] with the given [id].
     */
    suspend fun getUserClientRoles(id: UserId): Set<Role>
}
