package me.vipa.baseClient

class BaseConfiguration {
    lateinit var clients: BaseClient
    companion object {
        val instance = BaseConfiguration()
    }

    fun clientSetup(client: BaseClient) {
        clients=client
    }
}