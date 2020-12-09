package pollActions

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.behavior.channel.edit
import com.gitlab.kordlib.core.behavior.createCategory
import com.gitlab.kordlib.kordx.commands.argument.primitive.IntArgument
import com.gitlab.kordlib.kordx.commands.argument.primitive.LongArgument
import com.gitlab.kordlib.kordx.commands.argument.text.StringArgument
import components.SubCommands
import components.execute
import components.readOrStop
import utils.getCategory
import utils.getCategoryOrNull

fun SubCommands.categories() {
    "createCategory" {
        respond("Enter the name")
        val name = readOrStop(StringArgument)
        execute {
            guild?.createCategory {
                this.name = name
            }
        }
    }
    "moveCategory" {
        respond("Enter the name of category")
        val name = readOrStop(LongArgument) {
            guild?.getCategoryOrNull(Snowflake(it)) != null
        }
        val category = guild!!.getCategory(Snowflake(name))
        respond("Current position ${category.getPosition()}")
        respond("Enter the new position")
        val position = readOrStop(IntArgument)
        execute {
            category.edit {
                this.position = position
            }
        }
    }
    "deleteCategory" {
        respond("Enter the name of category")
        val name = readOrStop(LongArgument) {
            guild?.getCategoryOrNull(Snowflake(it)) != null
        }
        val category = guild!!.getCategory(Snowflake(name))
        execute {
            category.delete()
        }
    }
}