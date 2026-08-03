# Cosmetic Armor

Standalone Minecraft Forge 1.18.2 mod by Vonix.Network, kept separate from XP Skill Tree.

## Reference-informed fixes in 1.1.0

The implementation follows the public behavior of Cosmetic Armor Reworked without copying its source or assets:

- Four persistent cosmetic slots mapped directly beside the vanilla feet, legs, chest, and head armor slots
- Cosmetic armor is visual-only and never changes the player's actual equipment stats
- Tiny per-slot visibility switches plus a tiny global ON/OFF switch
- Per-player client cache, so other players' cosmetics render correctly in multiplayer
- Server-to-client synchronization on login, respawn, dimension changes, and every slot update
- Main-hand assignment validates the armor slot and copies one item without consuming it
- No replacement inventory screen and no large side panel, leaving JEI and the normal inventory usable
- Render state is always restored after player, hand, and arm render paths
- Cosmetic items persist with the player capability and clone through respawn

## Inventory controls

Open the normal inventory. The cosmetic slots appear immediately beside the normal armor slots and use the vanilla 18×18 armor-slot background:

- Click a cosmetic slot while holding matching armor in the main hand to copy it
- Click the tiny green/red control beside a cosmetic slot to show or hide that piece
- Click the tiny gray control beside a cosmetic slot to clear it
- Click the small `ON`/`OFF` control above the cosmetic column to toggle all cosmetics

JEI remains outside the inventory and is not covered by a custom panel.

## Build

Requires Java 17:

```bash
JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64 ./gradlew clean build
```

Artifact: `build/libs/cosmetic-armor-forge-1.18.2-1.1.0+1.18.2.jar`
