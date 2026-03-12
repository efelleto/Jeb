# 🐑 Jeb: Discord Color Bot

[![Website](https://img.shields.io/badge/Website-Live-2596be?style=for-the-badge&logo=google-chrome&logoColor=white)](https://efelleto.github.io/Jeb/)
![JDA](https://img.shields.io/badge/JDA-5.x-blue?style=for-the-badge&logo=discord)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-purple?style=for-the-badge&logo=kotlin)

**Jeb** is a lightweight, efficient Discord bot designed to give your community a splash of personality. It allows users with specific permissions to create and manage their own unique color roles via hex codes.

> **Check out the official website:** [efelleto.github.io/Jeb](https://efelleto.github.io/Jeb/)

---

## Features

* **Custom Hex Colors:** Users can set their name color using standard hex codes (e.g., `#2596be`).
* **Automatic Role Management:** Jeb creates, assigns, and cleans up old color roles automatically to keep the server list tidy.
* **Smart Hierarchy:** Automatically places new color roles just above the bot's highest role to ensure they remain visible.
* **Admin Dashboard:** A sleek, button-based configuration panel for server staff.
* **Allowed Channels:** Restrict the `/color` command to a specific channel to keep your chat clean.
* **Permission-Based:** Control exactly who can change colors and who can manage the bot settings.

---

## Commands

| Command | Description | Permissions |
| :--- | :--- | :--- |
| `/color <hex>` | Set your personal role color. | Configurable Permission Role |
| `/jeb` | Open the configuration panel. | Administrator or Staff Role |

---

## Setup & Installation

### Prerequisites
* **Java 17** or higher.
* A Discord Bot Token (from the [Discord Developer Portal](https://discord.com/developers/applications)).

### Running the bot
1.  **Download** the latest release or clone the repository.
2.  **Run** the jar for the first time to generate the `config.yml` file:
    ```bash
    java -jar jeb-1.0.0.jar
    ```
3.  **Configure** your token in `config.yml`:
    ```yaml
    token: "YOUR_BOT_TOKEN_HERE"
    ```
4.  **Restart** the bot.
5.  **Important:** In Discord, ensure the **Jeb** role is positioned **above** any roles you want it to manage, and grant it the `Manage Roles` permission.

---

## Configuration

Once the bot is online, use `/jeb` to:
1.  **Set Permission Role:** Choose which role is allowed to use the `/color` command.
2.  **Set Staff Role:** Choose which role (other than Administrators) can manage Jeb's settings.
3.  **Set Allowed Channel:** (Optional) Select a specific channel where the `/color` command can be used.

---

## Built With

* [JDA (Java Discord API)](https://github.com/discord-jda/JDA) - For Discord interaction.
* [Kotlin](https://kotlinlang.org/) - Modern and concise programming language.
* [Gson](https://github.com/google/gson) - For persistent data storage.
* [SnakeYAML](https://bitbucket.org/snakeyaml/snakeyaml/src/master/) - For configuration management.

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
