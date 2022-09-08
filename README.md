# REST demo project for XMCY.

## Description
This Java artifact is demo REST project which recommends cryptocurrency based on provided content.

## How to install
* Change `path.to.csv.dir` at `application.properties` to a correct path to CSV files.
* Build: run command `mvn clean install package` in root folder of artifact.
* Running locally: run `java -jar xmcy-0.0.1-SNAPSHOT.jar` in target folder of artifact.
* API: Once running locally, to access API, go to http://localhost:8080/swagger-ui/index.html for swagger access or call endpoints manually.

## Endpoints
* Available endpoints:
  1. GET `/cryptocurrencies` - lists all cryptocurrencies sorted by normalized range.
  2. GET `/cryptocurrencies/{crypto}` - returns cryptocurrency info, where {crypto} - cryptocurrency short name.
  3. GET `/cryptocurrencies/recommendation?fromDate=dd-MM-yyyy` - returns cryptocurrency recommendation based on normalized range, where dd-MM-yyyy - date format.
  4. GET `/cryptocurrencies/refresh` - refreshes all cryptocurrencies statistics and saves into files.

## Business Specifications
* When artifact is starting job reads all data from CSV files, calculates and organized it, then saves to specific files.
* Then artifact don't interact with CSV files until refresh endpoint is called.
* Calculated data saved as JSON in file and deserializes each time endpoints is called.

## Technical Specifications
* Java 11
* Spring Framework
* Testing dependencies : testng, mockito
