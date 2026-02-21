# EthioShop - Google IDX Development Environment

This directory contains the configuration for deploying EthioShop on Google IDX.

## Configuration Files

### `dev.nix`
The main Nix configuration file that defines:
- Nix channel (stable-24.05)
- Android SDK (version 34 with build tools, platforms, NDK, emulator)
- Java 17 JDK
- Node.js 20 for Cloud Functions
- Development tools (Git, Firebase CLI, Gradle, etc.)
- VS Code extensions for Android/Kotlin/Firebase development
- Environment variables
- Shell hooks for setup
- Firebase emulator services

### Environment Files

#### `.env` (Root)
Main environment configuration for the Android app:
- Firebase project configuration
- Chapa payment keys (test)
- App configuration (package name, SDK versions)
- Build configuration
- Firebase emulator settings
- Feature flags
- Localization settings

#### `functions/.env`
Cloud Functions environment configuration:
- Firebase project configuration
- Chapa API keys
- Image optimization settings
- Storage configuration
- Order and escrow settings
- Security settings
- CORS configuration

## Quick Start

1. **Open in IDX**
   - The project will automatically configure using `dev.nix`
   - All dependencies will be installed automatically

2. **Set up Firebase**
   ```bash
   # Copy your google-services.json to app/
   cp google-services.json.example app/google-services.json
   
   # Copy your service-account.json to functions/
   cp service-account.json.example functions/service-account.json
   ```

3. **Configure Environment**
   ```bash
   # Root .env is already configured with test keys
   # functions/.env is already configured with test keys
   
   # Update with your production keys when ready
   ```

4. **Start Firebase Emulators** (Optional)
   ```bash
   firebase emulators:start
   ```

5. **Build the App**
   ```bash
   ./gradlew assembleDebug
   ```

6. **Run Tests**
   ```bash
   ./gradlew test
   ```

## Available Tools

### Android Development
- Android SDK 34
- Android Studio
- Gradle 8.4
- Java 17

### Firebase
- Firebase CLI
- Firebase Emulators (Auth, Firestore, Functions, Storage)

### Cloud Functions
- Node.js 20
- npm

### Development Tools
- Git
- Vim/Neovim
- tmux
- Various CLI utilities

## VS Code Extensions

The following extensions are automatically installed:
- Android Debug Adapter
- Java Extension Pack
- Kotlin Language
- Gradle for Java
- Firebase Tools
- GitLens
- ESLint, Prettier
- And many more...

## Services

### Firebase Emulators
The Firebase emulators are configured to run on:
- Auth: localhost:9099
- Firestore: localhost:8080
- Functions: localhost:5001
- Storage: localhost:9199

Start emulators with:
```bash
firebase emulators:start
```

## Environment Variables

The following environment variables are automatically set:
- `ANDROID_HOME` - Android SDK path
- `ANDROID_SDK_ROOT` - Android SDK root
- `JAVA_HOME` - Java 17 home
- `GRADLE_OPTS` - Gradle JVM options
- `NODE_OPTIONS` - Node.js memory options
- `FIREBASE_PROJECT` - Firebase project ID

## Troubleshooting

### Android SDK Issues
If you encounter Android SDK issues, the SDK is managed by Nix and should be automatically available.

### Gradle Build Issues
Try cleaning and rebuilding:
```bash
./gradlew clean
./gradlew assembleDebug
```

### Firebase Emulator Issues
Stop and restart the emulators:
```bash
firebase emulators:start --only auth,firestore,functions,storage
```

## Next Steps

1. Configure your Firebase project
2. Add your Firebase configuration files
3. Build and test the app
4. Deploy Cloud Functions
5. Test Chapa payment integration
6. Prepare for production deployment

## Documentation

For more information, see:
- [README.md](../README.md) - Main project documentation
- [BUILD_INSTRUCTIONS.md](../BUILD_INSTRUCTIONS.md) - Build instructions
- [SETUP_GUIDE.md](../SETUP_GUIDE.md) - Setup guide
- [FIREBASE_CONFIG.md](../FIREBASE_CONFIG.md) - Firebase configuration

## Support

For issues or questions:
- Check the main README.md
- Review Firebase documentation
- Check Chapa API documentation