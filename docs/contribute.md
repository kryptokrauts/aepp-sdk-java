# How to contribute?

## Get started

### Prerequisites

- Docker
- Gradle
- [Lombok-Plugin](https://projectlombok.org)

The SDK heavily makes use of the lombok IDE plugin to hide glue code, especially getters, setters, builders and helper methods and helps to keep the classes clean and focus on the logic.
The plugin is needed in your IDE of choice to automatically create the necessary methods in the background.

- [System requirements](index.md#system-requirements)

### Development environment

To run integration tests you need to run the preconfigured docker setup by executing:

- `docker-compose up -d`

You should see the following instances up and running:

| service | description | endpoints |
| --- | --- | --- |
| compiler | Sophia http compiler that is required to compile contracts and to encode/decode calldata | http://localhost:8080 |
| node | æternity node including the middleware plugin | http://localhost:3013 (node external api), http://localhost:3113 (node debug api), http://localhost:3014 (node websocket), http://localhost:4000 (middleware api), http://localhost:4001 (middleware websocket) |
| proxy | nginx proxy to access specific APIs without the need to provide a port | http://localhost (node external & internal api), http://compiler.localhost (compiler), http://mdw.localhost (middleware) |

### Generate API clients

The SDK requires API clients (Compiler, Node & Middelware) that can be generated by executing following Gradle task:

- `gradle generateApiClients`

## Testing
The SDK ships with two kinds of tests:

- **unit tests**, which run independently of a running node
- **integration tests**, which require a running aeternity node

In order to run the integration tests you need to configure the following environment variables:

```sh
AETERNITY_BASE_URL  = http://localhost
COMPILER_BASE_URL   = http://localhost:3080
MDW_BASE_URL        = http://localhost:4000
``` 

The tests can be run within your IDE of choice by using the following Gradle tasks:

```sh
gradle test
gradle integrationTest
```

Integration tests should extend the `BaseTest` class which  provides some convenient methods for running tests using the underlying vertx framework.
Test methods can thus make use of the `executeTest` method as follows:

```
@Test
public void testMyLogic(TestContext context) {
this.executeTest(
        context,
        t -> {
          ...
          context.assertEquals("test", "test");
          ...
        });
}
```

## Code format
Before committing code you should make sure the code follows the Google Java Style. This can be done by executing:

- `gradle googleJavaFormat`

## Pull requests
Please provide a pull request to the latest development branch, e.g. `3.x`. We will review and merge or request some changes if neccesary. Make sure that the PR passes all the checks!