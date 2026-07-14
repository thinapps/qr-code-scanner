package top.thinapps.qrcodescanner

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class ScanHistoryItem(
    val value: String,
    val scannedAtMs: Long,
    val openableWebLink: Boolean
)

object ScanHistoryRepository {
    private const val PREFERENCES_NAME = "scan_history"
    private const val KEY_ITEMS = "items"
    private const val KEY_VALUE = "value"
    private const val KEY_SCANNED_AT_MS = "scannedAtMs"
    private const val KEY_OPENABLE_WEB_LINK = "openableWebLink"
    private const val MAX_HISTORY_ITEMS = 50

    fun getItems(context: Context): List<ScanHistoryItem> {
        val preferences = context.applicationContext.getSharedPreferences(
            PREFERENCES_NAME,
            Context.MODE_PRIVATE
        )
        val savedJson = preferences.getString(KEY_ITEMS, null) ?: return emptyList()
        val parsedItems = runCatching { JSONArray(savedJson) }.getOrNull() ?: return emptyList()
        val resolvedItems = mutableListOf<ScanHistoryItem>()
        val seenValues = mutableSetOf<String>()

        for (index in 0 until parsedItems.length()) {
            val itemJson = parsedItems.optJSONObject(index) ?: continue
            val value = itemJson.optString(KEY_VALUE, "")
            if (value.isBlank() || value in seenValues) continue

            seenValues += value
            resolvedItems += ScanHistoryItem(
                value = value,
                scannedAtMs = itemJson.optLong(KEY_SCANNED_AT_MS, 0L),
                openableWebLink = itemJson.optBoolean(KEY_OPENABLE_WEB_LINK, false)
            )
        }

        return resolvedItems.take(MAX_HISTORY_ITEMS)
    }

    fun record(context: Context, value: String, openableWebLink: Boolean) {
        if (value.isBlank()) return

        val updatedItems = listOf(
            ScanHistoryItem(
                value = value,
                scannedAtMs = System.currentTimeMillis(),
                openableWebLink = openableWebLink
            )
        ) + getItems(context).filter { item -> item.value != value }

        saveItems(context, updatedItems.take(MAX_HISTORY_ITEMS))
    }

    fun clear(context: Context) {
        context.applicationContext
            .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_ITEMS)
            .apply()
    }

    private fun saveItems(context: Context, items: List<ScanHistoryItem>) {
        val itemsJson = JSONArray()
        items.forEach { item ->
            itemsJson.put(
                JSONObject()
                    .put(KEY_VALUE, item.value)
                    .put(KEY_SCANNED_AT_MS, item.scannedAtMs)
                    .put(KEY_OPENABLE_WEB_LINK, item.openableWebLink)
            )
        }

        context.applicationContext
            .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_ITEMS, itemsJson.toString())
            .apply()
    }
}
