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

package org.ossreviewtoolkit.server.config

import com.typesafe.config.ConfigFactory

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.beInstanceOf

import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk

import java.io.FileNotFoundException

import kotlin.io.path.absolutePathString
import kotlin.io.path.toPath

import org.ossreviewtoolkit.server.utils.config.getStringOrNull

class ConfigManagerTest : WordSpec({
    "create" should {
        "throw an exception if no provider for config files is specified" {
            val managerMap = mapOf(
                ConfigManager.SECRET_PROVIDER_NAME_PROPERTY to ConfigSecretProviderFactoryForTesting.NAME,
                "foo" to "bar"
            )
            val configMap = mapOf(ConfigManager.CONFIG_MANAGER_SECTION to managerMap)
            val config = ConfigFactory.parseMap(configMap)

            val exception = shouldThrow<ConfigException> {
                val configManager = ConfigManager.create(config)
                configManager.containsFile(Context("someContext"), Path("somePath"))
            }

            exception.message shouldContain ConfigManager.FILE_PROVIDER_NAME_PROPERTY
        }

        "throw an exception if no provider for config secrets is specified" {
            val managerMap = mapOf(
                ConfigManager.FILE_PROVIDER_NAME_PROPERTY to ConfigFileProviderFactoryForTesting.NAME,
                "foo" to "bar"
            )
            val configMap = mapOf(ConfigManager.CONFIG_MANAGER_SECTION to managerMap)
            val config = ConfigFactory.parseMap(configMap)

            val exception = shouldThrow<ConfigException> {
                val configManager = ConfigManager.create(config)
                configManager.getSecret(Path("somePath"))
            }

            exception.message shouldContain ConfigManager.SECRET_PROVIDER_NAME_PROPERTY
        }

        "throw an exception if no section for the config manager is present" {
            val configMap = mapOf("foo" to "bar")
            val config = ConfigFactory.parseMap(configMap)

            val exception = shouldThrow<ConfigException> {
                ConfigManager.create(config)
            }

            exception.message shouldContain ConfigManager.CONFIG_MANAGER_SECTION
        }

        "throw an exception if the file provider cannot be found on the classpath" {
            val providerName = "unknownFileProvider"
            val managerMap = mapOf(
                ConfigManager.FILE_PROVIDER_NAME_PROPERTY to providerName,
                ConfigManager.SECRET_PROVIDER_NAME_PROPERTY to ConfigSecretProviderFactoryForTesting.NAME
            )
            val configMap = mapOf(ConfigManager.CONFIG_MANAGER_SECTION to managerMap)
            val config = ConfigFactory.parseMap(configMap)

            val exception = shouldThrow<ConfigException> {
                val configManager = ConfigManager.create(config)
                configManager.containsFile(Context("someContext"), Path("somePath"))
            }

            exception.message shouldContain providerName
        }

        "throw an exception if the secret provider cannot be found on the classpath" {
            val providerName = "unknownSecretProvider"
            val managerMap = mapOf(
                ConfigManager.SECRET_PROVIDER_NAME_PROPERTY to providerName,
                ConfigManager.FILE_PROVIDER_NAME_PROPERTY to ConfigFileProviderFactoryForTesting.NAME
            )
            val configMap = mapOf(ConfigManager.CONFIG_MANAGER_SECTION to managerMap)
            val config = ConfigFactory.parseMap(configMap)

            val exception = shouldThrow<ConfigException> {
                val configManager = ConfigManager.create(config)
                configManager.getSecret(Path("someSecret"))
            }

            exception.message shouldContain providerName
        }

        "pass an initialized secret provider to the file provider" {
            val providerMap = createConfigProviderProperties() +
                    mapOf(ConfigFileProviderFactoryForTesting.SECRET_PROPERTY to TEST_SECRET_NAME)
            val configMap = mapOf(ConfigManager.CONFIG_MANAGER_SECTION to providerMap)
            val config = ConfigFactory.parseMap(configMap)

            val configManager = ConfigManager.create(config)

            configManager.containsFile(testContext(), Path("somePath")) shouldBe false
        }

        "instantiate providers lazily" {
            val configMap = mapOf(ConfigManager.CONFIG_MANAGER_SECTION to emptyMap<String, Any>())
            val config = ConfigFactory.parseMap(configMap)

            ConfigManager.create(config)
        }
    }

    "resolveContext" should {
        "return the resolved context" {
            val context = "test-context"
            val manager = createConfigManager()

            val resolvedContext = manager.resolveContext(Context(context))

            resolvedContext.name shouldBe ConfigFileProviderFactoryForTesting.RESOLVED_PREFIX + context
        }

        "use the default context" {
            val manager = createConfigManager()

            val resolvedContext = manager.resolveContext(null)

            resolvedContext.name shouldBe ConfigFileProviderFactoryForTesting.RESOLVED_PREFIX +
                    ConfigManager.DEFAULT_CONTEXT.name
        }

        "handle exceptions from the provider" {
            val manager = createConfigManager()

            shouldThrow<ConfigException> {
                manager.resolveContext(Context(ConfigFileProviderFactoryForTesting.ERROR_VALUE))
            }
        }
    }

    "getFile" should {
        "return a stream for a configuration file" {
            val manager = createConfigManager()

            val fileContent = manager.getFile(testContext(), Path("root.txt")).use {
                String(it.readAllBytes()).trim()
            }

            fileContent shouldBe "Root config file."
        }

        "return a stream for a configuration from the default context" {
            val manager = createConfigManager()

            val fileContent = manager.getFile(null, Path("test.txt")).use {
                String(it.readAllBytes()).trim()
            }

            fileContent shouldBe "Test config file."
        }

        "handle exceptions from the provider" {
            val manager = createConfigManager()

            val exception = shouldThrow<ConfigException> {
                manager.getFile(testContext(), Path("nonExistingPath"))
            }

            exception.cause should beInstanceOf<FileNotFoundException>()
        }
    }

    "getFileString" should {
        "return the content of a configuration file as string" {
            val manager = createConfigManager()

            val fileContent = manager.getFileAsString(testContext(), Path("root.txt")).trim()

            fileContent shouldBe "Root config file."
        }

        "return the content of a configuration file from the default context as string" {
            val manager = createConfigManager()

            val fileContent = manager.getFileAsString(null, Path("test.txt")).trim()

            fileContent shouldBe "Test config file."
        }

        "handle exceptions while reading the stream" {
            val manager = spyk(createConfigManager())
            every { manager.getFile(any(), any()) } returns mockk()

            // Since an uninitialized mock is returned as stream, it will throw on each method call.
            shouldThrow<ConfigException> {
                manager.getFileAsString(testContext(), Path("root.txt"))
            }
        }
    }

    "containsFile" should {
        "return true for an existing configuration file" {
            val manager = createConfigManager()

            manager.containsFile(testContext(), Path("root.txt")) shouldBe true
        }

        "return false for a non-existing configuration file" {
            val manager = createConfigManager()

            manager.containsFile(testContext(), Path("nonExistingFile")) shouldBe false
        }

        "return true for an existing configuration file in the default context" {
            val manager = createConfigManager()

            manager.containsFile(null, Path("test.txt")) shouldBe true
        }

        "handle exceptions from the provider" {
            val manager = createConfigManager()

            shouldThrow<ConfigException> {
                manager.containsFile(testContext(), Path(ConfigFileProviderFactoryForTesting.ERROR_VALUE))
            }
        }
    }

    "listFiles" should {
        "return a set with Paths representing configuration files in a sub folder" {
            val manager = createConfigManager()

            val paths = manager.listFiles(testContext(), Path("sub"))

            paths shouldContainExactlyInAnyOrder listOf(Path("sub/sub1.txt"), Path("sub/sub2.txt"))
        }

        "return a set with Paths representing configuration files in the default context" {
            val manager = createConfigManager()

            val paths = manager.listFiles(null, Path("."))

            paths shouldContainExactlyInAnyOrder listOf(Path("./test.txt"))
        }

        "handle exceptions from the provider" {
            val manager = createConfigManager()

            shouldThrow<ConfigException> {
                manager.listFiles(testContext(), Path("nonExistingPath"))
            }
        }
    }

    "getSecret" should {
        "return the value of a secret" {
            val manager = createConfigManager()

            val secret = manager.getSecret(Path(TEST_SECRET_NAME))

            secret shouldBe TEST_SECRET_VALUE
        }

        "handle exceptions from the provider" {
            val manager = createConfigManager()

            shouldThrow<ConfigException> {
                manager.getSecret(Path("nonExistingSecret"))
            }
        }

        "return the value of a secret from the configuration" {
            val secretKey = "testSecret"
            val secretValue = "secretValue"
            val properties = createConfigManagerProperties() + mapOf(secretKey to secretValue)
            val manager = createConfigManager(properties)

            val secret = manager.getSecret(Path(secretKey))

            secret shouldBe secretValue
        }

        "prefer secrets from the configuration over the secret provider" {
            val secretValue = "overriddenSecretValue"
            val properties = createConfigManagerProperties() + mapOf(TEST_SECRET_NAME to secretValue)
            val manager = createConfigManager(properties)

            val secret = manager.getSecret(Path(TEST_SECRET_NAME))

            secret shouldBe secretValue
        }

        "support switching off reading secrets from the configuration" {
            val providerProperties = createConfigProviderProperties() + mapOf(
                ConfigManager.SECRET_FROM_CONFIG_PROPERTY to false
            )
            val properties = mapOf(
                ConfigManager.CONFIG_MANAGER_SECTION to providerProperties,
                TEST_SECRET_NAME to "someOtherSecretValue"
            )
            val manager = createConfigManager(properties)

            val secret = manager.getSecret(Path(TEST_SECRET_NAME))

            secret shouldBe TEST_SECRET_VALUE
        }
    }

    "config" should {
        "be accessible" {
            val testKey = "test.property.key"
            val testValue = "Success"
            val configMap = createConfigManagerProperties() + mapOf(testKey to testValue)
            val config = ConfigFactory.parseMap(configMap)

            val configManager = ConfigManager.create(config)

            configManager.getString(testKey) shouldBe testValue
            configManager.getStringOrNull("foo") should beNull()
        }
    }
})

private const val TEST_SECRET_NAME = "top-secret"
private const val TEST_SECRET_VALUE = "licenseToTest"

/**
 * Create a [ConfigManager] instance with the given [configuration][configMap].
 */
private fun createConfigManager(
    configMap: Map<String, Any> = createConfigManagerProperties()
): ConfigManager {
    val config = ConfigFactory.parseMap(configMap)

    return ConfigManager.create(config)
}

/**
 * Return a [Map] with properties that are required to create a [ConfigManager] instance.
 */
private fun createConfigManagerProperties(): Map<String, Map<String, Any>> {
    val configManagerMap = createConfigProviderProperties()

    return mapOf(ConfigManager.CONFIG_MANAGER_SECTION to configManagerMap)
}

/**
 * Return a [Map] with the properties related to the configuration providers. This basically defines the content of
 * the `configManager` section in the configuration.
 */
private fun createConfigProviderProperties(): Map<String, Any> {
    return mapOf(
        ConfigManager.FILE_PROVIDER_NAME_PROPERTY to ConfigFileProviderFactoryForTesting.NAME,
        ConfigManager.SECRET_PROVIDER_NAME_PROPERTY to ConfigSecretProviderFactoryForTesting.NAME,
        ConfigSecretProviderFactoryForTesting.SECRETS_PROPERTY to mapOf(TEST_SECRET_NAME to TEST_SECRET_VALUE)
    )
}

/**
 * Return a [Context] for the test [ConfigFileProvider] that points to the config directory in the test resources.
 */
private fun testContext(): Context {
    val configResource = ConfigManagerTest::class.java.getResource("/config").shouldNotBeNull()
    return Context(configResource.toURI().toPath().absolutePathString())
}
