const functions = require('firebase-functions');
const admin = require('firebase-admin');
const axios = require('axios');
const sharp = require('sharp');
const { v4: uuidv4 } = require('uuid');
const crypto = require('crypto');

admin.initializeApp();

// Chapa configuration (test keys)
const CHAPA_PUBLIC_KEY = 'CHAPUBK_TEST-QmCIBhWYIsdp2tgG0sPr67h5fozBbSz3';
const CHAPA_SECRET_KEY = 'CHASECK_TEST-Qto69ETjzvgaMcVG9HBabcHCDLSFdmmS';
const CHAPA_ENCRYPTION_KEY = 'I5yIHlhBRBoPyNgrh6xGeDhB';

// ============================================================================
// CHAPA PAYMENT INTEGRATION
// ============================================================================

/**
 * Create Chapa Payment - Callable Function
 * Initiates a payment session with Chapa API
 */
exports.createChapaPayment = functions.https.onCall(async (data, context) => {
  // 1. Authenticate
  if (!context.auth) {
    throw new functions.https.HttpsError('unauthenticated', 'Must be logged in');
  }
  
  const { orderId, returnUrl } = data;
  
  if (!orderId) {
    throw new functions.https.HttpsError('invalid-argument', 'Order ID is required');
  }
  
  const orderRef = admin.firestore().collection('orders').doc(orderId);
  const orderDoc = await orderRef.get();
  
  if (!orderDoc.exists) {
    throw new functions.https.HttpsError('not-found', 'Order not found');
  }
  
  const order = orderDoc.data();
  
  // 2. Validate order ownership and status
  if (order.buyerId !== context.auth.uid) {
    throw new functions.https.HttpsError('permission-denied', 'Not your order');
  }
  
  if (order.paymentStatus !== 'pending') {
    throw new functions.https.HttpsError('failed-precondition', 'Order already paid');
  }
  
  // 3. Prepare Chapa payload
  const fullName = order.address.fullName || '';
  const nameParts = fullName.split(' ');
  const firstName = nameParts[0] || 'Customer';
  const lastName = nameParts.slice(1).join(' ') || ' ';
  
  const txRef = `order-${orderId}-${Date.now()}`;
  
  // Get webhook URL from Firebase config or use default
  const webhookUrl = functions.config().chapa.webhook_url || 
                     `https://ethioshop.web.app/chapa-webhook`;
  
  const payload = {
    amount: order.totalETB,
    currency: 'ETB',
    email: context.auth.token.email || 'customer@example.com',
    first_name: firstName,
    last_name: lastName,
    tx_ref: txRef,
    callback_url: webhookUrl,
    return_url: returnUrl || 'ethioshop://payment/success',
    customization: {
      title: 'EthioShop Payment',
      description: `Order #${orderId}`
    }
  };
  
  // 4. Call Chapa API
  try {
    const response = await axios.post(
      'https://api.chapa.co/v1/transaction/initialize',
      payload,
      {
        headers: { 
          'Authorization': `Bearer ${CHAPA_SECRET_KEY}`,
          'Content-Type': 'application/json'
        }
      }
    );
    
    console.log('Chapa response:', response.data);
    
    if (!response.data.data || !response.data.data.checkout_url) {
      throw new Error('Invalid Chapa response');
    }
    
    // 5. Save payment record
    await admin.firestore().collection('payments').doc(txRef).set({
      orderId,
      buyerId: context.auth.uid,
      provider: 'chapa',
      sessionId: txRef,
      status: 'pending',
      amount: order.totalETB,
      currency: 'ETB',
      checkoutUrl: response.data.data.checkout_url,
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      updatedAt: admin.firestore.FieldValue.serverTimestamp()
    });
    
    // 6. Update order with payment reference
    await orderRef.update({
      'paymentMeta.sessionId': txRef,
      updatedAt: admin.firestore.FieldValue.serverTimestamp()
    });
    
    // 7. Return checkout URL
    return { 
      checkout_url: response.data.data.checkout_url,
      tx_ref: txRef
    };
    
  } catch (error) {
    console.error('Chapa payment initialization error:', error.response?.data || error.message);
    throw new functions.https.HttpsError(
      'internal',
      'Payment initialization failed: ' + (error.response?.data?.message || error.message)
    );
  }
});

/**
 * Chapa Webhook Handler - HTTP Function
 * Handles Chapa payment callbacks with signature verification
 */
exports.chapaWebhook = functions.https.onRequest(async (req, res) => {
  // 1. Verify signature using encryption key
  const signature = req.headers['x-hmac-signature'];
  const payload = JSON.stringify(req.body);
  const hash = crypto.createHmac('sha256', CHAPA_ENCRYPTION_KEY).update(payload).digest('hex');
  
  console.log('Webhook received:', req.body);
  console.log('Expected signature:', hash);
  console.log('Received signature:', signature);
  
  if (signature !== hash) {
    console.error('Invalid webhook signature');
    return res.status(401).send('Unauthorized');
  }
  
  const event = req.body;
  
  // 2. Handle only successful charges
  if (event.event === 'charge.success') {
    const txRef = event.data.tx_ref;
    const transactionId = event.data.transaction_id;
    
    try {
      const paymentRef = admin.firestore().collection('payments').doc(txRef);
      const paymentDoc = await paymentRef.get();
      
      if (!paymentDoc.exists) {
        console.error('Payment not found:', txRef);
        return res.status(404).send('Payment not found');
      }
      
      const payment = paymentDoc.data();
      
      // Update only if still pending
      if (payment.status === 'pending') {
        await paymentRef.update({
          status: 'success',
          transactionId: transactionId,
          meta: { chapaResponse: event.data },
          updatedAt: admin.firestore.FieldValue.serverTimestamp()
        });
        
        // Update order
        const orderRef = admin.firestore().collection('orders').doc(payment.orderId);
        await orderRef.update({
          paymentStatus: 'paid',
          status: 'confirmed',
          'paymentMeta.transactionId': transactionId,
          updatedAt: admin.firestore.FieldValue.serverTimestamp()
        });
        
        // Send notifications
        await sendPaymentNotification(payment.orderId, payment.buyerId, payment.amount);
        
        console.log('Payment processed successfully:', txRef);
      } else {
        console.log('Payment already processed:', txRef);
      }
    } catch (error) {
      console.error('Error processing webhook:', error);
      return res.status(500).send('Internal Server Error');
    }
  }
  
  res.status(200).send('OK');
});

/**
 * Release Escrow - Firestore Trigger
 * Releases funds to seller when order is delivered
 */
exports.releaseEscrow = functions.firestore
  .document('orders/{orderId}')
  .onUpdate(async (change, context) => {
    const newData = change.after.data();
    const previousData = change.before.data();
    
    // Check if order is delivered and payment is paid
    if (newData.status === 'delivered' && 
        newData.paymentStatus === 'paid' && 
        !newData.escrowReleased) {
      
      const paymentRef = admin.firestore().collection('payments')
        .where('orderId', '==', context.params.orderId)
        .where('status', '==', 'success')
        .limit(1);
      
      const paymentSnapshot = await paymentRef.get();
      
      if (paymentSnapshot.empty) {
        console.error('No successful payment found for order:', context.params.orderId);
        return null;
      }
      
      const payment = paymentSnapshot.docs[0].data();
      
      try {
        // Call Chapa transfer API to release funds
        const transferPayload = {
          amount: payment.amount,
          currency: 'ETB',
          account_name: newData.sellerName || 'Seller',
          account_number: newData.sellerAccount || '0000000000',
          bank_code: newData.sellerBankCode || '00', // Default Bank of Abyssinia
          reference: `escrow-release-${context.params.orderId}`
        };
        
        const response = await axios.post(
          'https://api.chapa.co/v1/transfers',
          transferPayload,
          {
            headers: { 
              'Authorization': `Bearer ${CHAPA_SECRET_KEY}`,
              'Content-Type': 'application/json'
            }
          }
        );
        
        console.log('Escrow release response:', response.data);
        
        // Update order
        await change.after.ref.update({
          escrowReleased: true,
          'paymentMeta.transferId': response.data.data?.transfer_id,
          updatedAt: admin.firestore.FieldValue.serverTimestamp()
        });
        
        // Send notification to seller
        await sendEscrowReleaseNotification(context.params.orderId, newData.sellerId);
        
      } catch (error) {
        console.error('Escrow release error:', error.response?.data || error.message);
        // Don't throw error, just log it
      }
    }
    
    return null;
  });

/**
 * Send payment notification
 */
async function sendPaymentNotification(orderId, buyerId, amount) {
  try {
    const userRef = admin.firestore().collection('users').doc(buyerId);
    const userDoc = await userRef.get();
    
    if (userDoc.exists) {
      const user = userDoc.data();
      const notification = {
        type: 'payment_success',
        recipientId: buyerId,
        payload: {
          title: 'Payment Successful',
          body: `Your payment of ${amount} ETB has been received`,
          data: { orderId }
        },
        status: 'unread',
        createdAt: admin.firestore.FieldValue.serverTimestamp()
      };
      
      await admin.firestore().collection('notifications').add(notification);
      
      // Send FCM if token exists
      if (user.fcmToken) {
        const message = {
          token: user.fcmToken,
          notification: {
            title: 'Payment Successful',
            body: `Your payment of ${amount} ETB has been received`
          },
          data: { orderId }
        };
        
        await admin.messaging().send(message);
      }
    }
  } catch (error) {
    console.error('Error sending payment notification:', error);
  }
}

/**
 * Send escrow release notification
 */
async function sendEscrowReleaseNotification(orderId, sellerId) {
  try {
    const userRef = admin.firestore().collection('users').doc(sellerId);
    const userDoc = await userRef.get();
    
    if (userDoc.exists) {
      const user = userDoc.data();
      const notification = {
        type: 'escrow_released',
        recipientId: sellerId,
        payload: {
          title: 'Funds Released',
          body: 'Payment has been released to your account',
          data: { orderId }
        },
        status: 'unread',
        createdAt: admin.firestore.FieldValue.serverTimestamp()
      };
      
      await admin.firestore().collection('notifications').add(notification);
      
      // Send FCM if token exists
      if (user.fcmToken) {
        const message = {
          token: user.fcmToken,
          notification: {
            title: 'Funds Released',
            body: 'Payment has been released to your account'
          },
          data: { orderId }
        };
        
        await admin.messaging().send(message);
      }
    }
  } catch (error) {
    console.error('Error sending escrow notification:', error);
  }
}

// ============================================================================
// IMAGE OPTIMIZATION
// ============================================================================

/**
 * Optimize Image - Storage Trigger
 * Compresses and converts images to WebP format
 */
exports.optimizeImage = functions.storage.object().onFinalize(async (object) => {
  const filePath = object.name;
  const contentType = object.contentType;
  
  // Only process images in /originals/ folder
  if (!filePath.includes('/originals/') || !contentType.startsWith('image/')) {
    return null;
  }
  
  // Extract product ID from path
  const pathParts = filePath.split('/');
  const productId = pathParts[1];
  const fileName = pathParts[pathParts.length - 1];
  const baseName = fileName.split('.')[0];
  
  const bucket = admin.storage().bucket(object.bucket);
  const file = bucket.file(filePath);
  
  try {
    // Download original image
    const [buffer] = await file.download();
    
    // Optimize image: resize to 800x800, convert to WebP with 85% quality
    const optimizedBuffer = await sharp(buffer)
      .resize(800, 800, {
        fit: 'inside',
        withoutEnlargement: true
      })
      .webp({ quality: 85 })
      .toBuffer();
    
    // Upload optimized image
    const optimizedPath = `products/${productId}/optimized/${baseName}.webp`;
    const optimizedFile = bucket.file(optimizedPath);
    
    await optimizedFile.save(optimizedBuffer, {
      contentType: 'image/webp',
      metadata: {
        optimized: true,
        source: filePath
      }
    });
    
    // Make optimized file public
    await optimizedFile.makePublic();
    
    // Get optimized URL
    const [url] = await optimizedFile.getSignedUrl({
      action: 'read',
      expires: Date.now() + 365 * 24 * 60 * 60 * 1000 // 1 year
    });
    
    // Update product document with optimized URL
    const productRef = admin.firestore().collection('products').doc(productId);
    await productRef.update({
      optimizedImages: admin.firestore.FieldValue.arrayUnion(url),
      updatedAt: admin.firestore.FieldValue.serverTimestamp()
    });
    
    console.log('Image optimized successfully:', optimizedPath);
    return null;
    
  } catch (error) {
    console.error('Error optimizing image:', error);
    return null;
  }
});

// ============================================================================
// SEED DATA (Development Only)
// ============================================================================

/**
 * Seed Firestore - HTTP Function
 * Populates database with initial data
 */
exports.seedFirestore = functions.https.onRequest(async (req, res) => {
  // Verify secret header for security
  const secret = req.headers['x-secret'];
  if (secret !== 'ethioshop-seed-secret-2024') {
    return res.status(403).send('Forbidden');
  }
  
  try {
    const db = admin.firestore();
    const batch = db.batch();
    
    // Sample categories
    const categories = [
      'Electronics',
      'Clothing',
      'Home & Garden',
      'Books',
      'Sports',
      'Beauty',
      'Automotive',
      'Health',
      'Food',
      'Services'
    ];
    
    // Seed categories in a separate collection
    categories.forEach((category, index) => {
      const catRef = db.collection('categories').doc(`cat_${index}`);
      batch.set(catRef, {
        name: category,
        displayName: category,
        icon: '',
        createdAt: admin.firestore.FieldValue.serverTimestamp()
      });
    });
    
    // Sample locations
    const locations = [
      'Addis Ababa',
      'Dire Dawa',
      'Mekelle',
      'Gondar',
      'Bahir Dar',
      'Hawassa',
      'Jimma',
      'Dessie',
      'Adama',
      'Shashamane'
    ];
    
    locations.forEach((location, index) => {
      const locRef = db.collection('locations').doc(`loc_${index}`);
      batch.set(locRef, {
        name: location,
        createdAt: admin.firestore.FieldValue.serverTimestamp()
      });
    });
    
    await batch.commit();
    
    res.status(200).json({ 
      success: true, 
      message: 'Firestore seeded successfully',
      categories: categories.length,
      locations: locations.length
    });
    
  } catch (error) {
    console.error('Seeding error:', error);
    res.status(500).json({ 
      success: false, 
      error: error.message 
    });
  }
});

// ============================================================================
// NOTIFICATION HELPERS
// ============================================================================

/**
 * Send FCM notification to user
 */
exports.sendPushNotification = functions.https.onCall(async (data, context) => {
  if (!context.auth) {
    throw new functions.https.HttpsError('unauthenticated', 'Must be logged in');
  }
  
  const { recipientId, title, body, data: notificationData } = data;
  
  try {
    const userRef = admin.firestore().collection('users').doc(recipientId);
    const userDoc = await userRef.get();
    
    if (!userDoc.exists) {
      throw new functions.https.HttpsError('not-found', 'User not found');
    }
    
    const user = userDoc.data();
    
    if (!user.fcmToken) {
      throw new functions.https.HttpsError('failed-precondition', 'User has no FCM token');
    }
    
    const message = {
      token: user.fcmToken,
      notification: {
        title,
        body
      },
      data: notificationData || {}
    };
    
    await admin.messaging().send(message);
    
    return { success: true };
    
  } catch (error) {
    console.error('Error sending push notification:', error);
    throw new functions.https.HttpsError('internal', error.message);
  }
});