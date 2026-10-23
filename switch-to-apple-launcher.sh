#!/usr/bin/env bash
# Chuyển về Launcher phong cách Apple TV (com.gathin.tvhome)
set -euo pipefail
cd "$(dirname "$0")"

export TV_HOST="${TV_HOST:-192.168.1.103}"
PY=tools/venv/bin/python
[ -x "$PY" ] || PY=tools/venv/Scripts/python.exe

printf '%s\n' \
  "cmd package set-home-activity --user 0 com.gathin.tvhome/.MainActivity" \
  "am start -W -n com.gathin.tvhome/.MainActivity" \
  | "$PY" -I tools/tv.py --batch

echo "Đã chuyển về Launcher phong cách Apple TV thành công!"
