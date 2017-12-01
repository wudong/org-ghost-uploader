package uk.graceliu.ghostuploader

import com.typesafe.config.ConfigFactory
import com.typesafe.config.ConfigParseOptions
import com.typesafe.config.ConfigSyntax
import mu.KotlinLogging
import java.nio.file.Files
import java.nio.file.Paths


enum class ConfigEnum{
    baseUrl, userName, password, clientId, clientSecret, grantType
}

data class Configuration(override val baseUrl: String,
                         override val userName: String,
                         override val password: String,
                         override val clientId: String,
                         override val clientSecret: String,
                         override val grantType: String
) : GhostConfig{
    companion object {
        private val logger = KotlinLogging.logger {}

        fun getConfiguration(args: Array<String>): Configuration {

            val userConfigFile = Paths.get(System.getenv("HOME"), ".ghostuploader")

            val userConfig = if (Files.exists(userConfigFile)) {
                val parseOption = ConfigParseOptions.defaults().setSyntax(ConfigSyntax.PROPERTIES)
                logger.debug { "Loading config file from home directory" }
                ConfigFactory.parseFile(userConfigFile.toFile(), parseOption)
            }else {
                logger.debug { "Loading config file from default application default" }
                ConfigFactory.defaultApplication()
            }

            val config = ConfigFactory.load(userConfig)

            logger.debug { "Config loaded: $config" }

            return Configuration(
                    baseUrl = config.getString(ConfigEnum.baseUrl.name),
                    userName = config.getString(ConfigEnum.userName.name),
                    password = config.getString(ConfigEnum.password.name),
                    clientId = config.getString(ConfigEnum.clientId.name),
                    clientSecret = config.getString(ConfigEnum.clientSecret.name),
                    grantType = config.getString(ConfigEnum.grantType.name)
            )
        }
    }

}

