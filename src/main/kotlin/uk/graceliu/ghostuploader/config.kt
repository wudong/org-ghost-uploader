package uk.graceliu.ghostuploader

data class Configuration(val baseUrl: String, val userName: String, val password: String, val fileName: String) {

    companion object {
        fun getConfigurationFromArgs(args: Array<String>): Configuration {
           TODO()
        }
    }
}

