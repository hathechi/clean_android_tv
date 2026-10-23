#!/usr/bin/env bash
# Build & Cài đặt Launcher Retro Việt Nam (Lịch Bloc, Giá vàng, Xăng dầu, 4 Chủ đề)
set -euo pipefail
cd "$(dirname "$0")"

export TV_HOST="${TV_HOST:-192.168.1.103}"
export JAVA_HOME="${JAVA_HOME:-/opt/homebrew/opt/openjdk@17}"
export ANDROID_HOME="${ANDROID_HOME:-/opt/homebrew/share/android-commandlinetools}"
export PATH="$JAVA_HOME/bin:$PATH"

PY=tools/venv/bin/python
[ -x "$PY" ] || PY=tools/venv/Scripts/python.exe
export MSYS_NO_PATHCONV=1

echo "1. Đang biên dịch Launcher Retro Việt Nam..."
cd launcher-vietnam
./gradlew assembleRelease -q --console=plain
cd ..

APK=launcher-vietnam/app/build/outputs/apk/release/app-release.apk

echo "2. Đang nạp Launcher lên TV ($TV_HOST)..."
"$PY" -I tools/b64push.py "$APK" /data/local/tmp/retrolauncher.apk

echo "3. Đang cài đặt và thiết lập màn hình chính..."
printf '%s\n' \
  "pm install -r /data/local/tmp/retrolauncher.apk" \
  "rm -f /data/local/tmp/retrolauncher.apk" \
  "cmd package compile -m speed -f com.vietnam.retrolauncher" \
  "am start -W -n com.vietnam.retrolauncher/.MainActivity" \
  "cmd package set-home-activity --user 0 com.vietnam.retrolauncher/.MainActivity" \
  | "$PY" -I tools/tv.py --batch

echo "Hoàn tất! Bấm phím Home trên remote để trải nghiệm giao diện Retro Việt Nam."
