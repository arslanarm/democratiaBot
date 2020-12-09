package components

import com.gitlab.kordlib.core.behavior.UserBehavior
import com.gitlab.kordlib.core.behavior.channel.MessageChannelBehavior
import com.gitlab.kordlib.core.behavior.channel.TextChannelBehavior
import com.gitlab.kordlib.core.behavior.channel.createEmbed
import com.gitlab.kordlib.core.entity.User
import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import com.gitlab.kordlib.core.event.message.ReactionAddEvent
import com.gitlab.kordlib.kordx.emoji.DiscordEmoji
import com.gitlab.kordlib.kordx.emoji.Emojis
import com.gitlab.kordlib.kordx.emoji.addReaction
import com.gitlab.kordlib.kordx.emoji.toReaction
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.awt.Color
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.time.Duration

val voteContext = newFixedThreadPoolContext(5, "vote")
val activePolls = mutableMapOf<TextChannelBehavior, Boolean>()

suspend fun MessageChannelBehavior.vote(name: String, timeout: Duration): Boolean {
    val message = createMessage("Голосование $name запущено").also { message ->
        coroutineScope {
            Vote.values().forEach {
                message.addReaction(it.emoji)
            }
        }
    }

    val votes = mutableMapOf<UserBehavior, Vote>()

    fun statistics() = Vote.values().joinToString("\n") { vote ->
        "${(votes.count { it.value == vote }.toDouble() / votes.size * 100).roundToInt()}% - ${vote.value}"
    }
    try {
        withTimeoutOrNull(timeout) {
            coroutineScope {
                kord.events
                    .filterIsInstance<ReactionAddEvent>()
                    .filter { it.message == message && it.user.asUser().isBot != true }
                    .flowOn(voteContext)
                    .map { event ->
                        launch {
                            val author = event.user
                            if (author in votes) {
                                message.deleteReaction(author.id, event.emoji)
                                return@launch
                            }
                            val vote = Vote.values().firstOrNull {
                                it.emoji.toReaction() == event.emoji
                            } ?: return@launch message.deleteReaction(author.id, event.emoji)

                            votes[author] = vote
                            val maxVote =
                                max(votes.count { it.value == Vote.ACCEPT }, votes.count { it.value == Vote.DENY })
                            val half = ((event.guild?.asGuild()?.memberCount ?: 0) - 2) / 2
                            println("$maxVote, $half")
                            if (maxVote > half) {
                                println("Cancelling")
                                this@coroutineScope.cancel()
                            }
                        }
                    }
                    .launchIn(this)

                kord.events
                    .filterIsInstance<MessageCreateEvent>()
                    .filter { it.message.channel == message.channel && it.message.content.startsWith(".voteStatus") }
                    .map {
                        createEmbed {
                            title = "Статус голосования $name"
                            color = Color(0, 255, 0)
                            description = """
                        Проголосовавших ${votes.size}:
                        ${statistics()}
                    """.trimIndent()
                        }
                    }
                    .launchIn(this)
            }
        }
    } catch (e: CancellationException) { }

    val accepted = votes.count { it.value == Vote.ACCEPT } > votes.count { it.value == Vote.DENY }
    createEmbed {
        title = "Голосование $name окончено"
        color = Color(255, 255, 255)
        val totalVotes = votes.size
        description = """
        Проголосовавших $totalVotes:
        ${statistics()}
        
        ${if (accepted) "Изменение принято" else "Изменение не приятно"}
    """.trimIndent()
    }
    return accepted
}

enum class Vote(val value: String, val emoji: DiscordEmoji) {
    ACCEPT("за", Emojis.whiteCheckMark), DENY("против", Emojis.x)
}