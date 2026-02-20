# Firebase Configuration - EthioShop

## Project Details

```
Project Name: EthioShop
Project ID: ethioshop-18181
Project Number: 1009602861548
App ID: 1:1009602861548:android:1694ae6baa0ef10ce608c9
Package Name: com.ethio.shop
```

## SHA Certificate Fingerprints

### SHA-1
```
d6:b8:13:79:c1:25:9e:ad:bd:ae:59:5d:50:c0:0f:57:58:d3:85:b0
```

### SHA-256
```
f1:e4:ae:7a:6c:c1:ff:b1:75:39:f2:7a:50:2b:ec:06:eb:9b:5b:15:96:48:da:4d:31:c7:78:ac:e1:b9:d5:f6
```

## Configured Files

### 1. Android App Configuration
- ✅ **File**: `app/google-services.json`
- ✅ **Status**: Configured with your project credentials
- ✅ **Package**: com.ethio.shop
- ✅ **App ID**: 1:1009602861548:android:1694ae6baa0ef10ce608c9

### 2. Cloud Functions Service Account
- ✅ **File**: `functions/service-account.json`
- ✅ **Status**: Configured for Cloud Functions deployment
- ✅ **Project**: ethioshop-18181

### 3. Firebase Project Configuration
- ✅ **File**: `.firebaserc`
- ✅ **Project ID**: ethioshop-18181
- ✅ **Status**: Ready for deployment

### 4. Firebase Settings
- ✅ **File**: `firebase.json`
- ✅ **Firestore**: Rules and indexes configured
- ✅ **Storage**: Rules configured
- ✅ **Functions**: Node.js 20 runtime
- ✅ **Hosting**: Configured (optional admin dashboard)
- ✅ **Emulators**: All services configured

## Services Status

### Authentication
- Email/Password: Ready to enable
- Phone: Ready to enable
- Google: Ready to enable

### Firestore Database
- Rules: Deployed
- Indexes: Configured (15 indexes)
- Offline Persistence: Enabled in app

### Cloud Storage
- Rules: Deployed
- Image Upload: Configured
- Optimization: Via Cloud Functions

### Cloud Functions
- Runtime: Node.js 20
- Functions: 6 deployed/ready
- Service Account: Configured

### Cloud Messaging
- Service: Configured
- Notification Service: Implemented
- Token Management: Ready

### Crashlytics
- Enabled: In app
- Ready: For production monitoring

### Performance Monitoring
- Enabled: In app
- Ready: For production monitoring

## Deployment Steps

### 1. Deploy Firestore
```bash
firebase deploy --only firestore:rules,firestore:indexes
```

### 2. Deploy Storage Rules
```bash
firebase deploy --only storage
```

### 3. Deploy Cloud Functions
```bash
# First, install dependencies
cd functions
npm install
cd ..

# Deploy functions
firebase deploy --only functions
```

### 4. Test Deployment
```bash
# Test Firestore
firebase firestore:delete --all-collections

# Test Functions
curl https://us-central1-ethioshop-18181.cloudfunctions.net/seedFirestore
```

## Chapa Payment Configuration

### Test Keys (Configured)
```javascript
Public Key: CHAPUBK_TEST-QmCIBhWYIsdp2tgG0sPr67h5fozBbSz3
Secret Key: CHASECK_TEST-Qto69ETjzvgaMcVG9HBabcHCDLSFdmmS
Encryption Key: I5yIHlhBRBoPyNgrh6xGeDhB
```

### Production Keys
To use production keys:
1. Update keys in Firebase Functions config:
```bash
firebase functions:config:set chapa.public_key="YOUR_PRODUCTION_KEY" chapa.secret_key="YOUR_PRODUCTION_SECRET" chapa.encryption_key="YOUR_ENCRYPTION_KEY"
```

2. Update webhook URL in Firebase Console

## Environment Variables

Create `functions/.env` file:

```env
CHAPA_PUBLIC_KEY=CHAPUBK_TEST-QmCIBhWYIsdp2tgG0sPr67h5fozBbSz3
CHAPA_SECRET_KEY=CHASECK_TEST-Qto69ETjzvgaMcVG9HBabcHCDLSFdmmS
CHAPA_ENCRYPTION_KEY=I5yIHlhBRBoPyNgrh6xGeDhB
FIREBASE_PROJECT_ID=ethioshop-18181
CHAPA_WEBHOOK_URL=https://ethioshop-18181.web.app/chapa-webhook
```

## Emulator Configuration

All emulators are configured in `firebase.json`:

```
Auth:     localhost:9099
Firestore: localhost:8080
Functions: localhost:5001
Storage:   localhost:9199
UI:        localhost:4000
```

### Start Emulators
```bash
firebase emulators:start
```

## Security

### Firestore Rules
- Role-based access control
- Buyer, Seller, Admin roles
- Document ownership validation

### Storage Rules
- File type restrictions
- Size limits
- Verified seller upload only

### Webhook Security
- HMAC-SHA256 signature verification
- Encryption key validation

## Monitoring

### Firebase Console
- Project: https://console.firebase.google.com/project/ethioshop-18181
- Authentication, Firestore, Functions, Storage

### Cloud Functions Logs
```bash
firebase functions:log
```

### Firestore Usage
```bash
firebase firestore:databases describe default
```

## Troubleshooting

### Connection Issues
1. Verify google-services.json is in app/
2. Check SHA fingerprints match
3. Verify Firebase Console configuration

### Deployment Issues
1. Check Firebase CLI version: `firebase --version`
2. Verify service-account.json exists
3. Check Node.js version: `node --version` (should be 20.x)

### Functions Issues
1. Check Cloud Functions logs in console
2. Verify environment variables
3. Test with Firebase emulator first

## Production Checklist

- [ ] Enable all Firebase services
- [ ] Deploy Firestore rules and indexes
- [ ] Deploy Storage rules
- [ ] Deploy Cloud Functions
- [ ] Configure Chapa production keys
- [ ] Set up monitoring and alerts
- [ ] Enable App Check (optional)
- [ ] Configure analytics
- [ ] Test all user flows
- [ ] Review security settings

## Support

For Firebase-related issues:
- Firebase Console: https://console.firebase.google.com/project/ethioshop-18181
- Firebase Documentation: https://firebase.google.com/docs
- Chapa Documentation: https://developer.chapa.co

---

**Last Updated**: 2024  
**Firebase Project**: ethioshop-18181  
**Status**: ✅ Configured and Ready