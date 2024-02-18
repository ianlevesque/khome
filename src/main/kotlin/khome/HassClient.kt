package khome

import io.ktor.http.HttpMethod
import khome.core.Configuration
import khome.core.clients.WebSocketClient
import khome.core.mapping.ObjectMapperInterface
import kotlinx.coroutines.ObsoleteCoroutinesApi
import mu.KotlinLogging
import java.net.ConnectException

@ObsoleteCoroutinesApi
internal class HassClientImpl(
    private val config: Configuration,
    private val httpClient: WebSocketClient,
    private val objectMapper: ObjectMapperInterface,
) : HassClient {
    private val logger = KotlinLogging.logger { }

    private val method = HttpMethod.Get
    private val path = "/api/websocket"
    private val isSecure: Boolean = config.secure

    @ObsoleteCoroutinesApi
    override suspend fun startSession(block: suspend KhomeSession.() -> Unit) = startSessionCatching(block)

    private suspend fun startSessionCatching(block: suspend KhomeSession.() -> Unit) =
        try {
            when (isSecure) {
                true ->
                    httpClient.secureWebsocket(
                        method = method,
                        host = config.host,
                        port = config.port,
                        path = path,
                        block = { block(KhomeSession(this, objectMapper)) },
                    )

                false ->
                    httpClient.websocket(
                        method = method,
                        host = config.host,
                        port = config.port,
                        path = path,
                        block = { block(KhomeSession(this, objectMapper)) },
                    )
            }
        } catch (exception: ConnectException) {
            logger.error(exception) { "Could not establish a connection to your homeassistant instance." }
        } catch (exception: RuntimeException) {
            logger.error(exception) { "Could not start khome due to: ${exception.message}" }
        }
}

internal interface HassClient {
    @ObsoleteCoroutinesApi
    suspend fun startSession(block: suspend KhomeSession.() -> Unit)
}
