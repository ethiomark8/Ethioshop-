# EthioShop - Ethiopian Marketplace Android App

A complete, production-ready Android marketplace application built with Kotlin and Firebase, designed for the Ethiopian market with full localization support (Amharic & English, RTL) and Chapa payment gateway integration.

## Features

### Core Features
- **User Authentication**: Email/Password, Phone (SMS OTP), Google Sign-In
- **Marketplace**: Browse, search, and filter products by category, price, condition, and location
- **Seller Verification**: Document-based seller verification system
- **Shopping Cart**: Offline-first cart with Room database
- **Checkout**: Complete checkout flow with Chapa payment integration
- **Order Management**: Track orders with real-time status updates
- **Real-time Chat**: In-app messaging between buyers and sellers
- **Reviews & Ratings**: Post-delivery product reviews
- **Push Notifications**: FCM-based notifications for orders and messages
- **Localization**: Full Amharic and English support with RTL layout
- **Admin Dashboard**: Web-based admin panel for platform management

## Technology Stack

### Android App
- **Language**: Kotlin 1.9.22
- **Min SDK**: 21 (Android 5.0)
- **Target SDK**: 34 (Android 14)
- **Architecture**: MVVM with Hilt Dependency Injection
- **UI**: Material Design Components, ViewBinding
- **Database**: Room (local cart)
- **Image Loading**: Coil
- **Navigation**: Jetpack Navigation Component

### Firebase Services
- **Authentication**: Email/Password, Phone, Google Sign-In
- **Firestore**: Real-time database with offline persistence
- **Storage**: Image hosting with automatic optimization
- **Cloud Functions**: Payment processing, image optimization, notifications
- **Cloud Messaging**: Push notifications
- **Crashlytics**: Crash reporting
- **Performance Monitoring**: App performance tracking

### Cloud Functions (Node.js 20)
- Chapa payment integration
- Image optimization (WebP conversion)
- Webhook handling
- Escrow management
- Push notifications

## Project Structure

```
EthioShop/
├── app/                              # Android application module
│   ├── src/main/
│   │   ├── java/com/ethio/shop/
│   │   │   ├── data/                # Data layer
│   │   │   │   ├── models/          # Data models
│   │   │   │   ├── local/           # Room database
│   │   │   │   ├── remote/          # Firebase services
│   │   │   │   └── repository/      # Repository pattern
│   │   │   ├── di/                  # Dependency injection
│   │   │   ├── ui/                  # UI layer
│   │   │   │   ├── activities/      # Activities
│   │   │   │   └── fragments/       # Fragments
│   │   │   ├── util/                # Utilities
│   │   │   └── notification/        # FCM service
│   │   └── res/                     # Resources
│   └── build.gradle.kts             # App build configuration
├── functions/                        # Cloud Functions
│   ├── index.js                     # Functions code
│   ├── package.json                 # Dependencies
│   └── seed_data.json               # Sample data
├── firebase.json                    # Firebase configuration
├── firestore.rules                  # Firestore security rules
├── firestore.indexes.json           # Firestore indexes
├── storage.rules                    # Storage security rules
├── build.gradle.kts                 # Root build configuration
├── settings.gradle.kts              # Gradle settings
└── .github/                         # CI/CD workflows
    └── workflows/
        ├── build-and-test.yml
        └── deploy-functions.yml
```

## Setup Instructions

### Prerequisites

- Android Studio Iguana (2023.2.1) or later
- Firebase CLI: `npm install -g firebase-tools`
- Node.js 20
- Java 17

### Firebase Project Setup

1. **Create Firebase Project**
   ```bash
   firebase login
   firebase init
   ```

2. **Enable Firebase Services**
   - Authentication (Email/Password, Phone, Google)
   - Firestore Database (start in test mode)
   - Firebase Storage
   - Cloud Functions (requires Blaze plan)
   - Cloud Messaging
   - Crashlytics
   - Performance Monitoring

3. **Configure Chapa Keys**
   ```bash
   firebase functions:config:set chapa.public_key="CHAPUBK_TEST-..." chapa.secret_key="CHASECK_TEST-..." chapa.encryption_key="..."
   ```

4. **Add Android App to Firebase**
   - Register app with package name: `com.ethio.shop`
   - Download `google-services.json`
   - Place it in `app/` directory

5. **Deploy Security Rules and Indexes**
   ```bash
   firebase deploy --only firestore:rules,firestore:indexes
   ```

### Running the App Locally

1. **Clone and Build**
   ```bash
   git clone <repository-url>
   cd EthioShop
   ./gradlew assembleDebug
   ```

2. **Run on Emulator/Device**
   - Open project in Android Studio
   - Sync Gradle
   - Run on emulator or device (API 21+)

3. **Start Firebase Emulator (Optional)**
   ```bash
   cd functions
   npm install
   cd ..
   firebase emulators:start
   ```

4. **Seed Test Data**
   ```bash
   curl -X POST http://localhost:5001/ethioshop/us-central1/seedFirestore \
     -H "x-secret: ethioshop-seed-secret-2024"
   ```

### Cloud Functions Deployment

```bash
# Install dependencies
cd functions
npm install

# Deploy to production
firebase deploy --only functions
```

## Chapa Payment Integration

The app uses Chapa test keys for sandbox environment:

- **Public Key**: `CHAPUBK_TEST-QmCIBhWYIsdp2tgG0sPr67h5fozBbSz3`
- **Secret Key**: `CHASECK_TEST-Qto69ETjzvgaMcVG9HBabcHCDLSFdmmS`
- **Encryption Key**: `I5yIHlhBRBoPyNgrh6xGeDhB`

### Payment Flow

1. Buyer proceeds to checkout
2. App calls `createChapaPayment` Cloud Function
3. Function initializes Chapa payment session
4. App opens Chapa checkout URL in Chrome Custom Tab
5. Buyer completes payment
6. Chapa sends webhook to `chapaWebhook` function
7. Webhook verifies signature and updates order status
8. Funds held in escrow until order delivered
9. `releaseEscrow` function releases funds to seller

## Security

### Firestore Security Rules
- Role-based access control (buyer, seller, admin)
- Document ownership verification
- Field validation
- Read/write permissions per collection

### Storage Security Rules
- Image upload restrictions (file type, size)
- Owner-only write access
- Public read access for optimized images

### Cloud Functions Security
- Authenticated user verification
- Webhook signature verification (HMAC-SHA256)
- Secrets management via Firebase config

## Localization

The app supports two languages:
- **English** (en): Default language
- **Amharic** (am): RTL support with Ethiopian Ge'ez script

Language can be changed in app settings:
```kotlin
LocaleManager.setNewLocale(context, "am") // or "en"
```

## Testing

### Unit Tests
```bash
./gradlew test
```

### Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

### UI Tests (Espresso)
```bash
./gradlew connectedAndroidTest
```

## CI/CD

The project includes GitHub Actions workflows for:
- **Build and Test**: Lint, unit tests, APK build
- **Deploy Functions**: Automatic deployment to Firebase

See `.github/workflows/` for configuration.

## Performance Optimizations

- **Offline Support**: Firestore offline persistence
- **Image Optimization**: Automatic WebP conversion and resizing
- **Pagination**: Paging 3 for efficient list loading
- **Lazy Loading**: Coil image loading with caching
- **Code Shrinking**: R8/ProGuard for reduced APK size
- **Target APK Size**: ≤15MB

## Accessibility

- Touch targets ≥48dp
- Content descriptions for images
- WCAG AA contrast ratios
- Screen reader support
- Keyboard navigation support

## Browser Support (Admin Dashboard)

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Known Issues & Limitations

- Firestore doesn't support full-text search natively (consider Algolia for production)
- Image optimization requires Blaze plan for Cloud Functions
- Real-time chat limited to 50 recent messages per conversation
- Phone OTP requires Firebase Authentication phone provider setup

## Future Enhancements

- [ ] Algolia integration for advanced search
- [ ] In-app video calls
- [ ] Product recommendations
- [ ] Advanced analytics dashboard
- [ ] Multi-vendor shipping
- [ ] Promo codes and discounts
- [ ] Wish list functionality
- [ ] Product comparison

## Support

For issues and questions:
- Create an issue in the GitHub repository
- Check documentation in `/docs`
- Review Firebase Console logs
- Test with Firebase Emulator Suite

