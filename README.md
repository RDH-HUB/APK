# Web2APK 🚀

> Convert a website into a simple Android APK.

Web2APK is a lightweight CLI tool that packages a website URL into an Android WebView APK using a customizable app name, package name, and icon.

## ✨ Features

- 🌐 Convert any website URL into an APK
- 📱 Custom app name
- 📦 Custom Android package name
- 🖼️ Custom app icon
- 🔧 Automatic `AndroidManifest.xml` patching
- 🔐 APK signing with `apksigner`
- ⚡ Simple command-line workflow

## 🛠️ Requirements

Make sure these are installed:

- [Node.js](https://nodejs.org/)
- Java JDK
- Android SDK Build Tools (`apksigner`)
- `curl`
- `unzip`

Check:

```bash
node --version
java --version
javac --version
apksigner --version
```

## 📥 Installation

```bash
git clone https://github.com/RDH-HUB/APK.git
cd APK
```

No `npm install` is required.

## 🚀 Usage

```bash
node apk.js "<App Name>" "<URL>" "<Package Name>" "<Icon>" "[Output APK]"
```

Example:

```bash
node apk.js   "My Website"   "https://example.com"   "com.example.mywebsite"   "icon.png"   "MyWebsite.apk"
```

If the output filename is omitted, the script will generate one automatically.

## 📁 Project Structure

```text
.
├── apk.js
├── patcher_src/
│   ├── ManifestPatcher.java
│   └── com/rdhhub/bikinaplikasi/helper/
├── build_patcher/
├── template/
│   ├── NamaAplikasi.zip
│   ├── AndroidManifest.xml
│   └── mmdfauzan.key
└── README.md
```

## ⚙️ How It Works

```text
Website URL
    ↓
apk.js
    ↓
Patch AndroidManifest.xml
    ↓
Insert URL + App Icon
    ↓
Build APK
    ↓
Sign APK
    ↓
YourApp.apk
```

The Java manifest patcher modifies the template manifest with the selected app name and package name before the APK is generated.

## 🔐 Security

Do **not** publish private signing keys, passwords, API keys, or other sensitive credentials in a public repository.

If `template/mmdfauzan.key` is a private production signing key, replace it with your own secure signing setup before making the repository public.

## ⚠️ Disclaimer

Use this tool only with websites, assets, and content that you have permission to package and distribute.

You are responsible for the APK you build and distribute.