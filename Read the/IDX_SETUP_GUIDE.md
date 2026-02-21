# EthioShop - Google IDX Setup Guide

This guide will help you set up and deploy EthioShop on Google IDX (idx.google.com).

## What is Google IDX?

Google IDX is a cloud-based development environment that provides:
- Pre-configured development environments
- Instant project setup
- Collaborative coding
- Built-in preview capabilities
- No local setup required

## Prerequisites

- A Google account
- Access to Google IDX (idx.google.com)
- EthioShop project files

## Step-by-Step Setup

### 1. Create a New IDX Workspace

1. Go to [idx.google.com](https://idx.google.com)
2. Click "Create new workspace"
3. Choose "Import from Git repository" or "Upload files"
4. Select or upload the EthioShop project
5. IDX will automatically detect the `.idx/dev.nix` configuration

### 2. Automatic Configuration

IDX will automatically:
- Install Nix with stable-24.05 channel
- Set up Android SDK 34
- Install Java 17
- Install Node.js 20
- Install all development tools
- Configure VS Code extensions
- Set up environment variables

This process may take 5-10 minutes on first setup.

### 3. Verify Installation

Once the workspace is ready, verify the installation:

```bash
# Check Java version
java -version
# Should show: openjdk version "17.x.x"

# Check Node.js version
node --version
# Should show: v20.x.x

# Check Gradle version
./gradlew --version
# Should show: Gradle 8.4

# Check Firebase CLI
firebase --version
# Should show: 13.x.x or higher

# Check Android SDK
echo $ANDROID_HOME
# Should show the Android SDK path
```

### 4. Configure Firebase

#### Option A: Use Existing Firebase Project

If you already have the Firebase project configured:

1. Copy your `google-services.json` to the app directory:
   ```bash
   # If you have the file locally, upload it to IDX
   # Then copy it to the app directory
   cp google-services.json app/
   ```

2. Copy your `service-account.json` to the functions directory:
   ```bash
   cp service-account.json functions/
   ```

#### Option B: Create New Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create a new project named "EthioShop"
3. Enable the following services:
   - Authentication (Email/Password, Phone, Google)
   - Firestore Database
   - Cloud Functions
   - Storage
   - Cloud Messaging
   - Crashlytics
   - Performance Monitoring

4. Add an Android app:
   - Package name: `com.ethio.shop`
   - Download `google-services.json`
   - Upload it to IDX and copy to `app/`

5. Generate a service account key:
   - Go to Project Settings → Service Accounts
   - Generate new private key
   - Download `service-account.json`
   - Upload it to IDX and copy to `functions/`

### 5. Configure Environment Variables

The environment files are already configured with test keys:

#### Root `.env`
```bash
# Already configured with:
# - Firebase project ID: ethioshop-18181
# - Chapa test keys
# - App configuration
# - Feature flags
```

#### `functions/.env`
```bash
# Already configured with:
# - Firebase project configuration
# - Chapa API keys
# - Image optimization settings
# - Security settings
```

**Important:** Update these files with your production keys when ready to deploy.

### 6. Start Firebase Emulators (Optional)

For local development, you can start the Firebase emulators:

```bash
# Start all emulators
firebase emulators:start

# Or start specific emulators
firebase emulators:start --only auth,firestore,functions,storage
```

The emulators will be available at:
- Auth: http://localhost:9099
- Firestore: http://localhost:8080
- Functions: http://localhost:5001
- Storage: http://localhost:9199

### 7. Build the Android App

```bash
# Clean previous builds
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK (requires signing)
./gradlew assembleRelease
```

The APK will be located at:
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release.apk`

### 8. Run Tests

```bash
# Run unit tests
./gradlew test

# Run instrumented tests (requires emulator)
./gradlew connectedAndroidTest

# Run specific test
./gradlew test --tests CurrencyUtilsTest
```

### 9. Deploy Cloud Functions

```bash
# Install dependencies
cd functions
npm install
cd ..

# Deploy to Firebase
firebase deploy --only functions

# Or deploy specific functions
firebase deploy --only functions:createChapaPayment,functions:chapaWebhook
```

### 10. Deploy Firebase Rules and Indexes

```bash
# Deploy Firestore rules
firebase deploy --only firestore:rules

# Deploy Firestore indexes
firebase deploy --only firestore:indexes

# Deploy Storage rules
firebase deploy --only storage
```

## Using IDX Features

### Preview the App

1. Build the debug APK
2. Download the APK from IDX
3. Install on your Android device or emulator
4. Test the app

### Collaborative Coding

1. Share your IDX workspace link with team members
2. Collaborate in real-time
3. Use built-in chat and video calls

### Terminal Access

IDX provides multiple terminals:
- Use Terminal 1 for Gradle builds
- Use Terminal 2 for Firebase emulators
- Use Terminal 3 for Cloud Functions development

### File Management

- Use the built-in file explorer
- Drag and drop files to upload
- Right-click for context menu options

## Troubleshooting

### Build Fails with "SDK not found"

```bash
# Check Android SDK path
echo $ANDROID_HOME

# If not set, restart the workspace
# IDX will reconfigure the environment
```

### Gradle Build is Slow

```bash
# Increase Gradle memory
export GRADLE_OPTS="-Xmx4096m -XX:MaxMetaspaceSize=1024m"

# Build with parallel execution
./gradlew assembleDebug --parallel
```

### Firebase Emulator Won't Start

```bash
# Stop any running emulators
firebase emulators:start --only auth,firestore,functions,storage

# Check if ports are in use
lsof -i :9099
lsof -i :8080
lsof -i :5001
lsof -i :9199
```

### Node.js Modules Not Found

```bash
cd functions
rm -rf node_modules package-lock.json
npm install
cd ..
```

### Out of Memory

```bash
# Increase Node.js memory
export NODE_OPTIONS="--max-old-space-size=8192"

# Increase Gradle memory
export GRADLE_OPTS="-Xmx4096m"
```

## Performance Tips

1. **Use Gradle Daemon**
   ```bash
   # Already enabled in .env
   GRADLE_OPTS="-Dorg.gradle.daemon=true"
   ```

2. **Enable Parallel Builds**
   ```bash
   ./gradlew assembleDebug --parallel
   ```

3. **Use Build Cache**
   ```bash
   ./gradlew assembleDebug --build-cache
   ```

4. **Disable Unnecessary Tasks**
   ```bash
   ./gradlew assembleDebug -x lint -x test
   ```

## Security Best Practices

1. **Never commit sensitive files**
   - `.env` files are in `.gitignore`
   - `google-services.json` is in `.gitignore`
   - `service-account.json` is in `.gitignore`

2. **Use environment variables**
   - All sensitive data is in `.env` files
   - Never hardcode keys in source code

3. **Update keys for production**
   - Replace test keys with production keys
   - Update webhook URLs
   - Configure proper CORS settings

## Next Steps

1. ✅ Set up IDX workspace
2. ✅ Configure Firebase
3. ✅ Build the app
4. ✅ Test all features
5. ✅ Deploy Cloud Functions
6. ✅ Test Chapa payment integration
7. ✅ Prepare for production

## Additional Resources

- [Google IDX Documentation](https://cloud.google.com/idx/docs)
- [Firebase Documentation](https://firebase.google.com/docs)
- [Chapa API Documentation](https://developer.chapa.co/docs)
- [Android Developer Guide](https://developer.android.com/guide)

## Support

For issues specific to IDX:
- Check [IDX Documentation](https://cloud.google.com/idx/docs)
- Use IDX's built-in help feature

For EthioShop issues:
- Check [README.md](README.md)
- Check [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md)
- Review Firebase and Chapa documentation

---

**Happy coding on Google IDX! 🚀**