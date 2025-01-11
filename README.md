# Validate PDF Accessibility Issues

A Java CLI application to validate and report accessibility issues in PDF documents.

## Setup

Download PDFix SDK for Java `net.pdfix.pdfixlib-<version>.jar` from https://pdfix.net/download and copy to `lib/`

## Compile App

Install the PDFix SDK 
```
mvn install:install-file -Dfile=lib/net.pdfix.pdfixlib-8.4.3.jar -DgroupId=net.pdfix -DartifactId=net.pdfix.pdfixlib -Dversion=8.4.3 -Dpackaging=jar
```

```
mvn compile -f pom.xml
mvn package -f pom.xml
```

## Run the sample

```
java -jar target/net.pdfix.validate-pdf-1.0.0 -i "<path to pdf>"
```
- `-i` - path to PDF document to process
- `-d` - directory with PDF documents to process
  
### Output

When duplicate entry was found:
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

## Have a question? Need help?
Let us know and we’ll get back to you. Write us to support@pdfix.net.