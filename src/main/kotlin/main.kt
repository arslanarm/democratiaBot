@file:AutoWired

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.kordx.commands.annotation.AutoWired
import com.gitlab.kordlib.kordx.commands.kord.bot
import com.gitlab.kordlib.kordx.commands.kord.model.prefix.kord
import com.gitlab.kordlib.kordx.commands.model.prefix.literal
import com.gitlab.kordlib.kordx.commands.model.prefix.prefix
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import kapt.kotlin.generated.configure

suspend fun main(args: Array<String>) {
    val token = args[0]
    val bot = Kord(token) {
        httpClient = HttpClient(CIO) {
            engine {
                threadsCount = 20
            }
        }
    }
    bot(bot) {
        configure()
    }
}

val prefix = prefix {
    kord { literal("!") }
}


