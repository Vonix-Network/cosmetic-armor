# Cosmetic Armor

Standalone Minecraft Forge 1.18.2 mod by Vonix.Network, kept separate from XP Skill Tree.

## Reference-informed fixes in 1.1.0

The implementation follows the public behavior of Cosmetic Armor Reworked without copying its source or assets:

- Four persistent cosmetic slots mapped to feet, legs, chest, and head
- Cosmetic armor is visual-only and never changes the player's actual equipment stats
- Per-slot visibility switches plus a global visibility switch
- Per-player client cache, so other players' cosmetics render correctly in multiplayer
- Server-to-client synchronization on login, respawn, dimension changes, and every slot update
- Main-hand assignment validates the armor slot and copies one item without consuming it
- Inventory controls remain integrated into the vanilla inventory screen
- Render state is always restored after each player render and cleared on logout
- Cosmetic items persist through player save data and death/respawn capability cloning

## Inventory controls

Open the normal inventory. Use the controls to:

- Toggle all cosmetic armor on or off
- Copy the main-hand armor item into Boots, Legs, Chest, or Helm
- Clear an individual cosmetic slot

## Build

Requires Java 17:

```bash
JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 ./gradlew clean build
```

Artifact: `build/libs/cosmetic-armor-forge-1.18.2-1.1.0+1.18.2.jar`
