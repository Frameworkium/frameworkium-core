#!/usr/bin/env bash

# Defensive bash script settings
set -o nounset -o pipefail -o errexit -o noglob

if [ $# -gt 0 ]; then
  echo "
This script checks if you have a chromedriver, whether it is up to date and
whether it is the right version for your Chrome browser.

It takes no arguments, simply run it like this:
./ensure-chromedriver.sh
"
fi

CHROME_MAJ_VER=$(/opt/google/chrome/chrome --version | grep -oP "[0-9]+(?=\.[0-9]+\.[0-9]+\.[0-9]+)")

echo "Chrome version: $CHROME_MAJ_VER"

CHROMEDRIVER_VERSION=""

if [ -e chromedriver ]; then
  CHROMEDRIVER_VERSION=$(./chromedriver --version | grep -oE "[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+")
  echo "Chromedriver version: $CHROMEDRIVER_VERSION"
fi

LATEST_CHROMEDRIVER_VER=$(curl -s https://chromedriver.storage.googleapis.com/LATEST_RELEASE_$CHROME_MAJ_VER)

echo "Latest chromedriver available for this Chrome: $LATEST_CHROMEDRIVER_VER"

if [ "$LATEST_CHROMEDRIVER_VER" != "$CHROMEDRIVER_VERSION" ]; then
  echo "Updating chromedriver"
  wget "https://chromedriver.storage.googleapis.com/$LATEST_CHROMEDRIVER_VER/chromedriver_linux64.zip"
  rm -f chromedriver
  unzip chromedriver_linux64.zip && rm chromedriver_linux64.zip
else
  echo "No update needed"
fi
