# LeetCode Tracker - Production Guide

## Overview

LeetCode Tracker is a production-ready Android app built with Jetpack Compose, supporting Android 16 (API 36) and minimum Android 8.0 (API 26).

**Tech Stack:**
- Language: Kotlin 2.0.20
- UI: Jetpack Compose (Material Design 3)
- Networking: Retrofit + OkHttp
- Data: DataStore Preferences
- API: LeetCode GraphQL
- Build: Gradle 8.11.1, Android Gradle Plugin 8.12.0
- Notifications: AlarmManager + NotificationCompat

---

## Quick Start - Building for Production

### Prerequisites
- JDK 17+
- Android SDK with API 36
- Gradle 8.11.1

### Build Steps

1. **Clone and Setup**
```bash
git clone <your-repo-url>
cd LeetCodeTracker
chmod +x gradlew
```

2. **Build Debug APK** (for testing)
```bash
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

3. **Build Release APK** (unsigned, for Google Play)
```bash
./gradlew assembleRelease
# Output: app/build/outputs/apk/release/app-release-unsigned.apk
```

4. **Sign for Production**
```bash
# You'll need a keystore file (create once, reuse forever)
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
  -keystore leetcode.keystore \
  app/build/outputs/apk/release/app-release-unsigned.apk \
  release_key_alias
```

---

## GitHub Actions CI/CD

The `.github/workflows/build.yml` automatically:
- ✅ Builds debug & release APKs on push
- ✅ Uploads artifacts for download
- ✅ Runs linting & tests
- ✅ Archives build logs on failure

**Trigger builds by:**
```bash
git push origin master  # Automatic build
git tag v1.0.0 && git push origin v1.0.0  # Versioned release
```

---

## App Features

### Core Functionality
✅ **365-Day Activity Heatmap** - GitHub-style visualization  
✅ **Live Stats** - Total solved, current streak, difficulty breakdown  
✅ **Home Widget** - 3x2 to 5x2 responsive widget, 10-minute refresh  
✅ **Daily Reminders** - User-set time, only if no problems solved  
✅ **Offline Support** - Graceful fallback when internet unavailable  

### Permissions
- `INTERNET` - Fetch LeetCode data
- `POST_NOTIFICATIONS` - Android 13+ reminder notifications
- `SCHEDULE_EXACT_ALARM` - Precise reminder timing
- `RECEIVE_BOOT_COMPLETED` - Restore reminders after reboot

---

## API Integration

### LeetCode GraphQL API
```graphql
{
    matchedUser(username: "rpupate") {
        submitStats {
            acSubmissionNum {
                difficulty
                count
            }
        }
        submissionCalendar
    }
}
```

**Rate Limits:**
- Widget refreshes every 10 minutes (configurable)
- Cached for 10 minutes to reduce API calls
- Graceful error handling for rate limits

---

## Production Checklist

### Code Quality
- [x] Jetpack Compose UI (no XML layouts)
- [x] MVVM with ViewModel
- [x] Coroutines for async operations
- [x] Error handling & logging
- [x] ProGuard obfuscation enabled
- [x] Resource shrinking enabled

### Security
- [x] No hardcoded secrets
- [x] DataStore encryption for preferences
- [x] HTTPS only for API calls
- [x] Input validation on username
- [x] Safe exception handling

### Performance
- [x] 10-minute refresh interval (battery efficient)
- [x] Lazy loading of data
- [x] Efficient heatmap rendering
- [x] Minimal memory footprint
- [x] Cancellation of ongoing requests on app exit

### Testing
- [x] Built with production-grade libraries
- [x] Tested on Android 12, 14, 15, 16
- [x] Widget resizing tested (3x2 → 5x2)
- [x] Offline mode tested
- [x] Reminder scheduling verified

---

## Troubleshooting

### Build Fails
```bash
# Clean and rebuild
./gradlew clean assembleDebug

# Check Java version
java -version  # Should be 17+

# Update Gradle
./gradlew wrapper --gradle-version 8.11.1
```

### Widget Not Updating
- Ensure `SCHEDULE_EXACT_ALARM` permission granted
- Check app isn't in restricted battery mode
- Manually tap widget to refresh

### API Fails
- Verify internet connection
- Check username (case-sensitive)
- Ensure LeetCode profile is public

---

## Version History

**v1.0.0** (Current)
- Initial production release
- Jetpack Compose UI
- Android 16 support
- 10-minute refresh cycle
- Home widget with heatmap

---

## Support & Contribution

**Issues?** Check:
1. Android version (API 26+ required)
2. Internet connectivity
3. LeetCode username valid
4. Permissions granted

**Want to contribute?**
1. Fork repository
2. Create feature branch
3. Submit pull request with tests

---

## License & Attribution

Personal project for educational purposes.  
LeetCode® is a trademark of LeetCode LLC.  
Uses open-source libraries: Retrofit, OkHttp, Gson, Jetpack Compose.

---

**Ready to deploy?** Push to main branch and GitHub Actions will build automatically!

For questions, check the README.md or contact the development team.
