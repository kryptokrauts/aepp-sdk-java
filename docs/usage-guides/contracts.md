# Contracts

## Introduction
The smart contract language of the æternity blockchain is [Sophia](https://aeternity.com/aesophia). It is a functional language in the ML family, strongly typed and has restricted mutable state.

Before interacting with contracts using the SDK you should get familiar with Sophia itself first. Have a look into [aepp-sophia-examples](https://github.com/aeternity/aepp-sophia-examples) and start rapid prototyping using [AEstudio](https://studio.aepps.com).

The SDK needs to interact with following components in order to enable smart contract interactions on the æternity blockchain:

- [æternity](https://github.com/aeternity/aeternity) (host your own one or use the public testnet node at `https://testnet.aeternity.io`)
- [aesophia_http](https://github.com/aeternity/aesophia_http) (host your own one or use the public compiler at `https://compiler.aepps.com`)

Note:

- For production deployments you should ***always*** host these services by yourself.
- We highly recommend the usage of the [contraect-maven-plugin](#contraect-maven-plugin) for smart contract interaction.

## Contract interaction

For the example interaction following contract is being used:

```sophia
@compiler >= 6

include "String.aes"

contract ChatBot =

    datatype event = Greeting(string)

    entrypoint greet(name: string) : string =
        Chain.event(Greeting(name))
        String.concat("Hello, ", name)
```

### Compile a contract

```java
// TODO simple example for compilation
```

### Deploy a contract

```java
// TODO: Simple example for a deployment.
```

### Call a contract entrypoint

```java
// TODO: Simple example for a contract call.
```

## Additional topics

### Gas estimation

```java
// TODO: Explanation & simple example for usage of dry-run
```

### Contract events

```java
// TODO: Provide example how to handle events
```

## Plugins

### contraect-maven-plugin
To provide an easy way to interact with smart contracts on the æternity blockchain we developed a plugin that uses the source code of contracts written in Sophia as input to generate Java classes. The generated classes make use of the aepp-sdk-java and provide methods to deploy contracts and call the respective entrypoint functions.

**Links**:

- https://github.com/kryptokrauts/contraect-maven-plugin
- https://github.com/kryptokrauts/contraect-showcase-maven