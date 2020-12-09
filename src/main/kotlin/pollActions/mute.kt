package pollActions

import com.gitlab.kordlib.common.entity.Permission
import com.gitlab.kordlib.common.entity.Permissions
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.addRole
import com.gitlab.kordlib.core.behavior.edit
import com.gitlab.kordlib.kordx.commands.argument.primitive.IntArgument
import com.gitlab.kordlib.kordx.commands.kord.argument.UserArgument
import components.SubCommands
import components.execute
import components.readOrStop
import components.roleByName
import kotlinx.coroutines.delay
import kotlin.time.minutes

val mutedPermissions: Permissions.PermissionsBuilder.() -> Unit = {
    -Permission.Speak
    -Permission.Connect
}
suspend fun GuildBehavior.mutedRole() =
    roleByName("muted")
        ?: addRole {
            name = "muted"
            permissions = permissions?.copy {
                mutedPermissions()
            } ?: Permissions { mutedPermissions() }
        }

fun SubCommands.mute() {
    "mute"{
        respond("Mention the user")
        val user = readOrStop(UserArgument)
        respond("Enter the number of minutes")
        val time = readOrStop(IntArgument)
        val role = guild!!.mutedRole()
        execute {
            val member = user.asMember(guild!!.id)
            member.edit {
                muted = true
            }
            member.addRole(role.id)
            delay(time.minutes)
            member.removeRole(role.id)
        }
    }
    "unmute"{
        respond("Mention the user")
        val user = readOrStop(UserArgument)
        val role = guild!!.mutedRole()
        execute {
            user.asMember(guild!!.id).removeRole(role.id)
        }
    }
}