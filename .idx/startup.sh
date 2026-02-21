#!/bin/bash

# EthioShop - IDX Startup Script
# This script helps you get started quickly on Google IDX

set -e

echo "🚀 EthioShop - IDX Startup Script"
echo "=================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to print colored output
print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

# Check if we're in IDX
if [ ! -d ".idx" ]; then
    print_error "This script must be run from the project root directory"
    exit 1
fi

print_success "Running in IDX environment"

# Check Java
echo ""
echo "📦 Checking Java installation..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1)
    print_success "Java installed: $JAVA_VERSION"
else
    print_error "Java not found. Please wait for IDX to finish setup."
    exit 1
fi

# Check Node.js
echo ""
echo "📦 Checking Node.js installation..."
if command -v node &> /dev/null; then
    NODE_VERSION=$(node --version)
    print_success "Node.js installed: $NODE_VERSION"
else
    print_error "Node.js not found. Please wait for IDX to finish setup."
    exit 1
fi

# Check Gradle
echo ""
echo "📦 Checking Gradle installation..."
if [ -f "./gradlew" ]; then
    GRADLE_VERSION=$(./gradlew --version | grep "Gradle" | head -n 1)
    print_success "Gradle installed: $GRADLE_VERSION"
else
    print_error "Gradle wrapper not found. Please check the project structure."
    exit 1
fi

# Check Firebase CLI
echo ""
echo "📦 Checking Firebase CLI installation..."
if command -v firebase &> /dev/null; then
    FIREBASE_VERSION=$(firebase --version)
    print_success "Firebase CLI installed: $FIREBASE_VERSION"
else
    print_warning "Firebase CLI not found. You may need to install it manually."
    print_warning "Run: npm install -g firebase-tools"
fi

# Check Android SDK
echo ""
echo "📦 Checking Android SDK..."
if [ -n "$ANDROID_HOME" ]; then
    print_success "Android SDK found at: $ANDROID_HOME"
else
    print_warning "ANDROID_HOME not set. This may be set by Nix automatically."
fi

# Check Firebase configuration files
echo ""
echo "🔍 Checking Firebase configuration..."

if [ -f "app/google-services.json" ]; then
    print_success "google-services.json found in app/"
else
    print_warning "google-services.json not found in app/"
    print_warning "Please copy your Firebase configuration file to app/google-services.json"
fi

if [ -f "functions/service-account.json" ]; then
    print_success "service-account.json found in functions/"
else
    print_warning "service-account.json not found in functions/"
    print_warning "Please copy your Firebase service account key to functions/service-account.json"
fi

# Check environment files
echo ""
echo "🔍 Checking environment files..."

if [ -f ".env" ]; then
    print_success ".env file found"
else
    print_warning ".env file not found"
fi

if [ -f "functions/.env" ]; then
    print_success "functions/.env file found"
else
    print_warning "functions/.env file not found"
fi

# Install Node.js dependencies for functions
echo ""
echo "📦 Installing Node.js dependencies for Cloud Functions..."
if [ -d "functions" ]; then
    cd functions
    if [ ! -d "node_modules" ]; then
        print_warning "Installing npm dependencies..."
        npm install
        print_success "Dependencies installed"
    else
        print_success "Dependencies already installed"
    fi
    cd ..
else
    print_error "functions directory not found"
fi

# Grant execute permission to gradlew
echo ""
echo "🔧 Setting up Gradle wrapper..."
chmod +x ./gradlew
print_success "Gradle wrapper is executable"

# Summary
echo ""
echo "=================================="
echo "📋 Setup Summary"
echo "=================================="
echo ""
echo "✓ Environment is ready!"
echo ""
echo "Next steps:"
echo ""
echo "1. If you haven't already, configure Firebase:"
echo "   - Copy google-services.json to app/"
echo "   - Copy service-account.json to functions/"
echo ""
echo "2. Build the app:"
echo "   ./gradlew assembleDebug"
echo ""
echo "3. Run tests:"
echo "   ./gradlew test"
echo ""
echo "4. Start Firebase emulators (optional):"
echo "   firebase emulators:start"
echo ""
echo "5. Deploy Cloud Functions:"
echo "   firebase deploy --only functions"
echo ""
echo "For more information, see:"
echo "  - IDX_SETUP_GUIDE.md"
echo "  - README.md"
echo "  - BUILD_INSTRUCTIONS.md"
echo ""
echo "Happy coding! 🚀"