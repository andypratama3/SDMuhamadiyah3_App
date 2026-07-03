#!/usr/bin/env bash
# Salin Firebase config dari Firebase Console ke project lokal.
# File asli TIDAK di-commit — hanya *.example yang ada di git.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"

ANDROID_SRC="${1:-$HOME/Downloads/google-services.json}"
IOS_SRC="${2:-$HOME/Downloads/GoogleService-Info.plist}"

ANDROID_DST="$ROOT/androidApp/google-services.json"
IOS_DST="$ROOT/iosApp/iosApp/GoogleService-Info.plist"

if [[ ! -f "$ANDROID_SRC" ]]; then
  echo "Android: $ANDROID_SRC tidak ditemukan."
  echo "Download dari Firebase Console → Project Settings → Android app."
  exit 1
fi

if [[ ! -f "$IOS_SRC" ]]; then
  echo "iOS: $IOS_SRC tidak ditemukan."
  echo "Download dari Firebase Console → Project Settings → iOS app."
  exit 1
fi

cp "$ANDROID_SRC" "$ANDROID_DST"
cp "$IOS_SRC" "$IOS_DST"

echo "✓ $ANDROID_DST"
echo "✓ $IOS_DST"
echo ""
echo "Langkah berikutnya (iOS):"
echo "  cd iosApp && pod install"
echo "  open iosApp.xcworkspace"
