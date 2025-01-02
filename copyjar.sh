#!/usr/bin/env bash

VERSION="$1"

# Ensure the target directory exists
mkdir -p build/libs

# Copy and rename the jar, using $VERSION in the final filename
cp build/libs/*-all.jar "build/libs/AnnouncerPlus-OG.jar"

# Remove the jars we don't want
rm -rf build/libs/AnnouncerPlus-OG-*
