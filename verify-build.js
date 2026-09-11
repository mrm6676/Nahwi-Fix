const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

console.log('[INFO] Verifying Android Jetpack Compose codebase...');

const requiredFiles = [
  'metadata.json',
  'settings.gradle.kts',
  'build.gradle.kts',
  'gradle/libs.versions.toml',
  'app/build.gradle.kts',
  'app/src/main/AndroidManifest.xml',
  'app/src/main/res/values/strings.xml',
  'app/src/main/res/values/colors.xml',
  'app/src/main/res/values/themes.xml',
  'app/src/main/res/drawable/ic_launcher_background.xml',
  'app/src/main/res/drawable/ic_launcher_foreground.xml',
  'app/src/main/java/com/example/nahwifix/MainActivity.kt',
  'app/src/main/java/com/example/nahwifix/model/CorrectionModel.kt',
  'app/src/main/java/com/example/nahwifix/engine/ArabicGrammarEngine.kt',
  'app/src/main/java/com/example/nahwifix/data/AppDatabase.kt',
  'app/src/main/java/com/example/nahwifix/data/gemini/GeminiModels.kt',
  'app/src/main/java/com/example/nahwifix/data/gemini/GeminiApiService.kt',
  'app/src/main/java/com/example/nahwifix/data/gemini/GeminiRetrofitClient.kt',
  'app/src/main/java/com/example/nahwifix/data/repository/GeminiArabicGrammarRepository.kt',
  'app/src/main/java/com/example/nahwifix/ui/NahwiFixViewModel.kt',
  'app/src/main/java/com/example/nahwifix/ui/screens/HomeScreen.kt',
  'app/src/main/java/com/example/nahwifix/ui/screens/RulesGuideScreen.kt',
  'app/src/main/java/com/example/nahwifix/ui/screens/PricingScreen.kt',
  'app/src/main/java/com/example/nahwifix/ui/screens/AuthScreen.kt',
  'app/src/main/java/com/example/nahwifix/ui/screens/TermsScreen.kt'
];

let allPassed = true;
for (const f of requiredFiles) {
  const fullPath = path.resolve(__dirname, '..', f);
  if (!fs.existsSync(fullPath)) {
    console.error(`[ERROR] Missing required Android file: ${f}`);
    allPassed = false;
  }
}

if (!allPassed) {
  process.exit(1);
}

try {
  execSync('gradle assembleDebug', { stdio: 'inherit' });
} catch (e) {
  console.error('[ERROR] Gradle assembleDebug failed:', e.message);
  process.exit(1);
}

console.log('[INFO] Build successful: APK ready at app/build/outputs/apk/debug/app-debug.apk');
process.exit(0);
