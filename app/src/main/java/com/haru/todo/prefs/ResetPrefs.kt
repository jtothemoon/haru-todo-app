import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey

object ResetPrefs {
    val LAST_RESET_TIME = longPreferencesKey("last_reset_time")
    val HOUR_KEY = intPreferencesKey("reset_hour")      // 추가
    val MINUTE_KEY = intPreferencesKey("reset_minute")  // 추가
    val ALLOW_NOTIFICATION = booleanPreferencesKey("allow_notification")
}
