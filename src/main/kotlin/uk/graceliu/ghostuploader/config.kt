package uk.graceliu.ghostuploader

import com.typesafe.config.ConfigFactory


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
        fun getConfiguration(args: Array<String>): Configuration {
            val config= ConfigFactory.load()

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

