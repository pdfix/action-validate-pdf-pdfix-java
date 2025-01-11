# Validate PDF Accessibility with PDFix

A Java CLI application to validate and report accessibility issues in PDF documents. We use free version of PDFix SDK for PDF reading operations.

## Table of Contents
- [Validate PDF Accessibility with PDFix](#validate-pdf-accessibility-with-pdfix)
  - [Table of Contents](#table-of-contents)
  - [Command-Line options](#command-line-options)
  - [Run the CLI Commands](#run-the-cli-commands)
    - [Report Duplicate MCID in Tagged PDF](#report-duplicate-mcid-in-tagged-pdf)
  - [Build Instructions](#build-instructions)
    - [1. Download and Install PDFix SDK for Java](#1-download-and-install-pdfix-sdk-for-java)
    - [2. Compile, Test and Package](#2-compile-test-and-package)
  - [Have a question? Need help?](#have-a-question-need-help)


## Command-Line options
```
Usage:
  java -jar validate-pdf-1.0.0.jar [operation] [arguments]

Operations:
  duplicate-mcid    : Validate and report duplicate MCID entries in a tagged content

Arguments:
  -i <file>         : Path to a PDF file to process
  -d <folder>       : Path to a directory to process
```

## Run the CLI Commands

### Report Duplicate MCID in Tagged PDF
```bash
java -jar target/net.pdfix.validate-pdf-1.0.0 duplicate-mcid -i "<path to pdf>"
```
**Output**
```
===============================================================================
File: /Users/administrator/Downloads/example.pdf
Duplicate MCID Found:
  MCID      : 9
  Page      : 26
  Index     : 32
  Type      : text
  BBox      : [240.12, 455.95, 297.72, 457.32]
  Content: Freitag

Total 1 duplicate MCIDs found  
===============================================================================
```

## Build Instructions

### 1. Download and Install PDFix SDK for Java
```bash
mkdir -p lib
curl -L https://github.com/pdfix/pdfix_sdk_builds/releases/download/v8.4.3/java8-net.pdfix.pdfixlib-8.4.3.jar.zip -o lib/pdfixlib-8.4.3.jar.zip
unzip lib/pdfixlib-8.4.3.jar.zip -d lib/
mvn install:install-file -Dfile=lib/net.pdfix.pdfixlib-8.4.3.jar -DgroupId=net.pdfix -DartifactId=net.pdfix.pdfixlib -Dversion=8.4.3 -Dpackaging=jar
```

### 2. Compile, Test and Package
```bash
mvn compile
mvn test
mvn package
```



## Have a question? Need help?
Let us know and we’ll get back to you. Write us to support@pdfix.net.
