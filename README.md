# 2025 Grid Game

A grid-based maze game written in **Java** with a **JavaFX** interface. Guide the player across a map, collect items, unlock obstacles that require specific items, and reach the goal. The game loads its level from a simple text map file, and its extra features are delivered as **plugins** that are discovered and loaded when the game starts.

## Features

- **Configurable levels** - grid size, start, goal, items and obstacles are all defined in a plain-text `.map` file (see [Map File Format](#map-file-format)).
- **Item and obstacle system** - obstacles can only be crossed if you are carrying the item they require. Picked-up items appear in the inventory panel.
- **Keyboard or mouse controls** - move with WASD or the on-screen buttons.
- **In-game calendar** - every move attempt advances the game date by one day, and a summary is printed to the console when you close the game.
- **Plugin architecture** - plugins implement a shared interface from the `:api` module and appear as buttons in the toolbar:
  - **Teleport** - warp to a random cell (single use).
  - **Reveal** - uncovers the map once you pick up the "Map" item.
  - **Prize** - rewards you with a special item after collecting enough items.
  - **Penalty** - adds a random obstacle if you take too long between moves.

## Prerequisites

- **JDK 11 or newer** (JDK 17 or 21 recommended). Check with `java -version`.
- **Internet access on first run** so Gradle can download JavaFX and the other dependencies.
- No separate Gradle install is needed - the included Gradle Wrapper (`gradlew` / `gradlew.bat`) handles it.

## Getting Started

Download or clone the repository, open a terminal in this project's folder (the one containing `gradlew`), then follow the instructions for your shell. The map file is passed to the game as a command-line argument; a sample level, `input_file.map`, is included.

### Bash (Linux / macOS / WSL / Git Bash)

```bash
# Check Java is installed
java -version

# Make the Gradle wrapper executable (first time only)
chmod +x gradlew

# Build and launch the game with the sample map
./gradlew run --args="input_file.map"
```

### PowerShell (Windows)

```powershell
# Check Java is installed
java -version

# Build and launch the game with the sample map
.\gradlew.bat run --args="input_file.map"
```

> **Important:** run the commands from the project root. The game looks for its plugins in `./plugins/build/classes/...`, a path relative to where you launch it. Gradle compiles the plugins automatically as part of `run`.

### Other useful commands

| Task | Bash | PowerShell |
|------|------|------------|
| Compile and run checks (including PMD) | `./gradlew build` | `.\gradlew.bat build` |
| Clean build output | `./gradlew clean` | `.\gradlew.bat clean` |
| Play a different map | `./gradlew run --args="my_level.map"` | `.\gradlew.bat run --args="my_level.map"` |

## How to Play

- Move with the **W / A / S / D** keys (up / left / down / right), or click the **`^`**, **`<`**, **`v`** and **`>`** buttons at the bottom of the window.
- Walk onto an item to pick it up; it will show in the inventory panel on the right.
- Obstacles block you until you hold the item they require.
- Click a plugin button in the top toolbar to activate that plugin.
- Close the window to end the game and print the total days elapsed to the console.

## Map File Format

Maps are plain text. The `size`, `start` and `goal` lines come first, followed by any number of `item` and `obstacle` blocks. Coordinates are written as `(x,y)`.

```
size (15,15)
start (1,5)
goal (9,8)

item "Wooden Sword" {
    at (0,0), (10,5), (7,1)
    message "Use wisely."
}

obstacle {
    at (1,1), (2,3), (3,4), (4,5)
    requires "Wooden Sword"
}
```

An obstacle with no requirement can never be crossed. Copy `input_file.map` as a starting point to design your own levels.

## Project Structure

```
2025_Grid_Game/
├── settings.gradle              # Multi-project build definition (:api, :plugins)
├── build.gradle                 # Root build config (application, JavaFX, PMD)
├── saed-pmd-rules.xml           # PMD static analysis rules
├── input_file.map               # Sample level
├── gradlew / gradlew.bat        # Gradle Wrapper scripts
├── gradle/wrapper/              # Gradle Wrapper files
│
├── src/main/
│   ├── java/edu/curtin/saed/assignment2/
│   │   └── App.java             # Entry point, JavaFX UI, plugin loader
│   └── resources/               # Sprites (player, goal, item, obstacle)
│
├── api/                         # Core game logic shared with plugins
│   ├── build.gradle
│   └── src/main/
│       ├── java/edu/curtin/saed/api/
│       │   ├── apiinterfaces/   # Plugin and game interfaces
│       │   ├── controllers/     # Game logic and data controllers
│       │   ├── fileio/          # Map file reader
│       │   └── model/           # Game state and entities (Player, Item, Obstacle, ...)
│       ├── antler4/edu/curtin/saed/assignment2/
│       │   └── GameConfig.g4    # ANTLR grammar for the map format
│       └── resources/           # Sprites
│
└── plugins/                     # Game plugins loaded at startup
    ├── build.gradle
    └── src/main/java/edu/curtin/saed/gameplugins/
        ├── Penalty.java
        ├── Prize.java
        ├── Reveal.java
        └── Teleport.java
```

The `bin/` and `build/` folders (and `.gradle/`) are generated by Gradle and can be ignored or deleted.

## Built With

- [Java](https://openjdk.org/) and [JavaFX](https://openjfx.io/) 19.0.2.1
- [Gradle](https://gradle.org/) 8.14.3 (via the wrapper)
- [ANTLR](https://www.antlr.org/) 4.13.1 (map grammar)
- [PMD](https://pmd.github.io/) 7.15.0 (static analysis)

## Troubleshooting

- **`Error: Plugin directory not found`** - you launched from the wrong folder, or the plugins haven't been compiled. Run from the project root using `./gradlew run --args="input_file.map"`.
- **Game crashes with an array index error on start** - no map file was supplied. Make sure `--args="input_file.map"` is included.
- **`Permission denied` on `./gradlew`** - run `chmod +x gradlew` first (Bash only).
- **`JAVA_HOME` errors** - install a JDK (11+) and make sure `JAVA_HOME` points to it.
