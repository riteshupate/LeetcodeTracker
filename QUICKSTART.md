# Quick Start Guide - LeetCode Tracker

Get your app built and running in minutes!

## 🎯 Prerequisites (5 minutes)

### Install Required Tools

**1. Java Development Kit (JDK 17+)**
- Download from: https://www.oracle.com/java/technologies/downloads/
- Or use: `brew install openjdk@17` (Mac) or `choco install openjdk` (Windows)
- Verify: `java -version` (should show Java 17 or higher)

**2. Android SDK**
- Option A: Install Android Studio (easiest)
  - Download from: https://developer.android.com/studio
  - Run installer and complete setup wizard
  
- Option B: Command-line tools only
  - Download from: https://developer.android.com/studio
  - Set `ANDROID_SDK_ROOT` environment variable
  - Run: `sdkmanager "platforms;android-36"`

**3. Set Environment Variables**

**Mac/Linux:**
```bash
export ANDROID_SDK_ROOT=$HOME/Library/Android/sdk  # macOS
# or
export ANDROID_SDK_ROOT=$HOME/Android/Sdk  # Linux

export PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

**Windows:**
```batch
set ANDROID_SDK_ROOT=C:\Users\YourUsername\AppData\Local\Android\Sdk
set PATH=%PATH%;%ANDROID_SDK_ROOT%\cmdline-tools\latest\bin
```

## 🚀 Build Your First APK (5 minutes)

### Step 1: Clone and Enter Directory
```bash
git clone <your-repo-url> LeetCodeTracker
cd LeetCodeTracker
```

### Step 2: Make Gradle Executable (Mac/Linux only)
```bash
chmod +x gradlew
```

### Step 3: Build Debug APK
```bash
./gradlew assembleDebug
```

**Windows users:** Use `gradlew.bat assembleDebug` instead

**First build takes 2-3 minutes** (downloading dependencies)

### Step 4: Find Your APK
```bash
# APK location:
app/build/outputs/apk/debug/app-debug.apk
```

✅ **Success!** You have a working APK!

## 📱 Install on Your Device (2 minutes)

### Option A: Using Android Studio
1. Open Android Studio
2. Click "Run" or "Run on Device"
3. Select your device
4. Click OK

### Option B: Using Command Line
```bash
# Ensure your device is connected and USB debugging is enabled
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Option C: Manual Installation
1. Transfer APK to your device (email, USB cable, cloud storage)
2. Open file manager on device
3. Tap the APK file
4. Tap "Install"
5. Grant permissions

## 🧪 Test the App (2 minutes)

1. **Open LeetCode Tracker**
2. **Enter your LeetCode username** (e.g., "rpupate")
3. **Tap "Save and Load Data"**
4. **Wait for data to load** (~5 seconds)
5. **See your stats!** 🎉

## 🔧 Common Issues & Fixes

### "JAVA_HOME not set"
```bash
# Find Java
which java  # macOS/Linux
where java  # Windows

# Set it
export JAVA_HOME=/path/to/java  # macOS/Linux
# or use Android Studio's built-in Java
```

### "Android SDK not found"
```bash
# Download it
sdkmanager "platforms;android-36" "build-tools;36.0.0"

# Set environment variable
export ANDROID_SDK_ROOT=/path/to/android/sdk
```

### "Command not found: ./gradlew"
```bash
# You're on Windows, use:
gradlew.bat assembleDebug

# Or you're on Mac/Linux and forgot to make it executable:
chmod +x gradlew
./gradlew assembleDebug
```

### "Build fails with Gradle error"
```bash
# Clean and rebuild
./gradlew clean assembleDebug

# Update Gradle
./gradlew wrapper --gradle-version 8.11.1
```

### "APK installs but crashes on startup"
- Make sure Android version is 8.0 (API 26) or higher
- Check device has at least 100MB free space
- Try uninstalling and reinstalling
- Check Logcat for error details in Android Studio

## 🎯 Next Steps

### 1. **Understand the Code**
- Read `README.md` for full feature list
- Check `PRODUCTION.md` for deployment
- Review source code in `src/main/java/com/leetcode/tracker/`

### 2. **Customize the App**
- Change colors in `src/main/java/com/leetcode/tracker/ui/theme/Theme.kt`
- Update app name in `src/main/res/values/strings.xml`
- Modify app icon in `src/main/res/drawable/ic_launcher_foreground.xml`

### 3. **Deploy to Production**
- Sign the APK with your keystore
- Test on multiple devices
- Publish to Google Play Store (see `PRODUCTION.md`)

### 4. **Set Up GitHub Actions**
- Push to GitHub
- Actions automatically build APKs
- Download from artifacts

## 📚 Useful Commands

```bash
# Clean build artifacts
./gradlew clean

# Build debug APK (fast)
./gradlew assembleDebug

# Build release APK (optimized)
./gradlew assembleRelease

# Install APK on connected device
./gradlew installDebug

# Run unit tests
./gradlew test

# Run linting checks
./gradlew lint

# View detailed build info
./gradlew assembleDebug --stacktrace --info

# Check Gradle configuration
./gradlew tasks

# Update dependencies
./gradlew dependencyUpdates
```

## 🆘 Need Help?

1. **Check logs:**
   ```bash
   adb logcat | grep "LeetCode"
   ```

2. **Android Studio Logcat:**
   - Bottom panel of Android Studio
   - Filter for "LeetCode" or "Exception"

3. **GitHub Issues:**
   - Create an issue with:
     - Device model and Android version
     - Full error message
     - Build command used
     - Output from `./gradlew --version`

4. **Environment Check:**
   ```bash
   java -version
   ./gradlew --version
   echo $ANDROID_SDK_ROOT
   ```

## 🎓 Learning Resources

- [Android Developers](https://developer.android.com)
- [Jetpack Compose Docs](https://developer.android.com/jetpack/compose)
- [Kotlin Documentation](https://kotlinlang.org/docs/)
- [Gradle User Guide](https://docs.gradle.org/8.11.1/userguide/)

## ✅ Success Checklist

- [ ] JDK 17+ installed
- [ ] Android SDK installed
- [ ] Environment variables set
- [ ] Repository cloned
- [ ] `./gradlew assembleDebug` ran successfully
- [ ] APK built at `app/build/outputs/apk/debug/app-debug.apk`
- [ ] APK installed on device
- [ ] App opens and shows interface
- [ ] Can enter LeetCode username
- [ ] Data loads successfully

**You're all set!** 🚀

Now proceed to `README.md` for full usage guide or `PRODUCTION.md` for deployment.

---

Questions? Check the **Troubleshooting** section or create a GitHub issue!
