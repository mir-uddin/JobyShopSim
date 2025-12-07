# Android App

A native Android application for the barber shop simulation.

# Screenshot

<table>
  <tr>
    <td><img width="300" alt="screenshot-2025-12-07_18 36 30 29" src="https://github.com/user-attachments/assets/4c55a1bb-9f7e-41ac-9f5e-0f8a1e9abbb0" /></td>
    <td>
Legend:
      
  - Top left - RWB cuts - is the shop name
  - ↗️ - Incoming customer.
  - ↖️ - Outgoing customer.
  - ◻️ - Customer id.
  - 🟩 - Barber.
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

## Project Structure

```
app/
├── src/main/java/     # Java source code
├── src/main/res/      # Resources (layouts, drawables, strings)
└── build.gradle       # App-level dependencies
```

## Dependencies

- AndroidX
