package utils

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.entity.channel.Category
import kotlinx.coroutines.flow.filterIsInstance

val GuildBehavior.categories
    get() = channels.filterIsInstance<Category>()

suspend fun GuildBehavior.getCategory(id: Snowflake) =
    getCategoryOrNull(id)!!

suspend fun GuildBehavior.getCategoryOrNull(id: Snowflake): Category? =
    getChannelOrNull(id)?.run { if (this is Category) this else null }