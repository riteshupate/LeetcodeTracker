package com.leetcode.tracker.api

import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Calendar
import java.util.concurrent.TimeUnit

data class LeetCodeUserData(
    val totalSolved: Int,
    val easyCount: Int,
    val mediumCount: Int,
    val hardCount: Int,
    val submissionCalendar: Map<String, Int>
)

class LeetCodeApi {
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val gson = Gson()
    
    suspend fun getUserSubmissions(username: String): LeetCodeUserData? = withContext(Dispatchers.IO) {
        try {
            val query = """
                {
                    matchedUser(username: "$username") {
                        submitStats {
                            acSubmissionNum {
                                difficulty
                                count
                            }
                        }
                        submissionCalendar
                    }
                }
            """.trimIndent()
            
            val json = JsonObject().apply {
                addProperty("query", query)
            }
            
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = json.toString().toRequestBody(mediaType)
            
            val request = Request.Builder()
                .url("https://leetcode.com/graphql")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Referer", "https://leetcode.com")
                .addHeader("User-Agent", "Mozilla/5.0 (Android)")
                .build()
            
            val response = client.newCall(request).execute()
            
            if (!response.isSuccessful) {
                return@withContext null
            }
            
            val responseBody = response.body?.string() ?: return@withContext null
            
            val jsonResponse = gson.fromJson(responseBody, JsonObject::class.java)
            val matchedUser = jsonResponse.getAsJsonObject("data")
                ?.getAsJsonObject("matchedUser") ?: return@withContext null
            
            if (matchedUser.isJsonNull) {
                return@withContext null
            }

            // Extract submission calendar
            val calendarJson = matchedUser.get("submissionCalendar")?.asString
            val calendarMap = parseSubmissionCalendar(calendarJson)

            // Extract difficulty counts
            var totalSolved = 0
            var easyCount = 0
            var mediumCount = 0
            var hardCount = 0
            
            val submitStats = matchedUser.getAsJsonObject("submitStats")
            val acSubmissionNum = submitStats?.getAsJsonArray("acSubmissionNum")
            
            acSubmissionNum?.forEach { element ->
                val obj = element.asJsonObject
                when (obj.get("difficulty")?.asString) {
                    "All" -> totalSolved = obj.get("count").asInt
                    "Easy" -> easyCount = obj.get("count").asInt
                    "Medium" -> mediumCount = obj.get("count").asInt
                    "Hard" -> hardCount = obj.get("count").asInt
                }
            }
            
            return@withContext LeetCodeUserData(
                totalSolved = totalSolved,
                easyCount = easyCount,
                mediumCount = mediumCount,
                hardCount = hardCount,
                submissionCalendar = calendarMap
            )
            
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
    
    private fun parseSubmissionCalendar(calendarJson: String?): Map<String, Int> {
        if (calendarJson == null) return emptyMap()
        
        val result = mutableMapOf<String, Int>()
        
        try {
            val calendarData = gson.fromJson(calendarJson, JsonObject::class.java)
            
            calendarData.entrySet().forEach { (timestamp, countElement) ->
                try {
                    val timeInMillis = timestamp.toLong() * 1000
                    val count = countElement.asInt
                    
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = timeInMillis
                    
                    val dateKey = String.format(
                        "%d-%02d-%02d",
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH) + 1,
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                    
                    result[dateKey] = count
                } catch (e: Exception) {
                    // Skip invalid entries
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        return result
    }
}
