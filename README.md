# LeetCode Tracker

A beautiful, production-ready Android app to track your LeetCode coding journey with a 365-day activity heatmap, live statistics, home screen widget, and daily smart reminders.

![Android](https://img.shields.io/badge/Android-16-brightgreen)
![Kotlin](https://img.shields.io/badge/Kotlin-2.0.20-purple)
![Compose](https://img.shields.io/badge/Jetpack%20Compose-Latest-blue)
![License](https://img.shields.io/badge/License-Educational-orange)

## ✨ Features

### 📊 Core Features
- **365-Day Activity Heatmap** - GitHub-style visualization of your problem-solving activity
- **Live Statistics** - Real-time tracking of total problems solved, current streak, and difficulty breakdown
- **Home Screen Widget** - Quick access widget (3x2 to 5x2 resizable) with 10-minute auto-refresh
- **Smart Daily Reminders** - Get notified at your preferred time if you haven't solved a problem today
- **Offline Support** - Works gracefully even without internet connection

### 🎨 Design
- **Jetpack Compose UI** - Modern, responsive Material Design 3
- **Dark & Light Themes** - Automatic theme switching based on system settings
- **Zero XML Layouts** - 100% Compose-based UI
- **Smooth Animations** - Delightful user experience

### 🔧 Technical
- **Production-Ready** - Professional-grade error handling and logging
- **Secure** - DataStore encryption for user preferences
- **Fast** - Minimal memory footprint, efficient API calls
- **Well-Architected** - MVVM with ViewModel and Coroutines

## 📋 Requirements

- **Android 8.0 (API 26)** minimum
- **Android 16 (API 36)** target/recommended
- **JDK 17** or higher
- **Gradle 8.11.1**

## 🚀 Quick Start

### Option 1: Build Locally (Android Studio)

1. **Clone the repository**
   ```bash
   git clone <your-repo-url> LeetCodeTracker
   cd LeetCodeTracker
   ```

2. **Open in Android Studio**
   - File → Open → Select LeetCodeTracker folder
   - Wait for Gradle sync to complete

3. **Build and Run**
   - Build → Build Bundle(s) / APK(s) → Build APK(s)
   - Or click the ▶ Run button

### Option 2: Command Line Build

1. **Make gradlew executable**
   ```bash
   chmod +x gradlew
   ```

2. **Build Debug APK**
   ```bash
   ./gradlew assembleDebug
   ```
   Output: `app/build/outputs/apk/debug/app-debug.apk`

3. **Build Release APK**
   ```bash
   ./gradlew assembleRelease
   ```
   Output: `app/build/outputs/apk/release/app-release-unsigned.apk`

### Option 3: GitHub Actions (Automatic)

Just push to the repository and GitHub Actions will automatically:
- Build debug and release APKs
- Run tests and linting
- Upload artifacts for download

```bash
git push origin master
# Wait 5-10 minutes → Download from GitHub Actions artifacts
```

## 📱 Installation

1. **Transfer APK to your device**
   ```bash
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

2. **Or manually**
   - Download APK file
   - Transfer to Android device
   - Settings → Security → Enable "Unknown Sources"
   - Tap APK to install
   - Grant requested permissions

## 🎯 Usage Guide

### Initial Setup

1. **Open the app**
2. **Enter your LeetCode username** (case-sensitive)
3. **Tap "Save and Load Data"**
4. **Wait for data to fetch** (first load takes 5-10 seconds)

### Viewing Your Stats

- **Total Solved** - All problems across all difficulties
- **Current Streak** - Consecutive days with at least 1 problem solved
- **Difficulty Breakdown** - Easy, Medium, Hard problems
- **Activity Heatmap** - Scroll right to see your last 365 days

### Setting Daily Reminder

1. **Tap "Set Reminder Time"**
2. **Choose your preferred time** (e.g., 8:00 PM)
3. **Allow notification permission** (Android 13+)
4. **Done!** You'll get notified if you haven't solved a problem by that time

### Adding Home Widget

1. **Long-press on home screen**
2. **Tap "Widgets"**
3. **Find "LeetCode Tracker"**
4. **Drag to desired location**
5. **Resize as needed** (3x2 to 5x2)
6. **Tap widget to open app**

## 📊 Activity Heatmap Colors

- **Gray** - No problems solved
- **Dark Green** - 1 problem solved
- **Medium Green** - 2-3 problems solved
- **Light Green** - 4-5 problems solved
- **Bright Green** - 5+ problems solved

## 🔐 Permissions

The app requests the following permissions:

| Permission | Purpose |
|-----------|---------|
| `INTERNET` | Fetch data from LeetCode API |
| `POST_NOTIFICATIONS` | Send daily reminder notifications (Android 13+) |
| `SCHEDULE_EXACT_ALARM` | Precise reminder timing |
| `RECEIVE_BOOT_COMPLETED` | Restore reminders after device restart |

All permissions are optional except INTERNET. Reminders won't work without notification permission, but the app will function normally.

## 🛠️ Development

### Project Structure

```
src/main/
├── java/com/leetcode/tracker/
│   ├── MainActivity.kt              # Entry point
│   ├── api/
│   │   └── LeetCodeApi.kt          # GraphQL API client
│   ├── data/
│   │   └── UserRepository.kt       # DataStore preferences
│   ├── ui/
│   │   ├── TrackerScreen.kt        # Main Compose UI
│   │   ├── TrackerViewModel.kt     # State management
│   │   └── theme/
│   │       └── Theme.kt            # Material Design 3
│   ├── widget/
│   │   └── LeetCodeWidgetProvider.kt # Home widget
│   └── notifications/
│       └── DailyReminderReceiver.kt  # Reminder logic
└── res/
    ├── layout/
    │   └── widget_layout.xml
    ├── xml/
    │   └── widget_provider_info.xml
    └── values/
        ├── strings.xml
        └── themes.xml
```

### Key Technologies

| Technology | Version | Purpose |
|-----------|---------|---------|
| Jetpack Compose | Latest | Modern UI framework |
| Kotlin | 2.0.20 | Programming language |
| Android Gradle Plugin | 8.12.0 | Build system |
| Retrofit | 2.11.0 | HTTP client |
| OkHttp | 4.12.0 | Network layer |
| Coroutines | 1.8.1 | Async programming |
| DataStore | 1.1.2 | Secure preferences |
| Material Design 3 | Latest | UI components |

### Building from Source

1. **Clean build**
   ```bash
   ./gradlew clean
   ```

2. **Build with verbose output**
   ```bash
   ./gradlew assembleDebug --stacktrace --info
   ```

3. **Run lint checks**
   ```bash
   ./gradlew lint
   ```

4. **Generate ProGuard mapping**
   ```bash
   ./gradlew assembleRelease
   ```

## 🐛 Troubleshooting

### "User not found"
- Verify username is spelled correctly (case-sensitive)
- Make sure LeetCode profile is public
- Check internet connection

### Widget not updating
- Ensure `SCHEDULE_EXACT_ALARM` permission is granted
- Check app isn't in battery saver mode
- Manually tap widget to force refresh

### Reminder not received
- Grant `POST_NOTIFICATIONS` permission
- Check notification settings for app
- Verify time is set correctly
- Ensure app isn't killed by system

### App crashes on startup
- Clear app cache: Settings → Apps → LeetCode Tracker → Storage → Clear Cache
- Uninstall and reinstall app
- Check device has minimum API 26

### Build fails with "cannot find symbol"
- Run `./gradlew clean`
- Delete `.gradle` folder
- Re-sync Gradle files
- Check JDK version (must be 17+)

## 📈 Performance Tips

- **Use widget** instead of opening app for quick checks
- **Disable auto-refresh** in settings to save battery
- **Cache refreshes every 10 minutes** (configurable)
- **Minimal data usage** - only fetches changed data

## 🔄 API Rate Limits

- **Widget**: Updates every 10 minutes
- **App**: Updates on demand
- **Cache**: 10 minutes
- **LeetCode API**: Respects public rate limits

No issues with normal usage!

## 📦 Production Build

For Google Play Store or other distribution:

1. **Create signing key** (one-time)
   ```bash
   keytool -genkey -v -keystore leetcode.keystore \
     -keyalg RSA -keysize 2048 -validity 10000 \
     -alias release_key_alias
   ```

2. **Build release APK**
   ```bash
   ./gradlew assembleRelease
   ```

3. **Sign APK**
   ```bash
   jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
     -keystore leetcode.keystore \
     app/build/outputs/apk/release/app-release-unsigned.apk \
     release_key_alias
   ```

See `PRODUCTION.md` for detailed deployment guide.

## 🤝 Contributing

Want to improve LeetCode Tracker?

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is for educational purposes. LeetCode® is a trademark of LeetCode LLC.

## 📞 Support

**Having issues?**

1. Check **Troubleshooting** section above
2. Review **Android version** (must be API 26+)
3. Verify **internet connection**
4. Check **app permissions** in Settings
5. Try **reinstalling the app**

## 🙏 Acknowledgments

Built with:
- **Jetpack Compose** - Modern Android UI
- **Retrofit** - REST API client
- **OkHttp** - HTTP client
- **Kotlin Coroutines** - Async programming
- **Material Design 3** - Design system
- **LeetCode API** - Data source

## 📚 Resources

- [Jetpack Compose Documentation](https://developer.android.com/jetpack/compose)
- [Material Design 3](https://m3.material.io)
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [Android Developers](https://developer.android.com)
- [LeetCode](https://leetcode.com)

## 🎉 What's Included

✅ Full source code  
✅ GitHub Actions CI/CD  
✅ ProGuard obfuscation rules  
✅ Production documentation  
✅ API integration ready  
✅ Widget support  
✅ Notification system  
✅ Error handling  
✅ Material Design 3 theme  
✅ Offline support  

---

**Ready to track your LeetCode journey?** Download, build, and start using LeetCode Tracker today! 🚀

For questions or suggestions, open an issue on GitHub.

---

**Made with ❤️ for LeetCode enthusiasts**
