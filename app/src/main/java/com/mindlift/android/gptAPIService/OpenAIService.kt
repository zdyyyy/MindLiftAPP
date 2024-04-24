package com.mindlift.android.gptAPIService

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIApiService {
    @Headers("Content-Type: application/json",
        "Authorization: Bearer " +
                "GIVE IN YOUR KEY")

    @POST("chat/completions")
    fun getGPTResponse(@Body prompt: GPTRequestParameters): Call<GPTResponse>
}

data class GPTRequestParameters(
    val model: String = "gpt-3.5-turbo",
    val messages: List<GPTRequestBody>
)
data class GPTRequestBody(
    val role: String,
    val content: String
)
data class GPTResponse (
    val id: String,
    val obj: String,
    val created: Int,
    val model: String,
    val usage: Usage,
    val choices: List<Choice>
)

data class Usage (
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)

data class Choice (
    val message: Message,
    val logprobs: Any,
    val finish_reason: Any,
    val index: Int
)

data class Message (
    val role: String,
    val content: String
)
