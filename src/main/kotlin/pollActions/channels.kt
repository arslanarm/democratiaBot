package pollActions

import com.gitlab.kordlib.common.entity.Overwrite
import com.gitlab.kordlib.common.entity.Permission
import com.gitlab.kordlib.common.entity.Permissions
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.behavior.channel.edit
import com.gitlab.kordlib.core.behavior.createTextChannel
import com.gitlab.kordlib.core.behavior.createVoiceChannel
import com.gitlab.kordlib.core.entity.PermissionOverwrite
import com.gitlab.kordlib.kordx.commands.argument.primitive.LongArgument
import com.gitlab.kordlib.kordx.commands.argument.text.StringArgument
import com.gitlab.kordlib.kordx.commands.kord.argument.ChannelArgument
import components.SubCommands
import components.execute
import components.readOrStop
import components.readRole
import utils.getCategoryOrNull

fun SubCommands.channels() {
    "createTextChannel" {
        respond("Enter the name of the channel")
        val name = readOrStop(StringArgument)

        execute {
            guild?.createTextChannel {
                this.name = name
            }
        }
    }
    "createVoiceChannel" {
        respond("Enter the name of the channel")
        val name = readOrStop(StringArgument)
        execute {
            guild?.createVoiceChannel {
                this.name = name
            }
        }
    }
    "deleteChannel" {
        respond("Mention the channel")
        val channel = readOrStop(ChannelArgument)

        execute {
            channel.delete()
        }
    }
    "changeChannelCategory" {
        respond("Mention the channel")
        val channel = readOrStop(ChannelArgument)
        respond("Enter the category's id")
        val categoryId = readOrStop(LongArgument) { categoryId ->
            guild?.getCategoryOrNull(Snowflake(categoryId)) != null
        }
        execute {
            channel.edit {
                parentId = Snowflake(categoryId)
            }
        }
    }
    "restrictChannelToRole" {
        respond("Mention the channel")
        val channel = readOrStop(ChannelArgument)
        respond("Enter the role's name")
        val role = readRole()
        execute {
            channel.edit {
                val allow = PermissionOverwrite.forRole(role.id, allowed = Permissions {
                    +Permission.SendMessages
                })
                val deny = PermissionOverwrite.forEveryone(guild!!.id, denied = Permissions {
                    +Permission.SendMessages
                })
                permissionOverwrites.add(allow, deny)
            }
        }
    }
}

fun Set<Overwrite>?.add(vararg permissionOverwrites: PermissionOverwrite) = 
    permissionOverwrites.fold(this) { acc, permissionOverwrite -> 
        acc.add(permissionOverwrite)
    }

fun Set<Overwrite>?.add(permissionOverwrite: PermissionOverwrite) = permissionOverwrite.toOverwrite().let {
    if (this == null)
        setOf(it)
    else
        this + it
}

fun PermissionOverwrite.toOverwrite() = Overwrite(id = target.value, type = type.value, allow = allowed.code, deny = denied.code)