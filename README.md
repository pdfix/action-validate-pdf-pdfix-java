# PDF Accessibility Validator using PDFix

A Java CLI tool for validating the compliance of PDF documents with accessibility standards such as WCAG, or PDF/UA.

## Introduction

This tool validates PDF accessibility by checking for issues in tagged PDF documents. It leverages the PDFix SDK (free version) for reading and processing PDF files. Ensuring that PDFs are accessible is crucial for users with disabilities, and this tool helps identify and report potential issues.

## Table of Contents

- [PDF Accessibility Validator using PDFix](#pdf-accessibility-validator-using-pdfix)
  - [Introduction](#introduction)
  - [Getting started](#getting-started)
  - [Usage](#usage)
  - [Commands](#commands)
  - [Arguments](#arguments)
  - [Return codes](#return-codes)
  - [Examples](#examples)
  - [PDFix Desktop integration](#pdfix-desktop-integration)
  - [Build](#build)
  - [Help & support](#help--support)
  - [Licenses](#licenses)

## Getting started

You need **Java 8 or newer** installed (the project targets Java 1.8 via Maven compiler settings).

## Usage

```bash
java -jar net.pdfix.validate-pdf-{version}.jar <command> [options]
```

## Commands

- `duplicate-mcid`: Check for and report duplicate MCID (Marked Content Identifier) entries in a tagged PDF.

## Arguments

Provide either `--input` or `--directory`.

| Option | Required | Type / expected value | Description |
|---|:---:|---|---|
| `-i`, `--input` | no* | Path to an existing `.pdf` file | Path to a single PDF file to validate |
| `-d`, `--directory` | no* | Path to an existing directory | Path to a directory of files to validate (non-PDFs are skipped) |

\* Exactly one of `--input` or `--directory` is expected.

## Return codes

- `0`: Success, no duplicate MCIDs found
- `1..100`: Success, duplicate MCIDs found (capped at 100)
- `101+`: Error (details printed to stderr)

## Examples

Report duplicate MCIDs in one PDF:

```bash
java -jar target/net.pdfix.validate-pdf-{version}.jar duplicate-mcid -i "path/to/your/file.pdf"
```

Report duplicate MCIDs in a folder:

```bash
java -jar target/net.pdfix.validate-pdf-{version}.jar duplicate-mcid -d "path/to/folder"
```

## PDFix Desktop integration

PDFix Desktop supports the integration of external actions into its user interface.

- Releases: `https://github.com/pdfix/action-validate-pdf-pdfix-java/releases/latest`

## Build

This project uses PDFix SDK for Java.

Download and install the PDFix SDK dependency into your local Maven repository:

```bash
mkdir -p lib
curl -L https://github.com/pdfix/pdfix_sdk_builds/releases/download/v8.4.3/java8-net.pdfix.pdfixlib-8.4.3.jar.zip -o lib/pdfixlib-8.4.3.jar.zip
unzip lib/pdfixlib-8.4.3.jar.zip -d lib/
mvn install:install-file -Dfile=lib/net.pdfix.pdfixlib-8.4.3.jar -DgroupId=net.pdfix -DartifactId=net.pdfix.pdfixlib -Dversion=8.4.3 -Dpackaging=jar
```

Compile, test, and package:

```bash
mvn compile
mvn test
mvn package
```

## Help & support

If you have any questions or need assistance, contact `support@pdfix.net`.

## Licenses

- [PDFix Terms](https://pdfix.net/terms)
