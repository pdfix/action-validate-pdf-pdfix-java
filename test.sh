#!/bin/bash

# This is local jar test during build and push action.

# Colors for output into console
GREEN='\033[0;32m'
RED='\033[0;31m'
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

# Function to print info messages
info() { echo -e "${PURPLE}$1${NC}"; }

# Function to print success messages
success() { echo -e "${GREEN}$1${NC}"; }

# Function to print error messages
error() { echo -e "${RED}ERROR: $1${NC}"; }

# init
pushd "$(dirname $0)" > /dev/null

EXIT_STATUS=0

info "List files in target directory"
ls -l $(pwd)/target

info "Test #01: Show help"
java -jar target/net.pdfix.validate-pdf-*.jar --help > /dev/null
if [ $? -eq 0 ]; then
    success "passed"
else
    error "Failed to run \"--help\" command"
    EXIT_STATUS=1
fi

info "Test #02: Run validate MCID"
OUTPUT=$(java -jar target/net.pdfix.validate-pdf-*.jar duplicate-mcid -i resources/test.pdf)
COUNT=$(echo "$OUTPUT" | grep -c "Duplicate MCID Found")
info "$OUTPUT"
if [ "$COUNT" -eq 5 ]; then
    success "passed"
else
    error "Validate MCID failed on resources/test.pdf. Expected 5 duplicate MCIDs, found $COUNT."
    EXIT_STATUS=1
fi

popd > /dev/null

if [ $EXIT_STATUS -eq 1 ]; then
    error "One or more tests failed."
    exit 1
else
    success "All tests passed."
    exit 0
fi
