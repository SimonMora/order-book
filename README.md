# order-book
Springboot program that builds and maintains real-time order book for two
trading pairs - BTC/USDT and ETH/USDT using the Binance API. Every 10 seconds, your
program prints the both orderbooks and the total volume change in USDT for the corresponding
trading pair.

# Prerequisites

**JDK 17**: Make sure you have JDK 17 installed on your machine. Also, JAVA_HOME env
variable must be appropriately set and pointing to the JDK 17.
**Gradle**: Ensure Gradle v7.3+ is installed and properly configured. Pay special attention
to the above-mentioned JAVA_HOME env variable.
**Git**: Use it to get the project locally

# Clone the repository:

`git clone https://github.com/SimonMora/order-book`
`cd order-book`

# Build the project using Gradle:

Run the following command: `./gradlew build`

# Running the Project

To run the project, use the following command in the terminal:
`./gradlew bootRun`

# Project Structure

#### src/main/java:
Contains the following packages:
*     commons: contains constants and utility methods.
*     config: contains WebSocket configuration classes.
*     entity: contains classes to parse the received records from Binance API.
*     event: contains events and event handler.
*     repository: contains classes to storage in memory the local order book.
*     scheduler: contains classes to start the processing, one for each trading pair.
*     service: contains the interfaces and implementations to manage the logic to print order books.

#### src/main/resources:
    Contains application.properties and no other resources.

#### src/test/java: 
    Pending implementation

# Dependencies
The project depends on simply in 3 dependencies listed as follows:

    //Web Socket configurations
    'org.java-websocket:Java-WebSocket:1.5.6'
    
    //Rest configurations
    'org.springframework.boot:spring-boot-starter-web'
    
    //Json parsing configurations
    'com.google.code.gson:gson:2.10.1'

Also contains a test dependency which is a springboot requirement:

    'org.springframework.boot:spring-boot-starter-test'

# Configuration
The following properties are located in the application.properties file:
    
    spring.application.name=orderbook
    spring.task.scheduling.pool.size=10
    
    binance.ws.uri=wss://stream.binance.com:9443/ws/#symbol@depth
    binance.depth.snapshot.uri=https://api.binance.com/api/v3/depth?symbol=#symbol&limit=50
