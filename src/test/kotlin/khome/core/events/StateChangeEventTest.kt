package khome.core.events

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import khome.core.StateChangedResponse
import khome.core.dependencyInjection.KhomeTestComponent
import khome.core.mapping.ObjectMapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import mu.KotlinLogging
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.koin.core.get

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StateChangeEventTest : KhomeTestComponent() {
    private val logger = KotlinLogging.logger { }

    @Test
    fun `assert callback was subscribed to event`() {
        val stateChangeEvent = StateChangeEvent(Event())
        stateChangeEvent.subscribe {
            logger.debug { it.id }
        }

        assertThat(stateChangeEvent.listenerCount).isEqualTo(1)
    }

    @Test
    fun `assert callback was subscribed to event with handle`() {
        val stateChangeEvent = StateChangeEvent(Event())
        stateChangeEvent.subscribe("handle") {
            logger.debug { it.id }
        }

        assertThat(stateChangeEvent["handle"]).isNotNull()
    }

    @Test
    fun `assert that subscribed event callback was fired`() = runBlocking {
        val stateChangeEvent = StateChangeEvent(Event())
        var testValue: StateChangedResponse? = null
        stateChangeEvent.subscribe {
            testValue = it
        }

        val eventResultJson = """
            {
               "id": 18,
               "type":"event",
               "event":{
                  "data":{
                     "entity_id":"light.bed_light",
                     "new_state":{
                        "entity_id":"light.bed_light",
                        "last_changed":"2016-11-26T01:37:24.265390+00:00",
                        "state":"on",
                        "attributes":{
                           "rgb_color":[
                              254,
                              208,
                              0
                           ],
                           "color_temp":380,
                           "supported_features":147,
                           "xy_color":[
                              0.5,
                              0.5
                           ],
                           "brightness":180,
                           "white_value":200,
                           "friendly_name":"Bed Light"
                        },
                        "last_updated":"2016-11-26T01:37:24.265390+00:00"
                     },
                     "old_state":{
                        "entity_id":"light.bed_light",
                        "last_changed":"2016-11-26T01:37:10.466994+00:00",
                        "state":"off",
                        "attributes":{
                           "supported_features":147,
                           "friendly_name":"Bed Light"
                        },
                        "last_updated":"2016-11-26T01:37:10.466994+00:00"
                     }
                  },
                  "event_type":"state_changed",
                  "time_fired":"2016-11-26T01:37:24.265429+00:00",
                  "origin":"LOCAL"
               }
            }
        """.trimIndent()
        val stateChangedResponse: StateChangedResponse = get<ObjectMapper>().fromJson(eventResultJson)

        stateChangeEvent.emit(stateChangedResponse)
        delay(1)
        assertThat(stateChangedResponse).isEqualTo(testValue)
    }

    @Test
    fun `assert that subscribed event callbacks were fired`() = runBlocking {
        val stateChangeEvent = StateChangeEvent(Event())
        var testValueOne: StateChangedResponse? = null
        var testValueTwo: StateChangedResponse? = null
        stateChangeEvent.subscribe {
            testValueOne = it
        }

        stateChangeEvent.subscribe {
            testValueTwo = it
        }

        val eventResultJson = """
            {
               "id": 18,
               "type":"event",
               "event":{
                  "data":{
                     "entity_id":"light.bed_light",
                     "new_state":{
                        "entity_id":"light.bed_light",
                        "last_changed":"2016-11-26T01:37:24.265390+00:00",
                        "state":"on",
                        "attributes":{
                           "rgb_color":[
                              254,
                              208,
                              0
                           ],
                           "color_temp":380,
                           "supported_features":147,
                           "xy_color":[
                              0.5,
                              0.5
                           ],
                           "brightness":180,
                           "white_value":200,
                           "friendly_name":"Bed Light"
                        },
                        "last_updated":"2016-11-26T01:37:24.265390+00:00"
                     },
                     "old_state":{
                        "entity_id":"light.bed_light",
                        "last_changed":"2016-11-26T01:37:10.466994+00:00",
                        "state":"off",
                        "attributes":{
                           "supported_features":147,
                           "friendly_name":"Bed Light"
                        },
                        "last_updated":"2016-11-26T01:37:10.466994+00:00"
                     }
                  },
                  "event_type":"state_changed",
                  "time_fired":"2016-11-26T01:37:24.265429+00:00",
                  "origin":"LOCAL"
               }
            }
        """.trimIndent()
        val stateChangedResponse: StateChangedResponse = get<ObjectMapper>().fromJson(eventResultJson)

        stateChangeEvent.emit(stateChangedResponse)
        delay(1)
        assertThat(stateChangedResponse).isEqualTo(testValueOne)
        assertThat(stateChangedResponse).isEqualTo(testValueTwo)
    }

    @Test
    fun `assert that unsubscribing was successful`() = runBlocking {
        val stateChangeEvent = StateChangeEvent(Event())
        var testValueOne: StateChangedResponse? = null
        var testValueTwo: StateChangedResponse? = null
        stateChangeEvent.subscribe("handle") {
            testValueOne = it
        }

        stateChangeEvent.subscribe {
            testValueTwo = it
        }

        val eventResultJson = """
            {
               "id": 18,
               "type":"event",
               "event":{
                  "data":{
                     "entity_id":"light.bed_light",
                     "new_state":{
                        "entity_id":"light.bed_light",
                        "last_changed":"2016-11-26T01:37:24.265390+00:00",
                        "state":"on",
                        "attributes":{
                           "rgb_color":[
                              254,
                              208,
                              0
                           ],
                           "color_temp":380,
                           "supported_features":147,
                           "xy_color":[
                              0.5,
                              0.5
                           ],
                           "brightness":180,
                           "white_value":200,
                           "friendly_name":"Bed Light"
                        },
                        "last_updated":"2016-11-26T01:37:24.265390+00:00"
                     },
                     "old_state":{
                        "entity_id":"light.bed_light",
                        "last_changed":"2016-11-26T01:37:10.466994+00:00",
                        "state":"off",
                        "attributes":{
                           "supported_features":147,
                           "friendly_name":"Bed Light"
                        },
                        "last_updated":"2016-11-26T01:37:10.466994+00:00"
                     }
                  },
                  "event_type":"state_changed",
                  "time_fired":"2016-11-26T01:37:24.265429+00:00",
                  "origin":"LOCAL"
               }
            }
        """.trimIndent()
        val stateChangedResponse: StateChangedResponse = get<ObjectMapper>().fromJson(eventResultJson)

        stateChangeEvent.unsubscribe("handle")
        stateChangeEvent.emit(stateChangedResponse)
        delay(1)
        assertThat(testValueOne).isNull()
        assertThat(stateChangedResponse).isEqualTo(testValueTwo)
    }
}
