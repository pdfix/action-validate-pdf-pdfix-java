#!/bin/bash

# This script is used to update the version number in the config.json file
# during the maven build process in GitHub Actions.
# It replace "0.0.0" in program arguments with current version of container.
# It also replaces the version number in the config.json file.

# Check if an argument is provided
if [ -z "$1" ]; then
    echo "No argument provided. Usage: ./update_version.sh v1.2.3"
    exit 1
fi

# Check if the input is "latest"
if [ "$1" == "latest" ]; then
    echo "Input is 'latest'. No changes made."
    exit 0
fi

version=$1
version_short="${1#v}"

echo "Version: ${version}"
echo "Short version: ${version_short}"


# pom.xml
# Check if pom.xml exists
if [ ! -f "pom.xml" ]; then
    echo "File pom.xml does not exist."
    exit 1
fi

# Update the version in pom.xml
mvn versions:set -DnewVersion=${version_short}

echo "Set version in pom.xml"


# config.json
# Check if config.json exists
if [ ! -f "config.json" ]; then
    echo "File config.json does not exist."
    exit 1
fi

# Replace "v0.0.0" placeholder with the provided argument in config.json
sed -i.bak "s|v0\.0\.0|${version}|g" config.json && rm config.json.bak

echo "Replaced all occurrences of 'v0.0.0' with '${version}' in config.json."

# Replace "0.0.0" placeholder with the provided argument in config.json
sed -i.bak "s|0\.0\.0|${version_short}|g" config.json && rm config.json.bak

echo "Replaced all occurrences of '0.0.0' with '${version_short}' in config.json."
