package khome.core.dependencyInjection

import khome.DefaultErrorResponseHandler
import khome.ErrorResponseHandlerInterface
import khome.Khome
import khome.KhomeSession
import khome.calling.PersistentNotificationCreate
import khome.core.ConfigurationInterface
import khome.core.boot.BootSequenceInterface
import khome.core.entities.system.DateTime
import khome.core.entities.system.Sun
import khome.core.entities.system.Time
import khome.core.events.DefaultErrorResultListenerExceptionHandler
import khome.core.events.DefaultHassEventListenerExceptionHandler
import khome.core.events.DefaultStateChangeListenerExceptionHandler
import khome.core.events.ErrorResultListenerExceptionHandler
import khome.core.events.EventListenerExceptionHandler
import khome.core.events.StateChangeListenerExceptionHandler

internal class KhomeModulesInitializer(override val khomeSession: KhomeSession, private val configuration: ConfigurationInterface) :
    BootSequenceInterface {

    private val systemBeansModule =
        khomeModule(createdAtStart = true, override = true) {
            bean { Sun() }
            bean { Time() }
            bean { DateTime() }
            service { PersistentNotificationCreate() }
            if (configuration.enableDefaultErrorResponseHandler)
                bean<ErrorResponseHandlerInterface> { DefaultErrorResponseHandler(get()) }
            if (configuration.enableDefaultStateChangeListenerExceptionHandler)
                bean<StateChangeListenerExceptionHandler> { DefaultStateChangeListenerExceptionHandler(get(), get()) }
            if (configuration.enableHassEventListenerExceptionHandler)
                bean<EventListenerExceptionHandler> { DefaultHassEventListenerExceptionHandler(get(), get()) }
            if (configuration.enableErrorResponseListenerExceptionHandler)
                bean<ErrorResultListenerExceptionHandler> { DefaultErrorResultListenerExceptionHandler(get(), get()) }
        }

    private val userBeansModule =
        khomeModule(
            createdAtStart = true,
            moduleDeclaration = Khome.beanDeclarations
        )

    override suspend fun runBootSequence() {
        loadKhomeModule(systemBeansModule)
        loadKhomeModule(userBeansModule)
    }
}
