# 🚀 Welcome to EthioShop on Google IDX!

## Quick Start Guide

This is your EthioShop project configured for Google IDX. Follow these steps to get started:

### 1. ✅ Automatic Setup (Already Done)

Your IDX workspace has been automatically configured with:
- ✅ Android SDK 34
- ✅ Java 17
- ✅ Node.js 20
- ✅ Gradle 8.4
- ✅ Firebase CLI
- ✅ All development tools
- ✅ VS Code extensions

### 2. 🔧 Run Startup Script

```bash
# Make executable and run
chmod +x .idx/startup.sh
.idx/startup.sh
```

This will verify your setup and provide next steps.

### 3. 🔑 Configure Firebase

You need to add your Firebase configuration files:

```bash
# Copy your files to the correct locations
cp google-services.json app/
cp service-account.json functions/
```

**Don't have these files?**
- Go to [Firebase Console](https://console.firebase.google.com)
- Create a new project or use existing one
- Add Android app with package name: `com.ethio.shop`
- Download `google-services.json`
- Generate service account key from Project Settings → Service Accounts

### 4. 🏗️ Build the App

```bash
# Build debug APK
./gradlew assembleDebug

# The APK will be at: app/build/outputs/apk/debug/app-debug.apk
```

### 5. 🧪 Run Tests

```bash
# Run unit tests
./gradlew test
```

### 6. 🚀 Deploy Cloud Functions

```bash
# Install dependencies
cd functions
npm install
cd ..

# Deploy to Firebase
firebase deploy --only functions
```

## What's Included?

### Complete Android App
- ✅ Full marketplace functionality
- ✅ Chapa payment integration
- ✅ Firebase backend
- ✅ Localization (English + Amharic)
- ✅ All UI components

### Cloud Functions
- ✅ Chapa payment processing
- ✅ Image optimization
- ✅ Escrow management
- ✅ Push notifications

### Admin Dashboard
- ✅ Web-based admin panel
- ✅ User management
- ✅ Order tracking
- ✅ Revenue analytics

## Documentation

### Essential Reading
1. **[IDX_SETUP_GUIDE.md](IDX_SETUP_GUIDE.md)** - Complete setup instructions
2. **[DEPLOY_ON_IDX.md](DEPLOY_ON_IDX.md)** - Deployment guide
3. **[IDX_QUICK_REFERENCE.md](IDX_QUICK_REFERENCE.md)** - Quick command reference
4. **[README.md](README.md)** - Main project documentation

### Configuration Files
- **[.idx/dev.nix](.idx/dev.nix)** - Nix configuration
- **[.env](.env)** - App environment variables
- **[functions/.env](functions/.env)** - Functions environment variables
- **[.idx/README.md](.idx/README.md)** - IDX-specific documentation

## Environment Configuration

### Test Keys (Pre-configured)
The project is configured with Chapa test keys:
- Public Key: `CHAPUBK_TEST-QmCIBhWYIsdp2tgG0sPr67h5fozBbSz3`
- Secret Key: `CHASECK_TEST-Qto69ETjzvgaMcVG9HBabcHCDLSFdmmS`
- Encryption Key: `I5yIHlhBRBoPyNgrh6xGeDhB`

**⚠️ Important:** Update these with production keys before deploying to production.

### Firebase Project
- Project ID: `ethioshop-18181`
- Package Name: `com.ethio.shop`

## Common Tasks

### Start Firebase Emulators
```bash
firebase emulators:start
```

### Build Release APK
```bash
./gradlew assembleRelease
```

### Deploy All Firebase Resources
```bash
firebase deploy
```

### View Logs
```bash
# Firebase logs
firebase functions:log

# Gradle build logs
./gradlew assembleDebug --info
```

## Troubleshooting

### Build Fails?
```bash
./gradlew clean
./gradlew assembleDebug
```

### Firebase Issues?
```bash
# Check Firebase CLI
firebase --version

# Restart emulators
pkill -f firebase
firebase emulators:start
```

### Need Help?
- Check [IDX_QUICK_REFERENCE.md](IDX_QUICK_REFERENCE.md)
- Review [DEPLOY_ON_IDX.md](DEPLOY_ON_IDX.md)
- See [Firebase Documentation](https://firebase.google.com/docs)

## Project Structure

```
ethioshop/
├── .idx/                    # IDX configuration
│   ├── dev.nix             # Nix setup
│   ├── startup.sh          # Setup script
│   └── README.md           # IDX docs
├── app/                     # Android app
│   ├── src/                # Source code
│   └── google-services.json # Firebase config (add this)
├── functions/              # Cloud Functions
│   ├── index.js            # Functions code
│   └── service-account.json # Service account (add this)
├── admin-dashboard/        # Web admin panel
├── .env                    # Environment variables
└── Documentation files
```

## Next Steps

1. ✅ Run `.idx/startup.sh` to verify setup
2. ⏳ Add Firebase configuration files
3. ⏳ Build the app: `./gradlew assembleDebug`
4. ⏳ Test all features
5. ⏳ Deploy Cloud Functions
6. ⏳ Prepare for production

## Support

- **Google IDX**: [cloud.google.com/idx/docs](https://cloud.google.com/idx/docs)
- **Firebase**: [firebase.google.com/docs](https://firebase.google.com/docs)
- **Chapa API**: [developer.chapa.co/docs](https://developer.chapa.co/docs)

---

**Happy coding! 🎉**

Your EthioShop project is ready to build and deploy on Google IDX!