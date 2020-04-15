package io.github.kotlinminecraft.kmhomes.manager

import br.com.devsrsouza.kotlinbukkitapi.architecture.lifecycle.LifecycleListener
import br.com.devsrsouza.kotlinbukkitapi.collections.onlinePlayerMapOf
import br.com.devsrsouza.kotlinbukkitapi.extensions.skedule.BukkitDispatchers
import br.com.devsrsouza.kmhomes.*
import io.github.kotlinminecraft.kmhomes.*
import kotlinx.coroutines.*
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class HomeManager(
    override val plugin: KMHomesPlugin
) : LifecycleListener<KMHomesPlugin> {

    private val homesCache by lazy { onlinePlayerMapOf<MutableList<Home>>() }

    private val job by lazy { Job() }
    private val coroutineScope by lazy { CoroutineScope(job + BukkitDispatchers.SYNC) }

    private val sqlManager get() = plugin.sqlManager

    override fun onPluginEnable() {}

    override fun onPluginDisable() {
        job.cancel()
    }

    fun loadPlayer(player: Player) {
        coroutineScope.launch {
            val playerHomes = sqlManager.homesFromPlayer(player)

            if(player.isOnline) // IDEA: coroutineScope based on player lifecycle
                homesCache[player] = playerHomes.toMutableList()
        }
    }

    fun getHomes(player: Player): List<Home> {
        return homesCache[player] ?: emptyList()
    }

    fun findCachedHome(
        player: Player,
        home: String
    ): Home? = homesCache[player]?.find { it.name.equals(home, ignoreCase = true) }

    suspend fun findStoragedHome(
        playerName: String,
        home: String
    ): Home? = withContext(job) {
        sqlManager.findHome(playerName, home)
    }

    fun setHome(player: Player, name: String) {
        val playerName = player.name
        val playerLocation = player.location.clone()

        val actualHome = findCachedHome(player, name)

        coroutineScope.launch {
            if(actualHome != null) {
                sqlManager.updateHome(actualHome) {
                    this@updateHome.location = playerLocation
                }
            } else {
                val home = sqlManager.createHome(playerName, name, playerLocation)
                cacheHome(player, home)
            }
        }

    }

    fun canSetHome(player: Player, name: String): Boolean {
        // if this is already set, permit update
        return findCachedHome(player, name) != null || playerInLimit(player)
    }

    private fun playerInLimit(player: Player): Boolean {
        if(player.hasPermission(PERMISSION_LIMIT_UNLIMITED)) return true

        val max = Config.limit.map {
            if (player.hasPermission(PERMISSION_LIMIT_BASE + ".${it.key}"))
                it.value
            else
                null
        }.filterNotNull().max() ?: 0

        val currentHomes = homesCache[player]?.size ?: 0

        return currentHomes < max
    }

    // true if was deleted, false otherwise
    fun deleteHome(player: Player, home: String): Boolean {
        val playerName = player.name
        if(homesCache[player]?.removeIf { it.name.equals(home, ignoreCase = true) } == true) {
            coroutineScope.launch {
                sqlManager.deleteHome(playerName, home)
            }
            return true
        } else {
            return false
        }
    }

    fun makePublic(home: Home, enable: Boolean) {
        coroutineScope.launch {
            sqlManager.updateHome(home) {
                this@updateHome.isPublic = enable
            }
        }
    }

    private fun cacheHome(player: Player, home: Home) {
        homesCache.getOrPut(player) { mutableListOf() }
            .add(home)
    }

}