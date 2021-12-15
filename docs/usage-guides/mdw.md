# Middleware
The [aeternity middleware](https://github.com/aeternity/ae_mdw) extends the node and exposes their own websocket and http api.
Certain information such as AENS auction data cannot be directly fetched from the node. Thus you will have to communicate with the mdw to get access to this data.

At the moment the [MiddlewareService](https://github.com/kryptokrauts/aepp-sdk-java/blob/master/src/main/java/com/kryptokrauts/aeternity/sdk/service/mdw/MiddlewareService.java)
only provides functions to get AENS auction relevant data.

If you need convenient access to any other specific API endpoint feel free to [open an issue](https://github.com/kryptokrauts/aepp-sdk-java/issues/new).

## AENS
### Get a specific auction

```java
// TODO
```

### Get all auctions

```java
// TODO
```