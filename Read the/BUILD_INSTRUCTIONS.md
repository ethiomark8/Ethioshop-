# EthioShop - Build Instructions

## Prerequisites

1. **Android Studio** (latest version) or **Command Line Tools**
2. **Java JDK 17** or higher
3. **Android SDK** (API 34)
4. **Gradle 8.4**

## Quick Start with Android Studio

### Step 1: Open Project
1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to the `ethioshop-complete` directory
4. Click "OK"

### Step 2: Configure Firebase
1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or select existing `ethioshop-18181`
3. Add an Android app with package name `com.ethio.shop`
4. Download `google-services.json`
5. Place it in `app/` directory (already included in the package)

### Step 3: Sync Gradle
1. Android Studio will automatically sync Gradle
2. Wait for dependencies to download
3. If sync fails, click "File" → "Sync Project with Gradle Files"

### Step 4: Build Debug APK
1. Click "Build" → "Build Bundle(s) / APK(s)" → "Build APK(s)"
2. Wait for build to complete
3. APK will be located at: `app/build/outputs/apk/debug/app-debug.apk`

## Command Line Build

### Step 1: Install Android SDK
```bash
# Download and install Android Command Line Tools
wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
unzip commandlinetools-linux-9477386_latest.zip
mkdir -p ~/Android/sdk/cmdline-tools/latest
mv cmdline-tools ~/Android/sdk/cmdline-tools/latest

# Set environment variables
export ANDROID_HOME=~/Android/sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

# Accept licenses
yes | sdkmanager --licenses

# Install required SDK packages
sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
```

### Step 2: Build APK
```bash
# Navigate to project directory
cd ethioshop-complete

# Make gradlew executable
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# APK location
ls -lh app/build/outputs/apk/debug/app-debug.apk
```

## Build Variants

### Debug APK
```bash
./gradlew assembleDebug
```
- Location: `app/build/outputs/apk/debug/app-debug.apk`
- Size: ~15-20 MB
- Use for: Testing and development

### Release APK
```bash
./gradlew assembleRelease
```
- Location: `app/build/outputs/apk/release/app-release.apk`
- Size: ~10-15 MB (optimized)
- Use for: Production deployment

### Android App Bundle (AAB)
```bash
./gradlew bundleRelease
```
- Location: `app/build/outputs/bundle/release/app-release.aab`
- Use for: Google Play Store upload

## Troubleshooting

### SDK Location Not Found
Create `local.properties` file:
```properties
sdk.dir=/path/to/your/Android/sdk
```

### Gradle Sync Failed
```bash
# Clean and rebuild
./gradlew clean
./gradlew build
```

### Out of Memory Error
Increase heap size in `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxPermSize=512m
```

### Firebase Configuration Issues
1. Verify `google-services.json` is in `app/` directory
2. Check Firebase Console for correct package name
3. Ensure SHA-1 fingerprints match

## Testing the APK

### Install on Device
```bash
# Enable USB debugging on device
# Connect device via USB
adb devices
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Install on Emulator
```bash
# Start emulator
emulator -avd <emulator_name>

# Install APK
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Deployment

### Google Play Store
1. Generate signed APK or AAB
2. Create Google Play Console account
3. Create new app
4. Upload AAB file
5. Complete store listing
6. Submit for review

### Firebase App Distribution
```bash
# Install Firebase CLI
npm install -g firebase-tools

# Login
firebase login

# Distribute APK
firebase appdistribution:distribute app/build/outputs/apk/debug/app-debug.apk \
  --app "1:1009602861548:android:1694ae6baa0ef10ce608c9" \
  --groups "testers"
```

## CI/CD

The project includes GitHub Actions workflows for automated building and testing:

- `.github/workflows/build-and-test.yml` - Build and test APK
- `.github/workflows/deploy-functions.yml` - Deploy Cloud Functions

These will automatically run on push to the repository.

## Performance Optimization

The app is already optimized for performance:
- R8 code shrinking enabled
- ProGuard rules configured
- Image optimization (WebP format)
- Firestore offline persistence
- Efficient queries with indexes

## Support

For issues or questions:
1. Check `README.md` for project overview
2. Review `SETUP_GUIDE.md` for Firebase setup
3. Check `FIREBASE_CONFIG.md` for configuration details
4. Review build logs for specific errors

## Next Steps

After building the APK:
1. Test all user flows
2. Test Chapa payment integration (sandbox)
3. Test push notifications
4. Test localization (English/Amharic)
5. Deploy to Firebase
6. Upload to Google Play Store