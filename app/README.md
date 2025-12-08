# Android App

A native Android application for the barber shop simulation.

_Note that the bonus Play/Pause feature and the ability to edit the timescale factor aren't implemented yet.
The UI shows the Play/Pause button but for now it only starts the simulation._

# Screenshots

<table>
  <tr>
    <td><img width="250" src="https://github.com/user-attachments/assets/4c55a1bb-9f7e-41ac-9f5e-0f8a1e9abbb0" /></td>
    <td><img width="250" src="https://github.com/user-attachments/assets/eb11705e-202f-432f-bf8f-aec41a042c74" />
</td>
    <td>
Legend:
      
  - Top left - RWB cuts - is the shop name
  - ↗️ - Incoming customer.
  - ↖️ - Outgoing customer.
  - ⬛️ - Customer id.
  - 🟩 - Barber (8 possible: A through H)
  - ↺ - Restart.

  </td>
  </tr>
</table>



## Setup

- Built with IntelliJ IDEA
- Minimum OS support: Android 15

## Build & Run

```bash
./gradlew installDebug
```

If you don't have java, you can install [the apk](https://github.com/mir-uddin/JobyShopSim/releases/tag/v2-UI-prerelease) directly on your android device/emulator.

## Project Structure

```
app/
├── src/main/java/     # Java source code
├── src/main/res/      # Resources (layouts, drawables, strings)
└── build.gradle       # App-level dependencies
```

## Dependencies

- AndroidX
