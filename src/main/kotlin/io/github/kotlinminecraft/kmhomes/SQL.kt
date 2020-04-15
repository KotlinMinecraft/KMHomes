package io.github.kotlinminecraft.kmhomes

import br.com.devsrsouza.kotlinbukkitapi.exposed.delegate.location
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.lowerCase

object HomeTable : IntIdTable(SQLConfig.table) {
    val name = varchar("name", 80)
    val owner = varchar("owner", 25)

    val world = varchar("world", 80)
    val x = double("x")
    val y = double("y")
    val z = double("z")
    val yaw = float("yaw")
    val pitch = float("pitch")

    val public = bool("isPublic").default(false)
}

class Home(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Home>(HomeTable) {
        fun newHome(owner: Player, home: String, location: Location): Home {
            return newHome(
                owner.name,
                home,
                location
            )
        }

        fun newHome(owner: String, home: String, location: Location): Home {
            return Home.new {
                this@new.name = home
                this@new.owner = owner
                this@new.location = location
            }
        }

        fun homesFromPlayer(player: Player): List<Home> {
            return homesFromPlayer(player.name)
        }

        fun homesFromPlayer(playerName: String): List<Home> {
            return Home.find { ownerEq(playerName) }.toList()
        }

        fun findHome(playerName: String, home: String): Home? {
            return Home.find {
                ownerEq(playerName) and nameEq(home)
            }.firstOrNull()
        }

        fun deleteHome(playerName: String, home: String) {
            findHome(playerName, home)?.delete()
        }

        private fun SqlExpressionBuilder.ownerEq(playerName: String): Op<Boolean> =
            HomeTable.owner.lowerCase() eq playerName.toLowerCase()

        private fun SqlExpressionBuilder.nameEq(home: String): Op<Boolean> =
            HomeTable.name.lowerCase() eq home.toLowerCase()
    }

    var name by HomeTable.name
    var owner by HomeTable.owner
    var location by location(
        HomeTable.world,
        HomeTable.x,
        HomeTable.y,
        HomeTable.z,
        HomeTable.yaw,
        HomeTable.pitch
    )
    var isPublic by HomeTable.public
}