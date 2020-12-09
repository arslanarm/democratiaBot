package pollActions

import com.gitlab.kordlib.common.entity.Overwrite
import com.gitlab.kordlib.common.entity.Permission
import com.gitlab.kordlib.common.entity.Permissions
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.behavior.channel.*
import com.gitlab.kordlib.core.behavior.createTextChannel
import com.gitlab.kordlib.core.behavior.createVoiceChannel
import com.gitlab.kordlib.core.cache.data.ChannelData
import com.gitlab.kordlib.core.entity.PermissionOverwrite
import com.gitlab.kordlib.core.entity.channel.*
import com.gitlab.kordlib.kordx.commands.argument.primitive.LongArgument
import com.gitlab.kordlib.kordx.commands.argument.text.StringArgument
import com.gitlab.kordlib.kordx.commands.kord.argument.ChannelArgument
import com.gitlab.kordlib.kordx.commands.kord.argument.TextChannelArgument
import com.gitlab.kordlib.kordx.commands.kord.argument.VoiceChannelArgument
import com.gitlab.kordlib.rest.builder.channel.TextChannelModifyBuilder
import com.gitlab.kordlib.rest.service.patchTextChannel
import components.SubCommands
import components.execute
import components.readOrStop
import components.readRole

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
    "createVoiceChannel"{
        respond("Enter the name of the channel")
        val name = readOrStop(StringArgument)
        execute {
            guild?.createVoiceChannel {
                this.name = name
            }
        }
    }
    "deleteChannel"{
        respond("Mention the channel")
        val channel = readOrStop(ChannelArgument)

        execute {
            channel.delete()
        }
    }
    "changeTextChannelCategory"{
        respond("Mention the channel")
        val channel = readOrStop(TextChannelArgument)
        respond("Enter the category's id")
        val category = readOrStop(ChannelArgument) { it is Category }
        execute {
            channel.edit {
                parentId = category.id
            }
        }
    }
    "changeVoiceChannelCategory"{
        respond("Mention the channel")
        val channel = readOrStop(VoiceChannelArgument)
        respond("Enter the category's id")
        val category = readOrStop(ChannelArgument) { it is Category }
        execute {
            channel.edit {
                parentId = category.id
            }
        }
    }
    "restrictChannelToRole"{
        respond("Mention the channel")
        val channel = readOrStop(TextChannelArgument)
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
    "restrictChannelToRole"{
        respond("Mention the channel")
        val channel = readOrStop(VoiceChannelArgument)
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