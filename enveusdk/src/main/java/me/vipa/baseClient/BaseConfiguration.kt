package me.vipa.baseClient

class BaseConfiguration {
    var clients: BaseClient? = null
    companion object {
        val instance = BaseConfiguration()
    }

    fun clientSetup(client: BaseClient) {
        clients=client
    }


}