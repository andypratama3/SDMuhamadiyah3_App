#!/usr/bin/env bash
# Generate Android launcher + iOS app icon from shared logo_sd.png
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
LOGO="$ROOT/shared/src/commonMain/composeResources/drawable/logo_sd.png"
BG="#001B3D"

if [[ ! -f "$LOGO" ]]; then
  echo "Logo not found: $LOGO"
  exit 1
fi

generate_square() {
  local size="$1"
  local out="$2"
  local logo_size=$((size * 72 / 100))
  magick -size "${size}x${size}" "xc:${BG}" \
    \( "$LOGO" -resize "${logo_size}x${logo_size}" \) \
    -gravity center -composite \
    "$out"
}

generate_foreground() {
  local size="$1"
  local out="$2"
  local logo_size=$((size * 72 / 100))
  magick -size "${size}x${size}" xc:none \
    \( "$LOGO" -resize "${logo_size}x${logo_size}" \) \
    -gravity center -composite \
    "$out"
}

generate_notification() {
  local out="$1"
  magick "$LOGO" -resize 96x96 -alpha extract -threshold 10% -negate \
    -fill white -opaque black -transparent black \
    -resize 24x24 \
    "$out"
}

RES="$ROOT/androidApp/src/main/res"

# folder:launcher_size:foreground_size
while IFS=: read -r folder launcher_size fg_size; do
  mkdir -p "$RES/$folder"
  generate_square "$launcher_size" "$RES/$folder/ic_launcher.png"
  cp "$RES/$folder/ic_launcher.png" "$RES/$folder/ic_launcher_round.png"
  generate_foreground "$fg_size" "$RES/$folder/ic_launcher_foreground.png"
done <<'EOF'
mipmap-mdpi:48:108
mipmap-hdpi:72:162
mipmap-xhdpi:96:216
mipmap-xxhdpi:144:324
mipmap-xxxhdpi:192:432
EOF

generate_square 1024 "$ROOT/iosApp/iosApp/Assets.xcassets/AppIcon.appiconset/app-icon-1024.png"

mkdir -p "$ROOT/shared/src/androidMain/res/drawable"
generate_notification "$ROOT/shared/src/androidMain/res/drawable/ic_notification.png"

echo "App icons generated from logo_sd.png"
