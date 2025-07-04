# PDF Accessibility Validator using PDFix

A Java CLI tool for validating the compliance of PDF documents with accessibility standards such as WCAG, or PDF/UA.

## Introduction

This tool validates PDF accessibility by checking for issues in tagged PDF documents. It leverages the PDFix SDK (free version) for reading and processing PDF files. Ensuring that PDFs are accessible is crucial for users with disabilities, and this tool helps identify and report potential issues.

## Table of Contents

- [PDF Accessibility Validator using PDFix](#pdf-accessibility-validator-using-pdfix)
  - [Introduction](#introduction)
  - [Table of Contents](#table-of-contents)
  - [Command-Line Options](#command-line-options)
  - [Run the CLI Commands](#run-the-cli-commands)
    - [Report Duplicate MCID in Tagged PDF](#report-duplicate-mcid-in-tagged-pdf)
  - [Installation into PDFix Desktop](#installation-into-pdfix-desktop)
  - [Build Instructions](#build-instructions)
    - [1. Download and Install PDFix SDK for Java](#1-download-and-install-pdfix-sdk-for-java)
    - [2. Compile, Test and Package](#2-compile-test-and-package)
  - [License](#license)
  - [Have a question? Need help?](#have-a-question-need-help)


## Command-Line Options

```yml
Usage:
  java -jar validate-pdf-{version}.jar [operation] [arguments]

Operations:
  duplicate-mcid    : Check for and report duplicate MCID (Marked Content Identifier) entries in a tagged PDF.

Arguments:
  -i <file>         : Path to a single PDF file to validate.
  -d <folder>       : Path to a directory of PDF files to validate (will process all PDFs in the folder).
```

## Run the CLI Commands

### Report Duplicate MCID in Tagged PDF

This command validates a PDF file for duplicate MCID entries, which can cause accessibility issues for screen readers.

```bash
java -jar target/net.pdfix.validate-pdf-{version}.jar duplicate-mcid -i "path/to/your/file.pdf"
```

**Output**
```yaml
===============================================================================
File: path/to/your/file.pdf
Duplicate MCID Found:
  MCID      : 9
  Page      : 26
  Index     : 32
  Type      : text
  BBox      : [240.12, 455.95, 297.72, 457.32]
  Content   : Friday

Total 1 duplicate MCID(s) found  
===============================================================================
```

## Installation into PDFix Desktop

PDFix Desktop supports the integration of external actions into its user interface. Follow these steps to install the Validate Duplicate MCID action:

1. **Download** the net.pdfix.validate-pdf ZIP file from the [Releases page](https://github.com/pdfix/action-validate-pdf-pdfix-java/releases/latest)
2. **Extract** the ZIP into a folder on your computer (e.g. ~/Desktop/net.pdfix.validate-pdf)
3. In PDFix Desktop 
   1. Open **Actions > Manage Actions** from the main menu of the application window
   2. Click button **External Actions** in the toolbar
   3. In the top-left dropdown select **Local** as the action source and **choose the extracted folder** in the editable widget.
   4. Click **Add Action** and the action will appear in the External Action list.
4. The action can be accessed via menu **Actions > Validation > Validate Duplicate MCID**.


## Build Instructions

### 1. Download and Install PDFix SDK for Java

Before building the project, you need to download and install the PDFix SDK. This SDK is used to read and process PDF files for accessibility validation. Run the following commands:

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

## License

This project is licensed under the [PDFix Free License](https://pdfix.net/terms/).

## Have a question? Need help?

If you have any questions or need assistance, feel free to reach out to us via email at [support@pdfix.net](mailto:support@pdfix.net).
