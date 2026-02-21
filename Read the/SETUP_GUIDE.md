# EthioShop - Setup Guide

## Firebase Project Configuration

Your Firebase project has been configured with the following details:

```
Project Name: EthioShop
Project ID: ethioshop-18181
Project Number: 1009602861548
App ID: 1:1009602861548:android:1694ae6baa0ef10ce608c9
Package Name: com.ethio.shop
```

## Installation Steps

### 1. Prerequisites

- **Android Studio**: Iguana (2023.2.1) or later
- **Firebase CLI**: `npm install -g firebase-tools`
- **Node.js**: 20.x
- **Java**: JDK 17

### 2. Clone the Project

```bash
git clone <repository-url>
cd EthioShop
```

### 3. Firebase Configuration

The following Firebase files are already configured:

- ✅ `app/google-services.json` - Android app configuration
- ✅ `functions/service-account.json` - Cloud Functions service account
- ✅ `.firebaserc` - Firebase project selection
- ✅ `firebase.json` - Firebase project settings

### 4. Install Dependencies

#### Android Dependencies
```bash
# Open the project in Android Studio
# Gradle will automatically sync and download dependencies
```

#### Cloud Functions Dependencies
```bash
cd functions
npm install
cd ..
```

### 5. Configure Environment Variables

Copy the example environment file and configure it:

```bash
cp functions/.env.example functions/.env
```

Edit `functions/.env` with your configuration:

```env
CHAPA_PUBLIC_KEY=CHAPUBK_TEST-QmCIBhWYIsdp2tgG0sPr67h5fozBbSz3
CHAPA_SECRET_KEY=CHASECK_TEST-Qto69ETjzvgaMcVG9HBabcHCDLSFdmmS
CHAPA_ENCRYPTION_KEY=I5yIHlhBRBoPyNgrh6xGeDhB
FIREBASE_PROJECT_ID=ethioshop-18181
CHAPA_WEBHOOK_URL=https://ethioshop-18181.web.app/chapa-webhook
```

### 6. Deploy Firestore Rules and Indexes

```bash
firebase deploy --only firestore:rules,firestore:indexes
```

### 7. Deploy Storage Rules

```bash
firebase deploy --only storage
```

### 8. Deploy Cloud Functions

```bash
firebase deploy --only functions
```

### 9. Enable Firebase Services in Console

Make sure the following services are enabled in your Firebase Console:

1. **Authentication**
   - Email/Password provider
   - Phone provider
   - Google provider

2. **Firestore Database**
   - Database should be created
   - Rules should be deployed

3. **Cloud Storage**
   - Rules should be deployed

4. **Cloud Functions**
   - Functions should be deployed
   - Requires Blaze plan for production

5. **Cloud Messaging**
   - Server key should be generated

6. **Crashlytics**
   - Enabled

7. **Performance Monitoring**
   - Enabled

### 10. Build and Run the Android App

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Select your device or emulator
4. Click Run or press Shift+F10

### 11. Test the App

#### Authentication Test
1. Open the app
2. Navigate to Login
3. Try signing up with email/password

#### Firestore Test
1. Login to the app
2. Navigate to Products
3. Products should load from Firestore

#### Cloud Functions Test
```bash
# Test the seed function
curl -X POST https://us-central1-ethioshop-18181.cloudfunctions.net/seedFirestore \
  -H "Content-Type: application/json" \
  -d '{"secret":"YOUR_SECRET_KEY"}'
```

#### Chapa Payment Test
1. Add products to cart
2. Proceed to checkout
3. Initiate payment with Chapa
4. Complete payment in sandbox mode

## Firebase Emulator Setup (For Development)

### Start Emulators

```bash
firebase emulators:start
```

### Emulator Endpoints

- **Auth**: `http://localhost:9099`
- **Firestore**: `http://localhost:8080`
- **Functions**: `http://localhost:5001`
- **Storage**: `http://localhost:9199`
- **UI**: `http://localhost:4000`

### Connect Android App to Emulator

The Firebase SDK automatically detects the emulator when running on an emulator. For physical devices, you may need to configure the SDK manually in `EthioShopApplication.kt`.

## CI/CD Setup

### GitHub Secrets Configuration

Configure the following secrets in your GitHub repository:

1. `FIREBASE_TOKEN`
   - Get it by running: `firebase login:ci`
   
2. `GOOGLE_SERVICES_JSON` (optional)
   - Base64 encoded google-services.json

3. `CREDENTIALS_FILE_CONTENT` (optional)
   - For Firebase App Distribution

### Deploy via GitHub Actions

Push to `main` branch to trigger:
- Build and Test workflow
- Deploy Functions workflow

## Troubleshooting

### Build Errors

**Issue**: Gradle sync fails
```
Solution: 
1. Check internet connection
2. Invalidate caches: File > Invalidate Caches
3. Delete .gradle folder and sync again
```

### Firebase Connection Issues

**Issue**: Cannot connect to Firebase
```
Solution:
1. Verify google-services.json is in app/ directory
2. Check Firebase Console app configuration
3. Verify SHA-1 and SHA-256 fingerprints match
```

### Cloud Functions Deployment Issues

**Issue**: Functions deployment fails
```
Solution:
1. Check Node.js version (should be 20.x)
2. Run npm install in functions/ directory
3. Verify service-account.json exists
4. Check Firebase console for function logs
```

### Chapa Payment Issues

**Issue**: Payment initialization fails
```
Solution:
1. Verify test keys are correct
2. Check Chapa sandbox status
3. Verify webhook URL is accessible
4. Check Cloud Functions logs
```

## Production Deployment Checklist

- [ ] Update Chapa keys from test to production
- [ ] Enable App Signing in Google Play Console
- [ ] Update app version number
- [ ] Build signed APK/AAB
- [ ] Upload to Google Play Console
- [ ] Configure store listing
- [ ] Set up pricing and distribution
- [ ] Enable crashlytics and performance monitoring
- [ ] Set up push notifications for production
- [ ] Configure analytics

## Security Best Practices

1. **Never commit sensitive files**
   - google-services.json
   - service-account.json
   - .env files

2. **Use environment variables for keys**
   - Chapa keys
   - API keys
   - Webhook URLs

3. **Enable Firebase App Check** (optional)
   - Adds extra security layer
   - Protects backend resources

4. **Regularly update dependencies**
   - Run `npm update` in functions/
   - Update Android dependencies

5. **Monitor Firebase Console**
   - Check for errors in Cloud Functions
   - Monitor Firestore usage
   - Review authentication logs

## Support

For issues or questions:
- Check Firebase Console logs
- Review Cloud Functions logs
- Check Android Logcat for errors
- Refer to README.md for detailed documentation

## Next Steps

1. Complete the remaining UI fragments
2. Implement RecyclerView adapters
3. Add ViewModels
4. Write unit tests
5. Test all features thoroughly
6. Deploy to production

---

**Last Updated**: 2024  
**Project Version**: 1.0.0  
**Firebase Project ID**: ethioshop-18181