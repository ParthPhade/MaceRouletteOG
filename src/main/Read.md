# MaceRouletteOG 🎲🔨

[![Paper 1.21+](https://img.shields.io/badge/Paper-1.21+-blue.svg)](https://papermc.io/)
[![Folia Supported](https://img.shields.io/badge/Folia-Supported-green.svg)](https://papermc.io/folia)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

**MaceRouletteOG** is a high-performance, multithreaded Minecraft minigame plugin inspired by the viral Mace Roulette trend. Built specifically for **Paper 1.21+** and fully optimized for **Folia's multithreaded region scheduling**, it delivers zero-lag spinning wheel animations, custom chaos round modifiers, and a streamlined interactive arena setup system.

---

## ✨ Key Features

* **Native Paper & Folia Support:** Utilizes Folia's `RegionScheduler` and `EntityScheduler` APIs. Each arena operates independently on its own chunk region tick thread with zero main-thread lockups.
* **Client-Side Display Entity Animations:** Uses 1.21 `ItemDisplay` entities combined with matrix interpolation for buttery-smooth, high-FPS roulette spinning with minimal server overhead.
* **Interactive Admin Setup Wizard:** No complicated coordinate math in chat. Grab the setup wand, click your arena bounds, set your wheel center, and configure everything through an intuitive workflow.
* **Advanced Chaos Modifiers:** Powered by a weighted random selector with anti-repeat history. Includes built-in modifiers like *Ant-Man (Tiny)*, *Titan Mode*, and *Moon Gravity* using native 1.21 player attributes (`SCALE`, `GRAVITY`).
* **Zero-Lag Block Rollbacks:** In-memory dirty-block snapshot queue restores modified arena blocks instantly at the end of rounds without WorldEdit lag.
* **Async Storage & Ecosystem:** HikariCP connection pooling for fast SQLite/MySQL player statistics, with full Kyori Adventure MiniMessage formatting and optional PlaceholderAPI support.

---

## 📥 Installation & Requirements

1. **Requirements:**
   * Java 21 or higher
   * A Paper or Folia 1.21+ server instance
2. **Setup:**
   * Download the latest `MaceRouletteOG-1.0.0-SNAPSHOT.jar` from the releases page.
   * Drop the `.jar` into your server's `plugins/` directory.
   * Start or restart your server to generate default configurations.

---

## 🛠️ Configuration Files

MaceRouletteOG comes with three fully customizable configuration files in your plugin directory (`plugins/MaceRouletteOG/`):

* `config.yml`: Global settings, match durations, database credentials, and audio cues.
* `messages.yml`: 100% Kyori Adventure MiniMessage support with full gradient and placeholder support.
* `modifiers.yml`: Toggle, weights, and parameters for every chaos round modifier.

---

## 🕹️ Commands & Permissions

### Player Commands
* `/mrog join <arena>` — Join an active arena lobby.
* `/mrog leave` — Leave your current arena match.

### Admin Commands (`macerouletteog.admin`)
* `/mrog reload` — Reload all configuration files.
* `/mrogsetup <arenaName>` — Gives you the interactive Setup Wand.
* `/mrogsetup <arenaName> save` — Finalizes and enables your configured arena.

### Permissions
* `macerouletteog.player` (Default: `true`) — Allows joining and playing arenas.
* `macerouletteog.admin` (Default: `op`) — Allows creating arenas, reloading, and managing setups.

---

## 📊 Placeholders (PlaceholderAPI)

If PlaceholderAPI is installed, MaceRouletteOG exposes the following expansion:
* `%macerouletteog_arenas_count%` — Returns the total number of loaded arenas.

---

## 🤝 Contributing

Contributions, bug reports, and feature requests are always welcome! Feel free to open an issue or submit a pull request on GitHub.

1. Fork the project.
2. Create your feature branch (`git checkout -b feature/AmazingFeature`).
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

---

## 📝 License

Distributed under the MIT License. See `LICENSE` for more information.