package io.github.kotlinminecraft.kmhomes.command

import br.com.devsrsouza.kmhomes.*
import io.github.kotlinminecraft.kmhomes.*

fun String.replaceHome(home: String) = replace(CONFIG_KEY_HOME, home, ignoreCase = true)
fun String.replaceHomeList(homes: String) = replace(CONFIG_KEY_HOME_LIST, homes, ignoreCase = true)
fun String.replaceTarget(target: String) = replace(CONFIG_KEY_TARGET, target, ignoreCase = true)
fun String.replaceHomePublic(home: Home) = replace(
    CONFIG_KEY_HOME_PUBLIC, "${home.isPublic}", ignoreCase = true)