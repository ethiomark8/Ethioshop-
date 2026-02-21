# EthioShop - IDX Quick Reference

Quick reference for common tasks on Google IDX.

## Setup Commands

### Initial Setup
```bash
# Run startup script
.idx/startup.sh

# Configure Firebase
cp google-services.json app/
cp service-account.json functions/
```

## Build Commands

### Build App
```bash
# Clean build
./gradlew clean

# Debug APK
./gradlew assembleDebug

# Release APK
./gradlew assembleRelease

# Output location
# Debug: app/build/outputs/apk/debug/app-debug.apk
# Release: app/build/outputs/apk/release/app-release.apk
```

### Run Tests
```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest

# Specific test
./gradlew test --tests CurrencyUtilsTest
```

## Firebase Commands

### Emulators
```bash
# Start all emulators
firebase emulators:start

# Start specific emulators
firebase emulators:start --only auth,firestore,functions,storage

# Emulator URLs
# Auth: http://localhost:9099
# Firestore: http://localhost:8080
# Functions: http://localhost:5001
# Storage: http://localhost:9199
```

### Deployment
```bash
# Deploy all
firebase deploy

# Deploy specific resources
firebase deploy --only functions
firebase deploy --only firestore:rules
firebase deploy --only firestore:indexes
firebase deploy --only storage
```

## Cloud Functions

### Development
```bash
cd functions

# Install dependencies
npm install

# Run locally
npm run serve

# Deploy
firebase deploy --only functions

cd ..
```

## Environment Variables

### Check Variables
```bash
# Android SDK
echo $ANDROID_HOME

# Java
echo $JAVA_HOME

# Firebase project
echo $FIREBASE_PROJECT

# Custom variables
source .env
echo $FIREBASE_PROJECT_ID
```

## Troubleshooting

### Build Issues
```bash
# Clean and rebuild
./gradlew clean
./gradlew assembleDebug

# Increase memory
export GRADLE_OPTS="-Xmx4096m"
```

### Firebase Issues
```bash
# Restart emulators
pkill -f firebase
firebase emulators:start

# Reinstall dependencies
cd functions
rm -rf node_modules package-lock.json
npm install
cd ..
```

## Useful Tools

### File Operations
```bash
# List files
tree -L 2

# Find files
find . -name "*.kt"

# Count lines
find . -name "*.kt" | xargs wc -l
```

### Git Operations
```bash
# Status
git status

# Commit
git add .
git commit -m "message"

# Push
git push origin main
```

## Documentation

- [IDX Setup Guide](IDX_SETUP_GUIDE.md) - Complete setup instructions
- [Deploy on IDX](DEPLOY_ON_IDX.md) - Deployment guide
- [IDX Configuration Summary](IDX_CONFIGURATION_SUMMARY.md) - Configuration details
- [Main README](README.md) - Project documentation
- [Build Instructions](BUILD_INSTRUCTIONS.md) - Build guide

## Support

- [Google IDX Docs](https://cloud.google.com/idx/docs)
- [Firebase Docs](https://firebase.google.com/docs)
- [Chapa API Docs](https://developer.chapa.co/docs)

---

**Quick Tips:**
- Use multiple terminals for different tasks
- Keep emulators running in background
- Use `./gradlew --parallel` for faster builds
- Check `.idx/startup.sh` for setup verification