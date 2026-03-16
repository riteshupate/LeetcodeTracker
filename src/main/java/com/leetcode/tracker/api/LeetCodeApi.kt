package com.leetcode.tracker.api

import android.util.Log
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
    private val tag = "LeetCodeApi"
    
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
            
            // Handle rate limiting (HTTP 429)
            if (response.code == 429) {
                Log.w(tag, "Rate limited by LeetCode API - HTTP 429")
                return@withContext null
            }
            
            // Handle other HTTP errors
            if (!response.isSuccessful) {
                Log.e(tag, "HTTP Error: ${response.code} - ${response.message}")
                return@withContext null
            }
            
            val responseBody = response.body?.string()
            if (responseBody.isNullOrEmpty()) {
                Log.e(tag, "Empty response body")
                return@withContext null
            }
            
            val jsonResponse = try {
                gson.fromJson(responseBody, JsonObject::class.java)
            } catch (e: Exception) {
                Log.e(tag, "Failed to parse JSON response", e)
                return@withContext null
            }
            
            // Check for GraphQL errors
            if (jsonResponse.has("errors")) {
                val errors = jsonResponse.get("errors")
                Log.e(tag, "GraphQL Error: $errors")
                return@withContext null
            }
            
            val dataObj = jsonResponse.getAsJsonObject("data") ?: run {
                Log.e(tag, "No data field in response")
                return@withContext null
            }
            
            val matchedUser = dataObj.getAsJsonObject("matchedUser")
            if (matchedUser == null || matchedUser.isJsonNull) {
                Log.w(tag, "User not found or profile is private: $username")
                return@withContext null
            }

            // Extract submission calendar
            val calendarJson = matchedUser.get("submissionCalendar")?.asString
            val calendarMap = if (!calendarJson.isNullOrEmpty()) {
                parseSubmissionCalendar(calendarJson)
            } else {
                emptyMap()
            }

            // Extract difficulty counts
            var totalSolved = 0
            var easyCount = 0
            var mediumCount = 0
            var hardCount = 0
            
            val submitStats = matchedUser.getAsJsonObject("submitStats")
            if (submitStats != null) {
                val acSubmissionNum = submitStats.getAsJsonArray("acSubmissionNum")
                
                acSubmissionNum?.forEach { element ->
                    try {
                        val obj = element.asJsonObject
                        val difficulty = obj.get("difficulty")?.asString ?: ""
                        val count = obj.get("count")?.asInt ?: 0
                        
                        when (difficulty) {
                            "All" -> totalSolved = count
                            "Easy" -> easyCount = count
                            "Medium" -> mediumCount = count
                            "Hard" -> hardCount = count
                        }
                    } catch (e: Exception) {
                        Log.w(tag, "Failed to parse submission count", e)
                    }
                }
            }
            
            Log.d(tag, "Successfully fetched data for user: $username (Total: $totalSolved, Easy: $easyCount, Medium: $mediumCount, Hard: $hardCount)")
            return@withContext LeetCodeUserData(
                totalSolved = totalSolved,
                easyCount = easyCount,
                mediumCount = mediumCount,
                hardCount = hardCount,
                submissionCalendar = calendarMap
            )
            
        } catch (e: Exception) {
            Log.e(tag, "Exception during API call", e)
            return@withContext null
        }
    }
    
    private fun parseSubmissionCalendar(calendarJson: String): Map<String, Int> {
        if (calendarJson.isBlank()) return emptyMap()
        
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
                    Log.w(tag, "Failed to parse calendar entry for timestamp: $timestamp", e)
                }
            }
        } catch (e: Exception) {
            Log.e(tag, "Exception parsing submission calendar", e)
        }
        
        return result
    }
}