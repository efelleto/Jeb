package dev.efelleto.jeb.database

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream

data class GuildConfig(
    val guildId: String,
    var permissionRoleId: String? = null,
    var staffRoleId: String? = null,
    var allowedChannelId: String? = null
)

object ConfigManager {
    private val gson = Gson()
    private val configFile = File("config.yml")
    private val dataFile = File("data.json")
    private val configs: MutableMap<String, GuildConfig> = loadAll()

    fun setup(): Boolean {
        if (!configFile.exists()) {
            configFile.writeText("""
                token: "YOUR_BOT_TOKEN_HERE"
            """.trimIndent())
            println("[ INFO ] CONFIG: File created at: ${configFile.absolutePath}")
            println("[ INFO ] CONFIG: Please fill config.yml and restart.")
            return false
        }
        return true
    }

    fun loadToken(): String {
        val yaml = Yaml()
        val data = yaml.load<Map<String, Any>>(FileInputStream(configFile))
        return data["token"].toString()
    }

    fun getConfig(guildId: String): GuildConfig {
        return configs.getOrPut(guildId) { GuildConfig(guildId) }
    }

    fun setPermissionRole(guildId: String, roleId: String) {
        getConfig(guildId).permissionRoleId = roleId
        save()
    }

    fun setStaffRole(guildId: String, roleId: String) {
        getConfig(guildId).staffRoleId = roleId
        save()
    }

    fun setAllowedChannel(guildId: String, channelId: String?) {
        getConfig(guildId).allowedChannelId = channelId
        save()
    }

    private fun save() {
        dataFile.writeText(gson.toJson(configs))
    }

    private fun loadAll(): MutableMap<String, GuildConfig> {
        if (!dataFile.exists()) return mutableMapOf()
        val type = object : TypeToken<MutableMap<String, GuildConfig>>() {}.type
        return gson.fromJson(dataFile.readText(), type) ?: mutableMapOf()
    }
}