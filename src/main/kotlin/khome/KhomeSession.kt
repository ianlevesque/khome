package khome

import io.ktor.client.features.websocket.ClientWebSocketSession
import io.ktor.client.features.websocket.DefaultClientWebSocketSession
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.http.cio.websocket.send
import io.ktor.util.KtorExperimentalAPI
import khome.core.MessageInterface
import khome.core.ServiceCallInterface
import khome.core.dependencyInjection.KhomeKoinComponent
import khome.core.mapping.ObjectMapper
import kotlinx.coroutines.ObsoleteCoroutinesApi
import mu.KotlinLogging
import org.koin.core.get

@KtorExperimentalAPI
@ObsoleteCoroutinesApi
internal class KhomeSession(
    delegate: DefaultClientWebSocketSession
) : KhomeKoinComponent, ClientWebSocketSession by delegate {

    private val logger = KotlinLogging.logger {}
    private val objectMapper: ObjectMapper = get()
    suspend fun callWebSocketApi(message: MessageInterface) =
        send(message.toJson()).also { logger.debug { "Called hass api with message: ${message.toJson()}" } }

    suspend fun callWebSocketApi(message: ServiceCallInterface) =
        send(message.toJson()).also { logger.info { "Called hass api with service call message: ${message.toJson()}" } }

    suspend inline fun <reified M : Any> consumeSingleMessage(): M = incoming.receive().asObject()
    inline fun <reified M : Any> Frame.asObject() = (this as Frame.Text).toObject<M>()
    inline fun <reified M : Any> Frame.Text.toObject(): M = objectMapper.fromJson(readText())

    private fun ServiceCallInterface.toJson(): String = objectMapper.toJson(this)
    private fun MessageInterface.toJson(): String = objectMapper.toJson(this)
}
