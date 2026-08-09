#!/bin/sh
set -e
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd -P)
PROPS="$APP_HOME/gradle/wrapper/gradle-wrapper.properties"
DIST_URL=$(sed -n 's/^distributionUrl=//p' "$PROPS" | sed 's/\\:/:/g')
VERSION=$(printf '%s' "$DIST_URL" | sed -n 's#.*gradle-\([^/]*\)-bin\.zip#\1#p')
GRADLE_USER_HOME=${GRADLE_USER_HOME:-"$HOME/.gradle"}
DIST_DIR="$GRADLE_USER_HOME/wrapper/dists/cot-monitor/gradle-$VERSION"
GRADLE_BIN="$DIST_DIR/bin/gradle"
if [ ! -x "$GRADLE_BIN" ]; then
  mkdir -p "$GRADLE_USER_HOME/wrapper/dists/cot-monitor"
  ZIP="$GRADLE_USER_HOME/wrapper/dists/cot-monitor/gradle-$VERSION-bin.zip"
  if [ ! -f "$ZIP" ]; then
    command -v curl >/dev/null 2>&1 || { echo "ERROR: curl is required to download Gradle." >&2; exit 1; }
    echo "Downloading Gradle $VERSION from $DIST_URL"
    curl -fL --retry 3 --connect-timeout 10 -o "$ZIP" "$DIST_URL"
  fi
  command -v unzip >/dev/null 2>&1 || { echo "ERROR: unzip is required to install Gradle." >&2; exit 1; }
  rm -rf "$DIST_DIR.tmp"
  mkdir -p "$DIST_DIR.tmp"
  unzip -q "$ZIP" -d "$DIST_DIR.tmp"
  FOUND=$(find "$DIST_DIR.tmp" -maxdepth 2 -type f -path '*/bin/gradle' | head -1)
  [ -n "$FOUND" ] || { echo "ERROR: Gradle archive did not contain a bin/gradle executable." >&2; exit 1; }
  mv "$(dirname "$(dirname "$FOUND")")" "$DIST_DIR"
  rm -rf "$DIST_DIR.tmp"
fi
exec "$GRADLE_BIN" "$@"
