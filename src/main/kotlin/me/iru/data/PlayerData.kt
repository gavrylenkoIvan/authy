package me.iru.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.iru.Authy
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.HashSet

const val apiKeyHeader = "X-Api-Key"

class PlayerData {
    val authy = Authy.instance
    private val gson: Gson
    private val httpClient: OkHttpClient
    private val baseUrl: String
    private val apiKey: String

    private val mediaType = "application/json; charset=utf-8".toMediaType()

    init {
        baseUrl = "${authy.config.getString("block.url")!!}/api"
        apiKey = authy.config.getString("block.api_key")!!
        gson = Gson()
        httpClient = OkHttpClient()
    }

    fun getAll(): HashSet<AuthyPlayer> {
        val request = Request.Builder()
            .addHeader(apiKeyHeader, apiKey)
            .url("$baseUrl/user")
            .get().build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected error, code ${response.code}")

            return constructSet(response)
        }
    }

    fun create(p: Player, password: String, pin: String?) {
        val authyPlayer = AuthyPlayer(
            p.uniqueId,
            p.name,
            p.address?.address?.hostAddress!!,
            password,
            pin != null,
            pin,
        )

        val request = Request.Builder()
            .addHeader(apiKeyHeader, apiKey)
            .url("$baseUrl/user").post(
                toJson(authyPlayer)
            ).build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected error, code ${response.code}")
        }
    }

    fun delete(uuid: UUID): Boolean {
        val request = Request.Builder()
            .url("$baseUrl/user/$uuid")
            .addHeader(apiKeyHeader, apiKey)
            .delete().build()

        httpClient.newCall(request).execute().use { response ->
            return response.isSuccessful
        }
    }

    fun get(uuid: UUID): AuthyPlayer? {
        val request = Request.Builder()
            .url("$baseUrl/user?uuid=$uuid")
            .addHeader(apiKeyHeader, apiKey)
            .get().build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null

            return construct(response)
        }
    }

    fun get(username: String): AuthyPlayer? {
        val request = Request.Builder()
            .url("$baseUrl/user?username=$username")
            .addHeader(apiKeyHeader, apiKey)
            .get().build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) return null

            return construct(response)
        }
    }

    fun update(d: AuthyPlayer) {
        val request = Request.Builder()
            .url("$baseUrl/user")
            .addHeader(apiKeyHeader, apiKey)
            .patch(toJson(d)).build()

        httpClient.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected error, code ${response.code}")
        }
    }

    fun exists(uuid: UUID): Boolean {
        return get(uuid) != null
    }

    fun construct(response: Response): AuthyPlayer {
        return gson.fromJson(response.body?.string(), AuthyPlayer::class.java)
    }

    private fun constructSet(response: Response): HashSet<AuthyPlayer> {
        val type = object : TypeToken<HashSet<AuthyPlayer>>() {}.type

        return gson.fromJson(response.body?.string(), type)
    }

    private fun toJson(d: AuthyPlayer): RequestBody {
        return gson.toJson(d).toRequestBody(mediaType)
    }
}