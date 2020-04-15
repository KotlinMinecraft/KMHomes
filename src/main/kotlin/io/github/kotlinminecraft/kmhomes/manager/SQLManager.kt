package io.github.kotlinminecraft.kmhomes.manager

import br.com.devsrsouza.kotlinbukkitapi.architecture.lifecycle.LifecycleListener
import br.com.devsrsouza.kotlinbukkitapi.exposed.databaseTypeFrom
import io.github.kotlinminecraft.kmhomes.Home
import io.github.kotlinminecraft.kmhomes.HomeTable
import io.github.kotlinminecraft.kmhomes.SQLConfig
import io.github.kotlinminecraft.kmhomes.KMHomesPlugin
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class SQLManager(
    override val plugin: KMHomesPlugin
) : LifecycleListener<KMHomesPlugin> {

    private lateinit var dataSource: HikariDataSource
    private lateinit var database: Database

    override fun onPluginEnable() {
        dataSource = databaseTypeFrom(plugin.dataFolder, SQLConfig).dataSource()
        database = Database.connect(dataSource)

        transaction {
            SchemaUtils.create(HomeTable)
        }
    }

    override fun onPluginDisable() {
        dataSource.close()
    }

    suspend fun homesFromPlayer(
        player: Player
    ): List<Home> = newSuspendedTransaction(Dispatchers.IO, database) {
        Home.homesFromPlayer(player)
    }

    suspend fun findHome(
        playerName: String,
        home: String
    ) = newSuspendedTransaction(Dispatchers.IO, database) {
        Home.findHome(playerName, home)
    }

    suspend fun createHome(
        player: String,
        name: String,
        location: Location
    ): Home = newSuspendedTransaction(Dispatchers.IO, database) {
        Home.newHome(player, name, location)
    }

    suspend fun updateHome(
        actualHome: Home,
        updateBlock: Home.() -> Unit
    ) = newSuspendedTransaction(Dispatchers.IO, database) {
        updateBlock(actualHome)
    }

    suspend fun deleteHome(
        player: String,
        home: String
    ) = newSuspendedTransaction(Dispatchers.IO, database) {
        Home.deleteHome(player, home)
    }


}