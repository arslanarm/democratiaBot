@file:AutoWired

package components

import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.annotation.ModuleName
import com.gitlab.kordlib.kordx.commands.argument.Argument
import com.gitlab.kordlib.kordx.commands.argument.text.StringArgument
import com.gitlab.kordlib.kordx.commands.kord.model.context.KordCommandEvent
import com.gitlab.kordlib.kordx.commands.kord.module.command
import com.gitlab.kordlib.kordx.commands.model.command.invoke
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withTimeoutOrNull
import pollActions.categories
import pollActions.channels
import pollActions.mute
import kotlin.time.Duration
import kotlin.time.minutes

class CommandExecution(val time: Duration = 5.minutes, val execution: (suspend KordCommandEvent.() -> Unit)? = null)

fun execute(duration: Duration = 5.minutes, execution: suspend KordCommandEvent.() -> Unit = {}) =
    CommandExecution(duration, execution)

@ModuleName("common")
fun poll() = command("poll") {
    val subCommands = subCommands {
        roles()
        channels()
        categories()
        mute()
    }

    invoke(StringArgument) { command ->
        if (activePolls[channel] == true) {
            respond("There is an active poll in this channel. Ignoring")
            return@invoke
        }
        if (command.isBlank())
            return@invoke respond("Please enter the command").let {}

        val executableCommand = try {
            subCommands[command]?.invoke(this)
        } catch (e: ExecutionStopException) {
            respond(e.message ?: "Execution stopped")
            return@invoke
        } ?: return@invoke respond("Can't find the command with such name").let {}
        val execution = executableCommand.execution
            ?: return@invoke respond("Poll stopped").let {}
        val accepted = channel.vote(command, executableCommand.time)
        if (!accepted)
            return@invoke

        execution()
    }
}

suspend fun <T : Any> KordCommandEvent.readOrStop(
    argument: Argument<T, MessageCreateEvent>,
    filter: suspend (T) -> Boolean = { true }
): T = withTimeoutOrNull(1.minutes) {
    read(argument, escape = { it.message.content == "stop" }, filter)
} ?: throw ExecutionStopException()

class ExecutionStopException : Exception()

class SubCommands {
    private val subCommands = mutableMapOf<String, suspend KordCommandEvent.() -> CommandExecution>()
    operator fun String.invoke(block: suspend KordCommandEvent.() -> CommandExecution) {
        subCommands[this] = block
    }

    operator fun get(command: String) = subCommands[command]
}

inline fun subCommands(block: SubCommands.() -> Unit) = SubCommands().apply(block)