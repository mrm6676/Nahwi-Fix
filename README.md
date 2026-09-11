# NahwiFix - Android Application

NahwiFix is a modern Arabic grammar and punctuation checker built for Android using Kotlin and Jetpack Compose.

## Features

- **Instant Arabic Grammar & Syntax Checking**: Detects and highlights errors in real time, including Hamzat Al-Wasl and Al-Qat', Tanween Al-Fath, subject-verb gender agreement, and accusative/genitive cases.
- **Arabic Punctuation Rules**: Corrects Arabic commas (`،`), Arabic question marks (`؟`), spacing around conjunctions (`و`), and misplaced punctuation.
- **Detailed Correction Inspector**: Inspects every issue with the original error snippet, suggested replacement, grammatical reason, and authoritative rule reference.
- **One-Click Corrections**: Apply corrections individually or apply all suggested fixes simultaneously.
- **Local Persistence with Room**: Stores past check history securely and offline using Room Database.
- **Full Localization & RTL Support**: Native Right-To-Left (RTL) Arabic layout with an English bilingual toggle.
- **Material 3 Theming**: Emerald and gold palette with system-aware Dark/Light mode and edge-to-edge layout.
- **Comprehensive Secondary Views**: Includes Grammar Rules Guide, Flexible Pricing Plans (Basic / Pro / Enterprise), Account authentication, and Privacy Terms.

## Tech Stack

- **Platform**: Android SDK 36 (Min SDK 26)
- **Language**: Kotlin 2.0.21
- **UI Toolkit**: Jetpack Compose with Material 3
- **Architecture**: MVVM with Repository Pattern and StateFlow
- **Database**: AndroidX Room with KSP
- **Build System**: Gradle 9.3.1 with Kotlin DSL
