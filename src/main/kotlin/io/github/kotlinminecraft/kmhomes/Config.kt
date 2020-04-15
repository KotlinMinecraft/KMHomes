package io.github.kotlinminecraft.kmhomes

import br.com.devsrsouza.kotlinbukkitapi.config.adapter.ChangeColor
import br.com.devsrsouza.kotlinbukkitapi.exposed.DatabaseTypeConfig

object Config {
    var database = SQLConfig
    var messages = MessagesConfig

    val default_home_name = "home"
    // permission: SouzaHome.limit.vip
    var limit = mapOf(
        "default" to 5,
        "vip" to 25
    )
}

object MessagesConfig {
    @ChangeColor var no_permission = "&cYou do not have permission to use this!"
    @ChangeColor var only_in_game = "&cYou can not use this command in console!"
    @ChangeColor var home_set = "&eYou set the home &a$CONFIG_KEY_HOME&e."
    @ChangeColor var home_limit_arrived = "&cLimit of homes arrived, try delete a home with /delhome."
    @ChangeColor var home_deleted = "&eYour home &a$CONFIG_KEY_HOME&e was deleted."
    @ChangeColor var home_not_found = "&cThis home was not found."
    @ChangeColor var none_home = "&eYou do not have any home set."
    @ChangeColor var home_list = "&eHomes: &a$CONFIG_KEY_HOME_LIST."
    @ChangeColor var home_teleport = "&eTeleported to home $CONFIG_KEY_HOME."
    @ChangeColor var home_visit = "&eTeleported to the home &a$CONFIG_KEY_HOME &efrom &a$CONFIG_KEY_TARGET."
    @ChangeColor var home_not_public = "&cThis player does not have a home public with this name!"
    @ChangeColor var home_turned_public = "&eYour home &a$CONFIG_KEY_HOME &ehas public mode: $CONFIG_KEY_HOME_PUBLIC."
}

object SQLConfig : DatabaseTypeConfig(
    type = "SQLite"
) {
    var table = "homes"
}