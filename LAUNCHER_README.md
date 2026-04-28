# Taztozi Block Launcher — MCPE Mod & Texture Pack Manager

## ⚖️ LEGAL NOTICE

**This is a STANDALONE LAUNCHER APPLICATION** — NOT a modified version of Minecraft PE.

- ✅ Works with legitimate MCPE installations you own
- ✅ Does NOT modify Minecraft PE source code
- ✅ Does NOT distribute Minecraft PE
- ✅ Complies with Mojang's terms of service

**Original Minecraft PE © Mojang AB / Microsoft**

This launcher is a fan-created utility for managing community mods and texture packs.

---

## What This Is

A lightweight Android launcher app that:
- 📱 Manages mod files (.so libraries)
- 🎨 Manages texture packs (.zip files)
- 🚀 Launches MCPE with selected mods/packs loaded
- ⚙️ Provides mod configuration UI
- 📊 Shows mod status and dependencies
- 💾 Persists enabled/disabled state

## Project Structure

```
launcher/
├── src/
│   ├── main/
│   │   ├── AndroidManifest.xml
│   │   ├── res/
│   │   │   ├── layout/
│   │   │   ├── drawable/
│   │   │   ├── values/
│   │   │   └── menu/
│   │   └── java/com/taztozi/launcher/
│   │       ├── MainActivity.java
│   │       ├── LauncherService.java
│   │       ├── ModManager.java
│   │       ├── TexturePackManager.java
│   │       ├── ui/
│   │       │   ├── ModListFragment.java
│   │       │   ├── TexturePackFragment.java
│   │       │   ├── SettingsFragment.java
│   │       │   └── ModDetailActivity.java
│   │       ├── models/
│   │       │   ├── Mod.java
│   │       │   └── TexturePack.java
│   │       ├── utils/
│   │       │   ├── FileUtils.java
│   │       │   ├── ZipUtils.java
│   │       │   ├── PreferenceManager.java
│   │       │   └── Logger.java
│   │       └── services/
│   │           ├── ModLoadService.java
│   │           ├── TexturePackService.java
│   │           └── LaunchService.java
│   └── test/
├── build.gradle
├── gradle.properties
└── settings.gradle
```

## Features

- **Mod Management**
  - Discover mods from `/storage/emulated/0/Taztozi/mods/`
  - Enable/disable mods without recompiling
  - View mod dependencies
  - Read mod metadata (name, version, author, description)
  - Enable/disable with one tap

- **Texture Pack Management**
  - Discover texture packs from `/storage/emulated/0/Taztozi/texturepacks/`
  - Apply single or multiple packs (layered)
  - Pack resolution detection (16x, 32x, 64x, etc.)
  - Live preview of active pack

- **Launcher**
  - Detects installed MCPE versions
  - Injects mod/texture pack paths via environment variables
  - Launches MCPE with mod system active
  - Minimal footprint — doesn't modify MCPE files

---

## Getting Started

### Prerequisites
- Android Studio 4.2+
- Android SDK 28+
- Gradle 7.0+

### Build
```bash
cd launcher
./gradlew build
./gradlew installDebug
```

### Install
Side-load the APK or install via Android Studio.

---

## How to Create Mods

See [MOD_API.md](./MOD_API.md) for the complete mod development guide.

Quick start:
1. Create a C++ project targeting Android ARM64
2. Include `ModAPI.h`
3. Implement the mod interface functions
4. Compile to `.so` shared library
5. Copy to `/storage/emulated/0/Taztozi/mods/`
6. Enable in launcher

---

## File Structure on Device

```
/storage/emulated/0/Taztozi/
├── mods/                          # Drop .so files here
│   ├── libsupertools.so
│   ├── libnightvision.so
│   ├── libautofarm.so
│   └── libcustomblocks.so
├── texturepacks/                  # Drop .zip files here
│   ├── faithful.zip
│   ├── realistic.zip
│   └── custom.zip
└── config/
    ├── launcher_config.json       # Launcher settings
    ├── mods.json                  # Mod enable/disable state
    └── texturepack_state.json     # Active texture pack
```

---

## License

This launcher application is provided as-is for personal use.

**Minecraft PE is a trademark of Mojang AB / Microsoft.**

We respect their intellectual property and recommend purchasing the official game.

---

## Support

For issues or questions:
- Check [TROUBLESHOOTING.md](./TROUBLESHOOTING.md)
- Open an issue on GitHub
- Contact: not-affiliated-with-mojang@example.com
