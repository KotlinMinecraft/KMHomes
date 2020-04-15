package io.github.kotlinminecraft.kmhomes

import br.com.devsrsouza.kotlinbukkitapi.architecture.KotlinPlugin
import io.github.kotlinminecraft.kmhomes.command.registerCommands
import io.github.kotlinminecraft.kmhomes.manager.HomeManager
import io.github.kotlinminecraft.kmhomes.manager.SQLManager

class KMHomesPlugin : KotlinPlugin() {

    val sqlManager = lifecycle(100) { SQLManager(this) }
    val homeManager = lifecycle(90) { HomeManager(this) }

    val config = config("config.yml", Config)

    override fun onPluginEnable() {
        registerCommands()
        registerEvents()
    }
}