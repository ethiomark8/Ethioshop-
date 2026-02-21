# Deploy EthioShop on Google IDX

This guide provides step-by-step instructions for deploying and developing EthioShop on Google IDX (idx.google.com).

## Table of Contents

1. [What is Google IDX?](#what-is-google-idx)
2. [Quick Start](#quick-start)
3. [Detailed Setup](#detailed-setup)
4. [Development Workflow](#development-workflow)
5. [Deployment](#deployment)
6. [Troubleshooting](#troubleshooting)

## What is Google IDX?

Google IDX is a cloud-based development environment that provides:
- **Instant Setup**: Pre-configured environments with all tools
- **No Local Installation**: Everything runs in the cloud
- **Collaboration**: Real-time collaborative coding
- **Previews**: Built-in preview capabilities
- **AI Assistance**: Integrated AI coding assistance

## Quick Start

### 1. Create IDX Workspace

1. Go to [idx.google.com](https://idx.google.com)
2. Click "Create new workspace"
3. Choose "Import from Git repository" or "Upload files"
4. Select the EthioShop project
5. Wait for automatic setup (5-10 minutes)

### 2. Run Startup Script

```bash
# Make the script executable (if needed)
chmod +x .idx/startup.sh

# Run the startup script
.idx/startup.sh
```

This will:
- Check all installations
- Verify configuration
- Install dependencies
- Provide next steps

### 3. Configure Firebase

```bash
# Copy your Firebase configuration files
cp google-services.json app/
cp service-account.json functions/
```

### 4. Build the App

```bash
# Build debug APK
./gradlew assembleDebug
```

## Detailed Setup

### Step 1: Create IDX Workspace

#### Option A: Import from Git Repository

1. Push EthioShop to GitHub/GitLab
2. In IDX, click "Create new workspace"
3. Select "Import from Git repository"
4. Enter your repository URL
5. Click "Create workspace"

#### Option B: Upload Files

1. In IDX, click "Create new workspace"
2. Select "Upload files"
3. Drag and drop the EthioShop project files
4. Click "Create workspace"

### Step 2: Automatic Configuration

IDX will automatically configure the environment using `.idx/dev.nix`:

**Installed Components:**
- ✅ Nix (stable-24.05 channel)
- ✅ Android SDK 34
- ✅ Java 17 JDK
- ✅ Node.js 20
- ✅ Gradle 8.4
- ✅ Firebase CLI
- ✅ Development tools (Git, Vim, tmux, etc.)
- ✅ VS Code extensions

**Environment Variables:**
- `ANDROID_HOME` - Android SDK path
- `ANDROID_SDK_ROOT` - Android SDK root
- `JAVA_HOME` - Java 17 home
- `GRADLE_OPTS` - Gradle JVM options
- `NODE_OPTIONS` - Node.js memory options
- `FIREBASE_PROJECT` - Firebase project ID

### Step 3: Verify Installation

Open a terminal in IDX and run:

```bash
# Check Java
java -version
# Expected: openjdk version "17.x.x"

# Check Node.js
node --version
# Expected: v20.x.x

# Check Gradle
./gradlew --version
# Expected: Gradle 8.4

# Check Firebase CLI
firebase --version
# Expected: 13.x.x or higher

# Check Android SDK
echo $ANDROID_HOME
# Expected: /nix/store/.../android-sdk
```

### Step 4: Configure Firebase

#### If You Have an Existing Firebase Project

1. **Download Configuration Files**
   - Go to [Firebase Console](https://console.firebase.google.com)
   - Select your project
   - Download `google-services.json` from Project Settings
   - Generate service account key from Project Settings → Service Accounts

2. **Upload to IDX**
   - Upload both files to IDX
   - Copy them to the correct locations:
     ```bash
     cp google-services.json app/
     cp service-account.json functions/
     ```

#### If You Need to Create a New Firebase Project

1. **Create Project**
   - Go to [Firebase Console](https://console.firebase.google.com)
   - Click "Add project"
   - Name it "EthioShop"
   - Follow the setup wizard

2. **Enable Services**
   - Authentication: Enable Email/Password, Phone, Google
   - Firestore Database: Create database
   - Cloud Functions: Enable
   - Storage: Enable
   - Cloud Messaging: Enable
   - Crashlytics: Enable
   - Performance Monitoring: Enable

3. **Add Android App**
   - Package name: `com.ethio.shop`
   - Download `google-services.json`
   - Upload to IDX and copy to `app/`

4. **Generate Service Account Key**
   - Go to Project Settings → Service Accounts
   - Click "Generate new private key"
   - Download `service-account.json`
   - Upload to IDX and copy to `functions/`

### Step 5: Configure Environment Variables

The environment files are pre-configured with test keys:

**Root `.env`:**
```bash
# Already configured with:
FIREBASE_PROJECT_ID=ethioshop-18181
CHAPA_PUBLIC_KEY=CHAPUBK_TEST-QmCIBhWYIsdp2tgG0sPr67h5fozBbSz3
CHAPA_SECRET_KEY=CHASECK_TEST-Qto69ETjzvgaMcVG9HBabcHCDLSFdmmS
CHAPA_ENCRYPTION_KEY=I5yIHlhBRBoPyNgrh6xGeDhB
```

**`functions/.env`:**
```bash
# Already configured with:
FIREBASE_PROJECT_ID=ethioshop-18181
CHAPA_PUBLIC_KEY=CHAPUBK_TEST-QmCIBhWYIsdp2tgG0sPr67h5fozBbSz3
CHAPA_SECRET_KEY=CHASECK_TEST-Qto69ETjzvgaMcVG9HBabcHCDLSFdmmS
CHAPA_ENCRYPTION_KEY=I5yIHlhBRBoPyNgrh6xGeDhB
```

**⚠️ Important:** Update these with your production keys before deploying to production.

## Development Workflow

### Building the App

```bash
# Clean previous builds
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK (requires signing)
./gradlew assembleRelease

# Build with specific variant
./gradlew assembleDebug assembleRelease
```

**Output Location:**
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release.apk`

### Running Tests

```bash
# Run all unit tests
./gradlew test

# Run instrumented tests (requires emulator)
./gradlew connectedAndroidTest

# Run specific test class
./gradlew test --tests CurrencyUtilsTest

# Run tests with coverage
./gradlew test jacocoTestReport
```

### Using Firebase Emulators

```bash
# Start all emulators
firebase emulators:start

# Start specific emulators
firebase emulators:start --only auth,firestore,functions,storage

# Start in background
firebase emulators:start --only auth,firestore &
```

**Emulator URLs:**
- Auth: http://localhost:9099
- Firestore: http://localhost:8080
- Functions: http://localhost:5001
- Storage: http://localhost:9199

### Developing Cloud Functions

```bash
# Navigate to functions directory
cd functions

# Install dependencies
npm install

# Run functions locally
npm run serve

# Deploy functions
firebase deploy --only functions

# Deploy specific function
firebase deploy --only functions:createChapaPayment
```

### Debugging

```bash
# Enable debug logging
./gradlew assembleDebug --debug

# Run with verbose output
./gradlew assembleDebug --info

# Check build scan
./gradlew assembleDebug --scan
```

## Deployment

### Deploy to Firebase

```bash
# Deploy all Firebase resources
firebase deploy

# Deploy specific resources
firebase deploy --only firestore:rules
firebase deploy --only firestore:indexes
firebase deploy --only storage
firebase deploy --only functions
firebase deploy --only hosting
```

### Deploy Cloud Functions

```bash
# Install dependencies
cd functions
npm install
cd ..

# Deploy all functions
firebase deploy --only functions

# Deploy specific functions
firebase deploy --only functions:createChapaPayment,functions:chapaWebhook
```

### Deploy Firestore Rules and Indexes

```bash
# Deploy rules
firebase deploy --only firestore:rules

# Deploy indexes
firebase deploy --only firestore:indexes
```

### Deploy Storage Rules

```bash
firebase deploy --only storage
```

### Deploy to Google Play Store

1. **Build Signed APK/AAB**
   ```bash
   ./gradlew assembleRelease
   # or
   ./gradlew bundleRelease
   ```

2. **Upload to Google Play Console**
   - Go to [Google Play Console](https://play.google.com/console)
   - Create or select your app
   - Upload the APK/AAB
   - Complete the store listing
   - Submit for review

## Troubleshooting

### Build Issues

**Problem: Gradle build fails with "SDK not found"**

```bash
# Check Android SDK path
echo $ANDROID_HOME

# If not set, restart the workspace
# IDX will reconfigure the environment
```

**Problem: Out of memory during build**

```bash
# Increase Gradle memory
export GRADLE_OPTS="-Xmx4096m -XX:MaxMetaspaceSize=1024m"

# Build again
./gradlew assembleDebug
```

**Problem: Build is slow**

```bash
# Enable parallel builds
./gradlew assembleDebug --parallel

# Enable build cache
./gradlew assembleDebug --build-cache

# Disable unnecessary tasks
./gradlew assembleDebug -x lint -x test
```

### Firebase Issues

**Problem: Firebase emulators won't start**

```bash
# Stop any running processes
pkill -f firebase

# Start emulators again
firebase emulators:start --only auth,firestore,functions,storage
```

**Problem: Functions deployment fails**

```bash
# Check Node.js version
node --version
# Should be v20.x.x

# Reinstall dependencies
cd functions
rm -rf node_modules package-lock.json
npm install
cd ..

# Deploy again
firebase deploy --only functions
```

### Environment Issues

**Problem: Environment variables not set**

```bash
# Check if .env file exists
ls -la .env

# Source the environment variables
source .env

# Check variables
echo $FIREBASE_PROJECT_ID
```

**Problem: Node.js modules not found**

```bash
cd functions
rm -rf node_modules package-lock.json
npm install
cd ..
```

### IDX-Specific Issues

**Problem: Workspace won't start**

1. Check your internet connection
2. Try creating a new workspace
3. Contact IDX support

**Problem: Files not syncing**

1. Refresh the file explorer
2. Check for file conflicts
3. Try reloading the workspace

## Performance Tips

### Gradle Optimization

```bash
# Use Gradle daemon (enabled by default)
export GRADLE_OPTS="-Dorg.gradle.daemon=true"

# Enable parallel execution
./gradlew assembleDebug --parallel

# Enable configuration cache
./gradlew assembleDebug --configuration-cache

# Enable build cache
./gradlew assembleDebug --build-cache
```

### Node.js Optimization

```bash
# Increase Node.js memory
export NODE_OPTIONS="--max-old-space-size=8192"

# Use npm ci for faster installs
cd functions
npm ci
```

### Firebase Optimization

```bash
# Use specific emulator ports
firebase emulators:start --only auth,firestore,functions,storage

# Use local-only mode for faster startup
firebase emulators:start --only auth,firestore --import=./firebase-export
```

## Security Best Practices

1. **Never Commit Sensitive Files**
   - `.env` files are in `.gitignore`
   - `google-services.json` is in `.gitignore`
   - `service-account.json` is in `.gitignore`

2. **Use Environment Variables**
   - All sensitive data is in `.env` files
   - Never hardcode keys in source code

3. **Update Keys for Production**
   - Replace test keys with production keys
   - Update webhook URLs
   - Configure proper CORS settings

4. **Enable Firebase Security Rules**
   - Deploy Firestore rules
   - Deploy Storage rules
   - Test rules in emulator

## Additional Resources

- [Google IDX Documentation](https://cloud.google.com/idx/docs)
- [Firebase Documentation](https://firebase.google.com/docs)
- [Chapa API Documentation](https://developer.chapa.co/docs)
- [Android Developer Guide](https://developer.android.com/guide)
- [Gradle Documentation](https://docs.gradle.org)

## Support

For IDX-specific issues:
- [IDX Documentation](https://cloud.google.com/idx/docs)
- [IDX Support](https://cloud.google.com/idx/docs/support)

For EthioShop issues:
- [README.md](README.md)
- [BUILD_INSTRUCTIONS.md](BUILD_INSTRUCTIONS.md)
- [SETUP_GUIDE.md](SETUP_GUIDE.md)

---

**Happy coding on Google IDX! 🚀**