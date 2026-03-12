package dev.efelleto.jeb.command

import dev.efelleto.jeb.database.ConfigManager
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.components.actionrow.ActionRow
import net.dv8tion.jda.api.components.buttons.Button
import net.dv8tion.jda.api.components.selections.EntitySelectMenu
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.InteractionHook
import java.awt.Color

class JebCommand : ListenerAdapter() {

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        if (event.name != "jeb") return

        val guild = event.guild ?: return
        val member = event.member ?: return
        event.deferReply(true).queue()

        val config = ConfigManager.getConfig(guild.id)
        val staffRoleId = config.staffRoleId
        val isAdmin = member.hasPermission(Permission.ADMINISTRATOR)
        val isStaff = staffRoleId != null && member.roles.any { it.id == staffRoleId }

        if (!isAdmin && !isStaff) {
            event.hook.sendMessage("❌ **»** You don't have permission to use this command.")
                .setEphemeral(true).queue()
            return
        }

        renderPanel(event.hook, guild.id)
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        val id = event.componentId
        event.guild ?: return

        when (id) {
            "jeb_set_permission_role" -> {
                val menu = EntitySelectMenu.create("jeb_select_permission_role", EntitySelectMenu.SelectTarget.ROLE)
                    .setPlaceholder("Select the role allowed to use /color")
                    .setRequiredRange(1, 1)
                    .build()

                event.reply("Select the **Permission Role**. Members with this role will be able to use `/color`:")
                    .setComponents(ActionRow.of(menu))
                    .setEphemeral(true)
                    .queue()
            }

            "jeb_set_staff_role" -> {
                val menu = EntitySelectMenu.create("jeb_select_staff_role", EntitySelectMenu.SelectTarget.ROLE)
                    .setPlaceholder("Select the role allowed to use /jeb")
                    .setRequiredRange(1, 1)
                    .build()

                event.reply("Select the **Staff Role**. Members with this role will be able to use `/jeb`:")
                    .setComponents(ActionRow.of(menu))
                    .setEphemeral(true)
                    .queue()
            }

            "jeb_set_allowed_channel" -> {
                val menu = EntitySelectMenu.create("jeb_select_allowed_channel", EntitySelectMenu.SelectTarget.CHANNEL)
                    .setPlaceholder("Select the color command channel")
                    .setRequiredRange(1, 1)
                    .build()

                event.reply("Select the **Allowed Channel**. Members will only be able to use `/color` there:")
                    .setComponents(ActionRow.of(menu))
                    .setEphemeral(true)
                    .queue()
            }

            "jeb_refresh" -> {
                event.deferEdit().queue()
                renderPanel(event.hook, event.guild!!.id)
            }
        }
    }

    override fun onEntitySelectInteraction(event: EntitySelectInteractionEvent) {
        val id = event.componentId
        val guild = event.guild ?: return

        when (id) {
            "jeb_select_permission_role" -> {
                event.deferReply(true).queue()
                val role = event.mentions.roles[0]
                ConfigManager.setPermissionRole(guild.id, role.id)
                event.hook.sendMessage("✅ **»** Permission Role set to ${role.asMention}.")
                    .setEphemeral(true).queue()
            }

            "jeb_select_staff_role" -> {
                event.deferReply(true).queue()
                val role = event.mentions.roles[0]
                ConfigManager.setStaffRole(guild.id, role.id)
                event.hook.sendMessage("✅ **»** Staff Role set to ${role.asMention}.")
                    .setEphemeral(true).queue()
            }

            "jeb_select_allowed_channel" -> {
                event.deferReply(true).queue()
                val channel = event.mentions.channels[0]
                ConfigManager.setAllowedChannel(guild.id, channel.id)
                event.hook.sendMessage("✅ **»** Allowed Channel set to ${channel.asMention}.")
                    .setEphemeral(true).queue()
            }
        }
    }

    private fun renderPanel(hook: InteractionHook, guildId: String) {
        val config = ConfigManager.getConfig(guildId)

        val permissionRole = if (config.permissionRoleId != null) "<@&${config.permissionRoleId}>" else "`Not configured`"
        val staffRole = if (config.staffRoleId != null) "<@&${config.staffRoleId}>" else "`Not configured`"
        val allowedChannel = if (config.allowedChannelId != null) "<#${config.allowedChannelId}>" else "`All channels`"

        val embed = EmbedBuilder()
            .setTitle("🐑 Jeb | Configuration Panel")
            .setDescription("Manage the bot settings for this server.")
            .setColor(Color.decode("#2596be"))
            .addField("🎨 Permission Role", "$permissionRole\nMembers with this role can use `/color`.", false)
            .addField("🛡️ Staff Role", "$staffRole\nMembers with this role can use `/jeb`.", false)
            .addField("📍 Allowed Channel", "$allowedChannel\nChannel where `/color` is allowed.", false)
            .setFooter("Jeb. Every server deservers a little color.")
            .build()

        hook.editOriginalEmbeds(embed)
            .setComponents(
                ActionRow.of(
                    Button.secondary("jeb_set_permission_role", "🎨 Role"),
                    Button.secondary("jeb_set_staff_role", "🛡️ Staff"),
                    Button.secondary("jeb_set_allowed_channel", "📍 Channel")
                ),
                ActionRow.of(Button.primary("jeb_refresh", "🔄 Refresh"))
            ).queue()
    }
}