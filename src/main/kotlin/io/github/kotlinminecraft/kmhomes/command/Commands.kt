package io.github.kotlinminecraft.kmhomes.command

import br.com.devsrsouza.kotlinbukkitapi.architecture.lifecycle.cancellableCoroutineScope
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.CommandDSL
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.arguments.boolean
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.arguments.optional
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.arguments.string
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.command
import br.com.devsrsouza.kotlinbukkitapi.dsl.command.fail
import br.com.devsrsouza.kotlinbukkitapi.extensions.bukkit.onlinePlayer
import br.com.devsrsouza.kotlinbukkitapi.extensions.text.msg
import br.com.devsrsouza.kmhomes.*
import io.github.kotlinminecraft.kmhomes.*
import kotlinx.coroutines.launch

internal fun KMHomesPlugin.registerCommands() {

    fun CommandDSL.applyMessages() {
        permissionMessage = MessagesConfig.no_permission
        onlyInGameMessage = MessagesConfig.only_in_game
    }

    command("home") {
        permission = PERMISSION_CMD_HOME
        description = "Teleport to your home"
        applyMessages()

        executorPlayer {
            val homeName = optional { string(0) } ?: Config.default_home_name

            val home = homeManager.findCachedHome(sender, homeName)

            if(home != null) {
                sender.teleport(home.location)

                sender.msg(MessagesConfig.home_teleport.replaceHome(home.name))
            } else {
                sender.msg(MessagesConfig.home_not_found)
            }
        }
    }

    command("sethome") {
        permission = PERMISSION_CMD_SETHOME
        description = "Sets a new home with the given name"
        applyMessages()

        executorPlayer {
            val name = optional { string(0) } ?: Config.default_home_name

            if(homeManager.canSetHome(sender, name)) {
                homeManager.setHome(sender, name)

                sender.msg(MessagesConfig.home_set.replaceHome(name))
            } else {
                sender.msg(MessagesConfig.home_limit_arrived)
            }
        }
    }

    command("delhome", "deletehome", "removehome") {
        permission = PERMISSION_CMD_DELHOME
        description = "Delete a home"
        applyMessages()

        executorPlayer {
            val home = string(0)

            if(homeManager.deleteHome(sender, home)) {
                sender.msg(MessagesConfig.home_deleted.replaceHome(home))
            } else {
                sender.msg(MessagesConfig.home_not_found)
            }

        }
    }

    command("homes") {
        permission = PERMISSION_CMD_HOMES
        description = "List all your homes"
        applyMessages()

        executorPlayer {
            val homes = homeManager.getHomes(sender)
                .takeIf { it.isNotEmpty() }
                ?: fail(MessagesConfig.none_home)

            val homesString = homes.joinToString(", ") { it.name }

            sender.msg(MessagesConfig.home_list.replaceHomeList(homesString))
        }
    }

    command("makePublic") {
        permission = PERMISSION_CMD_MAKE_PUBLIC
        description = "Turns your home in public home."
        applyMessages()

        executorPlayer {
            val homeName = string(0)
            val enable = optional { boolean(1) } ?: true

            val home = homeManager.findCachedHome(sender, homeName)

            if(home != null) {
                homeManager.makePublic(home, enable)
                sender.msg(
                    MessagesConfig.home_turned_public
                    .replaceHome(home.name)
                    .replaceHomePublic(home))
            } else {
                sender.msg(MessagesConfig.home_not_found)
            }
        }
    }

    command("visit") { // visit [player] [home]
        permission = PERMISSION_CMD_VISIT
        description = "Visit a home from a player"
        applyMessages()

        executorPlayer {
            val targetName = string(0)
            val homeName = string(1)

            val home = onlinePlayer(targetName)?.let {
                homeManager.findCachedHome(it, homeName)
            }

            fun onSuccesses() = sender.msg(
                MessagesConfig.home_visit
                    .replaceHome(homeName)
                    .replaceTarget(targetName)
            )

            fun onFail() = sender.msg(MessagesConfig.home_not_public)

            if(home != null) {
                if(home.isPublic) {
                    sender.teleport(home.location)
                    onSuccesses()
                } else onFail()
            } else {
                cancellableCoroutineScope.launch {
                    val home = homeManager.findStoragedHome(targetName, homeName)

                    if(home?.isPublic == true && sender.isValid && sender.isOnline) {
                        sender.teleport(home.location)
                        onSuccesses()
                    } else onFail()
                }
            }

        }
    }

}