package dev.efelleto.jeb

import dev.efelleto.jeb.command.ColorCommand
import dev.efelleto.jeb.command.JebCommand
import dev.efelleto.jeb.database.ConfigManager
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.requests.GatewayIntent
import java.io.PrintStream

private const val RESET = "\u001B[0m"
private const val LIGHT_BLUE = "\u001B[36m"
private const val WHITE = "\u001B[37m"
private const val RED = "\u001B[31m"
private const val GRAY = "\u001B[90m"
private const val BOLD = "\u001B[1m"
private const val SEPARATOR = "\u001B[90m__________________________________________________________________________________\u001B[0m"

fun main() {
    System.setOut(PrintStream(System.out, true, "UTF-8"))
    System.setErr(PrintStream(System.err, true, "UTF-8"))

    // Try to obtain the token from the environment variables first (Fallback).
    val envToken = System.getenv("BOT_TOKEN")

    val token = if (envToken != null && envToken.isNotBlank()) {
        log("ENV", "Using token from environment variables.")
        envToken
    } else {
        // If the variable does not exist, the original setup flow from the file is followed.
        if (!ConfigManager.setup()) return
        ConfigManager.loadToken()
    }

        // Security check to ensure the token is valid
        if (token == null || token.isEmpty() || token == "YOUR_BOT_TOKEN_HERE") {
        logError("AUTH", "Token not found! Set BOT_TOKEN environment variable or edit config.yml.")
        return
    }

    showBanner()

    val jda = JDABuilder.createDefault(token)
        .enableIntents(GatewayIntent.GUILD_MEMBERS)
        .setActivity(net.dv8tion.jda.api.entities.Activity.playing("/color"))
        .addEventListeners(
            JebCommand(),
            ColorCommand()
        )
        .build()

    jda.awaitReady()

    jda.updateCommands().addCommands(
        Commands.slash("jeb", "Open the Jeb configuration panel.")
            .setDefaultPermissions(DefaultMemberPermissions.DISABLED),

        Commands.slash("color", "Set your custom color role.")
            .addOption(OptionType.STRING, "hex", "The hex color code (e.g. #2596be)", true)
    ).queue()

    log("BOT", "Jeb is online. Commands registered.")
}

fun log(prefix: String, message: String) {
    println("$WHITE[$LIGHT_BLUE INFO $WHITE] $LIGHT_BLUE$prefix: $WHITE$message$RESET")
}

fun logError(prefix: String, message: String) {
    println("$WHITE[$RED ERROR $WHITE] $RED$prefix: $WHITE$message$RESET")
}

private fun showBanner() {
    println(SEPARATOR)
    println("""
$LIGHT_BLUE⠀⠀⠀⠀⠀⠀⠀⠀⢀⣀⢴⢺⠳⣭⢟⠢⡀
$LIGHT_BLUE⠀⠀⠀⠀⠀⠀⡔⠉⠀⡰⠺⣍⡿⢞⣖⢓⢻⡀
$LIGHT_BLUE⠀⠀⠀⠀⠀⠀⠑⠀⢊⣄⢀⡈⠓⢺⡀⡾⢼⢀⣀
$LIGHT_BLUE⠀⠀⠀⠀⠀⠀⠀⠀⡏⠉⠙⠀⢰⡀⡀⡠⠘⣭⣽⢿⣯⡖⢦⡀
$LIGHT_BLUE⠀⠀⠀⠀⠀⠀⠀⠀⢡⢠⡀⠀⠀⣩⡞⡇⠀⣟⣖⢹⢭⣏⡏⣽⡀
$LIGHT_BLUE⠀⠀⠀⠀⠀⠀⠀⠀⡆⡛⣶⣲⣪⡟⡿⢍⣾⣞⣩⡟⠷⠔⢈⣄⠇
$LIGHT_BLUE⠀⠀⠀⠀⠀⠀⠀⠀⢹⣻⣌⣖⡿⢮⢗⢵⣻⠧⣾⣮⣭⡽⡦⣼⠂
$LIGHT_BLUE⠀⠀⠀⠀⠀⠀⠀⠀⠀⢿⣦⠦⠓⣔⣣⠭⣞⣳⠾⢏⣧⣛⠏
$LIGHT_BLUE⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠘⡏⡏⡇⡵⠋⠁⠈⡍⡏⡏⢹
$LIGHT_BLUE⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠷⠇⠷⡦⠀⠀⠀⠷⠇⠱⠾    $BOLD${LIGHT_BLUE}Jeb  $RESET$GRAY"Every server deserves a little color."
    """.trimIndent())
    println(SEPARATOR)
    println()
}