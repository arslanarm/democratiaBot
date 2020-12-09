package components

import com.gitlab.kordlib.common.entity.Permission
import com.gitlab.kordlib.common.entity.Permissions
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.addRole
import com.gitlab.kordlib.core.behavior.edit
import com.gitlab.kordlib.core.entity.Role
import com.gitlab.kordlib.kordx.commands.argument.primitive.IntArgument
import com.gitlab.kordlib.kordx.commands.argument.text.StringArgument
import com.gitlab.kordlib.kordx.commands.kord.argument.RoleArgument
import com.gitlab.kordlib.kordx.commands.kord.argument.UserArgument
import com.gitlab.kordlib.kordx.commands.kord.model.context.KordCommandEvent
import kotlinx.coroutines.flow.firstOrNull
import java.awt.Color

fun SubCommands.roles() {
    "createRole" {
        respond("Enter the role's name")
        val name = readOrStop(StringArgument)

        suspend fun readColorElement(name: String): Int {
            respond("Enter the value of $name color")
            return readOrStop(IntArgument) { it in 0..255 }
        }

        val color = Color(readColorElement("red"), readColorElement("green"), readColorElement("blue"))
        execute {
            guild!!.addRole {
                this.name = name
                this.color = color
            }
        }
    }
    "deleteRole" {
        respond("Enter the role's name")
        val name = readOrStop(StringArgument)

        execute {
            guild!!.roles.firstOrNull { it.name == name }?.delete()
        }
    }
    "addRole" {
        respond("Enter the role")
        val role = readRole()

        respond("Enter the user")
        val user = readOrStop(UserArgument)

        execute {
            user.asMember(guild!!.id).addRole(role.id)
        }
    }
    "removeRole" {
        respond("Enter the role")
        val role = readRole()

        respond("Enter the user")
        val user = readOrStop(UserArgument)

        execute {
            user.asMember(guild!!.id).removeRole(role.id)
        }
    }
}

suspend fun GuildBehavior.roleByName(name: String) = roles.firstOrNull { it.name.equals(name, ignoreCase = true) }
suspend fun KordCommandEvent.readRole(): Role =
    guild?.roleByName(readOrStop(StringArgument) {
        guild?.roleByName(it) != null
    })!!