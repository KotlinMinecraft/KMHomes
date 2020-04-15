package io.github.kotlinminecraft.kmhomes

import br.com.devsrsouza.kotlinbukkitapi.extensions.event.event
import br.com.devsrsouza.kotlinbukkitapi.extensions.event.events
import org.bukkit.event.player.PlayerJoinEvent

fun KMHomesPlugin.registerEvents() = events {
    event<PlayerJoinEvent> {
        homeManager.loadPlayer(player)
    }
}