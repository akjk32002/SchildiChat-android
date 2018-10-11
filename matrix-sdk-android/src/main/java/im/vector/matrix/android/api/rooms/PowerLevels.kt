package im.vector.matrix.android.api.rooms

import android.text.TextUtils
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import im.vector.matrix.android.api.events.EventType
import java.util.*

@JsonClass(generateAdapter = true)
data class PowerLevels(
        @Json(name = "ban") val ban: Int = 50,
        @Json(name = "kick") val kick: Int = 50,
        @Json(name = "invite") val invite: Int = 50,
        @Json(name = "redact") val redact: Int = 50,
        @Json(name = "events_default") val eventsDefault: Int = 0,
        @Json(name = "events") val events: MutableMap<String, Int> = HashMap(),
        @Json(name = "users_default") val usersDefault: Int = 0,
        @Json(name = "users") val users: MutableMap<String, Int> = HashMap(),
        @Json(name = "state_default") val stateDefault: Int = 50,
        @Json(name = "notifications") val notifications: Map<String, Any> = HashMap()
) {
    /**
     * Returns the user power level of a dedicated user Id
     *
     * @param userId the user id
     * @return the power level
     */
    fun getUserPowerLevel(userId: String): Int {
        // sanity check
        if (!TextUtils.isEmpty(userId)) {
            val powerLevel = users[userId]
            return powerLevel ?: usersDefault
        }

        return usersDefault
    }

    /**
     * Updates the user power levels of a dedicated user id
     *
     * @param userId     the user
     * @param powerLevel the new power level
     */
    fun setUserPowerLevel(userId: String?, powerLevel: Int) {
        if (null != userId) {
            users[userId] = Integer.valueOf(powerLevel)
        }
    }

    /**
     * Tell if an user can send an event of type 'eventTypeString'.
     *
     * @param eventTypeString the event type  (in Event.EVENT_TYPE_XXX values)
     * @param userId          the user id
     * @return true if the user can send the event
     */
    fun maySendEventOfType(eventTypeString: String, userId: String): Boolean {
        return if (!TextUtils.isEmpty(eventTypeString) && !TextUtils.isEmpty(userId)) {
            getUserPowerLevel(userId) >= minimumPowerLevelForSendingEventAsMessage(eventTypeString)
        } else false

    }

    /**
     * Tells if an user can send a room message.
     *
     * @param userId the user id
     * @return true if the user can send a room message
     */
    fun maySendMessage(userId: String): Boolean {
        return maySendEventOfType(EventType.MESSAGE, userId)
    }

    /**
     * Helper to get the minimum power level the user must have to send an event of the given type
     * as a message.
     *
     * @param eventTypeString the type of event (in Event.EVENT_TYPE_XXX values)
     * @return the required minimum power level.
     */
    fun minimumPowerLevelForSendingEventAsMessage(eventTypeString: String?): Int {
        return events[eventTypeString] ?: eventsDefault
    }

    /**
     * Helper to get the minimum power level the user must have to send an event of the given type
     * as a state event.
     *
     * @param eventTypeString the type of event (in Event.EVENT_TYPE_STATE_ values).
     * @return the required minimum power level.
     */
    fun minimumPowerLevelForSendingEventAsStateEvent(eventTypeString: String?): Int {
        return events[eventTypeString] ?: stateDefault
    }


    /**
     * Get the notification level for a dedicated key.
     *
     * @param key the notification key
     * @return the level
     */
    fun notificationLevel(key: String?): Int {
        if (null != key && notifications.containsKey(key)) {
            val valAsVoid = notifications[key]

            // the first implementation was a string value
            return if (valAsVoid is String) {
                Integer.parseInt(valAsVoid)
            } else {
                valAsVoid as Int
            }
        }

        return 50
    }
}