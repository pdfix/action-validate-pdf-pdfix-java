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

function validateFile {
    FILE=$1
    COUNT=$2
    # EXIT_STATUS=0

    # Run Java program and capture outputs
    stdout=$(mktemp)   # temporary file for stdout
    stderr=$(mktemp)   # temporary file for stderr    

    # Run the program, redirecting streams
    java -jar target/net.pdfix.validate-pdf-*.jar duplicate-mcid -i "$FILE" >"$stdout" 2>"$stderr"
    exit_code=$?

    err=$(cat "$stderr")
    out=$(cat "$stdout")
    # echo "err: $err"
    # echo "out: $out"

    if [ "$exit_code" -gt 100 ]; then
        error "Validate MCID failed with exception $exit_code."
        EXIT_STATUS=1
    else
        if [ "$COUNT" -eq $exit_code ]; then
            success "passed: $FILE"
        else
            error "Validate MCID failed on $FILE. Expected $COUNT duplicate MCIDs, found $exit_code."
            EXIT_STATUS=1
        fi
    fi

    # Clean up temporary files
    rm "$stdout" "$stderr"
    return $EXIT_STATUS
}

validateFile "resources/test.pdf" 1
validateFile "resources/test1.pdf" 40

popd > /dev/null

if [ $EXIT_STATUS -eq 1 ]; then
    error "One or more tests failed."
    exit 1
else
    success "All tests passed."
    exit 0
fi
