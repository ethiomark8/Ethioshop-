import { initializeApp } from 'https://www.gstatic.com/firebasejs/10.7.1/firebase-app.js';
import { getFirestore, collection, getDocs, query, orderBy, limit } from 'https://www.gstatic.com/firebasejs/10.7.1/firebase-firestore.js';
import { getAuth, onAuthStateChanged, signOut } from 'https://www.gstatic.com/firebasejs/10.7.1/firebase-auth.js';

// Firebase Configuration - Replace with your actual config
const firebaseConfig = {
    apiKey: "YOUR_API_KEY",
    authDomain: "ethioshop-18181.firebaseapp.com",
    projectId: "ethioshop-18181",
    storageBucket: "ethioshop-18181.appspot.com",
    messagingSenderId: "1009602861548",
    appId: "1:1009602861548:web:1234567890"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const db = getFirestore(app);
const auth = getAuth(app);

// DOM Elements
const loadingOverlay = document.getElementById('loadingOverlay');
const totalUsersEl = document.getElementById('totalUsers');
const totalProductsEl = document.getElementById('totalProducts');
const totalOrdersEl = document.getElementById('totalOrders');
const totalRevenueEl = document.getElementById('totalRevenue');
const ordersTable = document.getElementById('ordersTable');
const productsTable = document.getElementById('productsTable');
const logoutBtn = document.getElementById('logoutBtn');

// Show/Hide Loading
function showLoading() {
    loadingOverlay.classList.add('active');
}

function hideLoading() {
    loadingOverlay.classList.remove('active');
}

// Format Currency
function formatETB(amount) {
    return 'ETB ' + amount.toLocaleString('en-US', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    });
}

// Get Status Badge Class
function getStatusBadgeClass(status) {
    switch (status) {
        case 'active':
        case 'confirmed':
        case 'delivered':
        case 'paid':
            return 'status-active';
        case 'pending':
            return 'status-pending';
        case 'completed':
            return 'status-completed';
        case 'cancelled':
        case 'deleted':
            return 'status-cancelled';
        default:
            return 'status-pending';
    }
}

// Load Dashboard Stats
async function loadStats() {
    showLoading();
    
    try {
        // Load Users
        const usersSnapshot = await getDocs(collection(db, 'users'));
        totalUsersEl.textContent = usersSnapshot.size;

        // Load Products
        const productsSnapshot = await getDocs(collection(db, 'products'));
        totalProductsEl.textContent = productsSnapshot.size;

        // Load Orders
        const ordersSnapshot = await getDocs(collection(db, 'orders'));
        totalOrdersEl.textContent = ordersSnapshot.size;

        // Calculate Revenue
        let totalRevenue = 0;
        ordersSnapshot.forEach(doc => {
            const order = doc.data();
            if (order.status === 'delivered' && order.paymentStatus === 'paid') {
                totalRevenue += order.totalETB || 0;
            }
        });
        totalRevenueEl.textContent = formatETB(totalRevenue);

        // Load Recent Orders
        await loadRecentOrders();

        // Load Recent Products
        await loadRecentProducts();

    } catch (error) {
        console.error('Error loading stats:', error);
        alert('Error loading dashboard data. Please check your Firebase configuration.');
    } finally {
        hideLoading();
    }
}

// Load Recent Orders
async function loadRecentOrders() {
    try {
        const ordersQuery = query(
            collection(db, 'orders'),
            orderBy('createdAt', 'desc'),
            limit(5)
        );
        const ordersSnapshot = await getDocs(ordersQuery);

        if (ordersSnapshot.empty) {
            ordersTable.innerHTML = '<tr><td colspan="4" class="text-center">No orders found</td></tr>';
            return;
        }

        ordersTable.innerHTML = ordersSnapshot.docs.map(doc => {
            const order = doc.data();
            const orderId = doc.id.slice(-6);
            return `
                <tr>
                    <td>#${orderId}</td>
                    <td>${order.buyerName || 'N/A'}</td>
                    <td>${formatETB(order.totalETB || 0)}</td>
                    <td><span class="status-badge ${getStatusBadgeClass(order.status)}">${order.status}</span></td>
                </tr>
            `;
        }).join('');
    } catch (error) {
        console.error('Error loading orders:', error);
        ordersTable.innerHTML = '<tr><td colspan="4" class="text-center">Error loading orders</td></tr>';
    }
}

// Load Recent Products
async function loadRecentProducts() {
    try {
        const productsQuery = query(
            collection(db, 'products'),
            orderBy('createdAt', 'desc'),
            limit(5)
        );
        const productsSnapshot = await getDocs(productsQuery);

        if (productsSnapshot.empty) {
            productsTable.innerHTML = '<tr><td colspan="4" class="text-center">No products found</td></tr>';
            return;
        }

        productsTable.innerHTML = productsSnapshot.docs.map(doc => {
            const product = doc.data();
            return `
                <tr>
                    <td>${product.title || 'N/A'}</td>
                    <td>${product.sellerName || 'N/A'}</td>
                    <td>${formatETB(product.priceETB || 0)}</td>
                    <td><span class="status-badge ${getStatusBadgeClass(product.status)}">${product.status}</span></td>
                </tr>
            `;
        }).join('');
    } catch (error) {
        console.error('Error loading products:', error);
        productsTable.innerHTML = '<tr><td colspan="4" class="text-center">Error loading products</td></tr>';
    }
}

// Quick Actions
function manageUsers() {
    alert('User Management feature coming soon!');
}

function manageProducts() {
    alert('Product Management feature coming soon!');
}

function viewReports() {
    alert('Reports feature coming soon!');
}

function systemSettings() {
    alert('System Settings feature coming soon!');
}

function viewAllOrders() {
    alert('View All Orders feature coming soon!');
}

function viewAllProducts() {
    alert('View All Products feature coming soon!');
}

// Logout
logoutBtn.addEventListener('click', async () => {
    try {
        await signOut(auth);
        alert('Logged out successfully!');
        // Redirect to login page
        window.location.href = 'login.html';
    } catch (error) {
        console.error('Logout error:', error);
        alert('Error logging out');
    }
});

// Auth State Observer
onAuthStateChanged(auth, (user) => {
    if (user) {
        // User is signed in
        document.getElementById('userName').textContent = user.displayName || user.email || 'Admin User';
        loadStats();
    } else {
        // User is signed out
        alert('Please sign in to access the admin dashboard');
        window.location.href = 'login.html';
    }
});

// Initialize on page load
document.addEventListener('DOMContentLoaded', () => {
    console.log('Admin Dashboard loaded');
});