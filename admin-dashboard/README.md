# EthioShop Admin Dashboard

A web-based admin dashboard for managing the EthioShop marketplace.

## Features

- **Dashboard Overview**: View key statistics (users, products, orders, revenue)
- **Recent Orders**: Monitor latest orders with status tracking
- **Recent Products**: View recently added products
- **Quick Actions**: Access management features

## Setup Instructions

### 1. Firebase Configuration

Update the `firebaseConfig` object in `app.js` with your Firebase project credentials:

```javascript
const firebaseConfig = {
    apiKey: "YOUR_API_KEY",
    authDomain: "ethioshop-18181.firebaseapp.com",
    projectId: "ethioshop-18181",
    storageBucket: "ethioshop-18181.appspot.com",
    messagingSenderId: "1009602861548",
    appId: "YOUR_WEB_APP_ID"
};
```

### 2. Firebase Security Rules

Ensure your Firestore rules allow admin access:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.auth != null;
    }
  }
}
```

### 3. Deployment Options

#### Option 1: Firebase Hosting
```bash
npm install -g firebase-tools
firebase login
firebase init hosting
firebase deploy
```

#### Option 2: Static Web Server
```bash
# Using Python
python -m http.server 8000

# Using Node.js
npx serve
```

#### Option 3: GitHub Pages
1. Push to GitHub repository
2. Enable GitHub Pages in repository settings
3. Select `admin-dashboard` folder as source

## Usage

1. Open the dashboard in a web browser
2. Sign in with Firebase Authentication
3. View statistics and manage the marketplace

## Features Coming Soon

- User Management (add, edit, delete users)
- Product Management (approve, reject, delete products)
- Order Management (view, update order status)
- Reports and Analytics
- System Settings
- Real-time notifications

## Technologies Used

- HTML5
- CSS3
- JavaScript (ES6+)
- Firebase SDK (Firestore, Auth)

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Security Notes

- Ensure Firebase Authentication is properly configured
- Use Firestore security rules to restrict access
- Enable Firebase App Check for production
- Use HTTPS for production deployment

## Support

For issues or questions, please contact the development team.