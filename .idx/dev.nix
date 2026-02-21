# EthioShop - Google IDX Development Environment
# Latest stable channel with Android SDK and all necessary tools

{ pkgs, ... }: {
  # Which channel nixpkgs should use
  channel = "stable-24.05"; # Pinning to stable channel

  # Enable the Android SDK
  android = {
    enable = true;
    # Use the latest stable Android SDK
    sdkVersion = "34";
    # Include build tools
    buildTools = [
      "34.0.0"
      "33.0.0"
      "32.0.0"
    ];
    # Include platform tools
    platformTools = true;
    # Include platforms
    platforms = [
      "android-34"
      "android-33"
      "android-21"
    ];
    # Include CMake for native builds
    cmake = true;
    # Include NDK
    ndk = true;
    # Include emulator
    emulator = true;
  };

  # Enable Java
  java = {
    enable = true;
    jdk = pkgs.jdk17;
  };

  # Packages to install
  packages = [
    # Version control
    pkgs.git
    pkgs.git-lfs

    # Node.js for Cloud Functions
    pkgs.nodejs_20
    pkgs.nodePackages.npm
    pkgs.yarn

    # Build tools
    pkgs.gradle
    pkgs.gradle_8

    # Android development tools
    pkgs.android-tools
    pkgs.android-studio

    # Firebase CLI
    pkgs.firebase-tools

    # Shell utilities
    pkgs.bash
    pkgs.zsh
    pkgs.fish
    pkgs.starship

    # Text editors and tools
    pkgs.vim
    pkgs.neovim
    pkgs.nano
    pkgs.ripgrep
    pkgs.fd
    pkgs.fzf
    pkgs.bat
    pkgs.eza
    pkgs.htop
    pkgs.tree
    pkgs.jq

    # Network tools
    pkgs.curl
    pkgs.wget
    pkgs.httpie

    # Compression tools
    pkgs.zip
    pkgs.unzip
    pkgs.gzip

    # Image tools
    pkgs.imagemagick

    # Other utilities
    pkgs.watch
    pkgs.tmux
    pkgs.screen
  ];

  # Environment variables
  env = {
    # Android SDK paths
    ANDROID_HOME = "/nix/store/.../android-sdk";
    ANDROID_SDK_ROOT = "/nix/store/.../android-sdk";
    
    # Java home
    JAVA_HOME = "${pkgs.jdk17.home}";
    
    # Gradle options
    GRADLE_OPTS = "-Dorg.gradle.daemon=false -Dorg.gradle.parallel=true -Dorg.gradle.jvmargs='-Xmx2048m -XX:MaxMetaspaceSize=512m'";
    
    # Node options
    NODE_OPTIONS = "--max-old-space-size=4096";
    
    # Firebase
    FIREBASE_PROJECT = "ethioshop-18181";
  };

  # Shell configuration
  shell = {
    startup = ''
      # Welcome message
      echo "🚀 EthioShop Development Environment"
      echo "===================================="
      echo "✓ Android SDK: Enabled"
      echo "✓ Java 17: Enabled"
      echo "✓ Node.js 20: Enabled"
      echo "✓ Firebase CLI: Enabled"
      echo ""
      echo "Quick Start:"
      echo "  1. Copy google-services.json to app/"
      echo "  2. Copy service-account.json to functions/"
      echo "  3. Run: ./gradlew assembleDebug"
      echo ""
    '';
    
    hooks = {
      # Set up Android SDK environment
      android-sdk-setup = ''
        export ANDROID_HOME=$HOME/Android/Sdk
        export ANDROID_SDK_ROOT=$ANDROID_HOME
        export PATH=$PATH:$ANDROID_HOME/emulator
        export PATH=$PATH:$ANDROID_HOME/tools
        export PATH=$PATH:$ANDROID_HOME/tools/bin
        export PATH=$PATH:$ANDROID_HOME/platform-tools
      '';
      
      # Set up Java environment
      java-setup = ''
        export JAVA_HOME=${pkgs.jdk17.home}
        export PATH=$PATH:$JAVA_HOME/bin
      '';
      
      # Set up Node.js environment
      node-setup = ''
        export PATH=$PATH:$(npm config get prefix)/bin
      '';
      
      # Set up Firebase environment
      firebase-setup = ''
        export PATH=$PATH:$HOME/.local/bin
      '';
    };
  };

  # IDX extensions
  idx.extensions = [
    # Android development
    "ms-vscode.vscode-android-debug-adapter"
    "vscjava.vscode-java-pack"
    "redhat.java"
    "vscjava.vscode-java-debug"
    "vscjava.vscode-java-dependency"
    "vscjava.vscode-java-test"
    "vscjava.vscode-maven"
    
    # Kotlin
    "fwcd.kotlin"
    
    # Gradle
    "richardwillis.vscode-gradle"
    
    # Firebase
    "firebase.firebase-tools"
    
    # Git
    "eamodio.gitlens"
    "mhutchie.git-graph"
    
    # General development
    "dbaeumer.vscode-eslint"
    "esbenp.prettier-vscode"
    "streetsidesoftware.code-spell-checker"
    
    # Shell
    "mads-hartmann.bash-ide-vscode"
    
    # YAML
    "redhat.vscode-yaml"
    
    # JSON
    "vscode.json"
    
    # Markdown
    "yzhang.markdown-all-in-one"
    
    # Docker (if needed)
    "ms-azuretools.vscode-docker"
    
    # REST API testing
    "humao.rest-client"
    
    # TODO highlighting
    "wayou.vscode-todo-highlight"
    
    # Bracket colorizer
    "coenraads.bracket-pair-colorizer-2"
    
    # Indent guides
    "oderwat.indent-rainbow"
    
    # Auto rename tag
    "formulahendry.auto-rename-tag"
    
    # Auto close tag
    "formulahendry.auto-close-tag"
    
    # Path intellisense
    "christian-kohler.path-intellisense"
    
    # File icons
    "vscode-icons-team.vscode-icons"
    
    # Material icon theme
    "PKief.material-icon-theme"
    
    # One dark pro theme
    "binaryify.one-dark-pro"
    
    # GitHub copilot (if available)
    "GitHub.copilot"
    "GitHub.copilot-chat"
  ];

  # Previews configuration
  idx.previews = {
    enable = true;
    previews = {
      # Android emulator preview
      web = {
        command = ["npm" "run" "dev"];
        manager = "web";
      };
    };
  };

  # Services to run
  idx.services = {
    # Firebase emulators
    firebase = {
      command = ["firebase" "emulators:start" "--only" "auth,firestore,functions,storage"];
      env = {
        FIREBASE_AUTH_EMULATOR_HOST = "localhost:9099";
        FIRESTORE_EMULATOR_HOST = "localhost:8080";
        FUNCTIONS_EMULATOR_HOST = "localhost:5001";
        STORAGE_EMULATOR_HOST = "localhost:9199";
      };
    };
  };
}