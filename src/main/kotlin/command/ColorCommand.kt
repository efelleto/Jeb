package dev.efelleto.jeb.command

import dev.efelleto.jeb.database.ConfigManager
import dev.efelleto.jeb.log
import dev.efelleto.jeb.logError
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import java.awt.Color

class ColorCommand : ListenerAdapter() {

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name != "color") return

        val guild = event.guild ?: return
        val member = event.member ?: return
        event.deferReply(true).queue()

        val config = ConfigManager.getConfig(guild.id)
        val permissionRoleId = config.permissionRoleId

        if (permissionRoleId == null) {
            event.hook.sendMessage("⚠️ **»** Jeb has not been configured yet. Ask an admin to run `/jeb` first.")
                .setEphemeral(true).queue()
            return
        }

        val hasPermission = member.roles.any { it.id == permissionRoleId }
        if (!hasPermission) {
            event.hook.sendMessage("❌ **»** You don't have permission to use this command.")
                .setEphemeral(true).queue()
            return
        }

        val hex = event.getOption("hex")?.asString?.trim() ?: return
        val color = parseHex(hex)

        if (color == null) {
            event.hook.sendMessage("❌ **»** Invalid hex color. Please use the format `#RRGGBB` (e.g. `#2596be`).")
                .setEphemeral(true).queue()
            return
        }

        // Delete existing color role if the user already has one
        val existingColorRole = member.roles.find { it.name.startsWith("color.${member.user.id}") }
        existingColorRole?.delete()?.queue()

        val roleName = "color.${member.user.id}"
        guild.createRole()
            .setName(roleName)
            .setColor(color)
            .setMentionable(false)
            .setHoisted(false)
            .queue({ role ->
                val jebHighestRole = guild.selfMember.roles.maxByOrNull { it.position }

                if (jebHighestRole != null) {
                    val sortedRoles = guild.roles.sortedByDescending { it.position }
                    val jebIndex = sortedRoles.indexOfFirst { it.id == jebHighestRole.id }

                    if (jebIndex >= 0) {
                        guild.modifyRolePositions()
                            .selectPosition(role)
                            .moveTo(jebIndex + 1)
                            .queue(
                                { log("COLOR", "Color role assigned to ${member.user.name} — hex: $hex") },
                                { error -> logError("COLOR", "Failed to move role: ${error.message}") }
                            )
                    }
                }

                guild.addRoleToMember(member, role).queue({
                    event.hook.sendMessage("🎨 **»** Your color has been set to `$hex`!")
                        .setEphemeral(true).queue()
                }, { error ->
                    logError("COLOR", "Failed to assign role: ${error.message}")
                    event.hook.sendMessage("❌ **»** Failed to assign your color role. Make sure Jeb's role is above your color roles in the server settings.")
                        .setEphemeral(true).queue()
                })
            }, { error ->
                logError("COLOR", "Failed to create role: ${error.message}")
                event.hook.sendMessage("❌ **»** Failed to create your color role. Make sure Jeb has the `Manage Roles` permission.")
                    .setEphemeral(true).queue()
            })
    }

    private fun parseHex(hex: String): Color? {
        return try {
            val clean = hex.removePrefix("#")
            if (clean.length != 6) return null
            Color(
                clean.substring(0, 2).toInt(16),
                clean.substring(2, 4).toInt(16),
                clean.substring(4, 6).toInt(16)
            )
        } catch (e: Exception) {
            null
        }
    }
}