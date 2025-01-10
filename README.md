# PDFix Report Duplicate MCID in tagged PDF
Command-line application to report duplicate MCID entries in tagged PDF written in Java.

## Setup

Download PDFix SDK for Java `net.pdfix.pdfixlib-<version>.jar` and copy to `lib/`

All resources are available on https://pdfix.net/download.

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
java -jar target/net.pdfix.FindDuplicateMcid-1.0.0 -i "<path to pdf>"
```
- `-i` - path to PDF document to process
  
### Output

When duplicate entry was found:
```
Duplicate MCID: '6'
Page: 1, Index: 2, Type: text
BBox: [413.4234619140625, 797.4384155273438 439.1921081542969 804.83447265625]
Text: Freitag
```

## Have a question? Need help?
Let us know and we’ll get back to you. Write us to support@pdfix.net.