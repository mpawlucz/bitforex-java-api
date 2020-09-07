# Bitforex Java API
bitforex-java-api is a lightweight Java library for interacting with the [Bitforex API](https://github.com/githubdev2020/API_Doc_en/wiki)

## Features
- [x] getBalance
- [x] createOrder
- [x] getOpenOrders
- [x] cancelOrder

## Installation
1. Install library into your Maven's local repository by running `mvn install`
2. Add the following Maven dependency to your project's `pom.xml`:
```
<dependency>
    <groupId>com.github.mpawlucz.bitforex</groupId>
    <artifactId>bitforex-java-api</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Example

### getBalance
```
BitforexRestApi api = new BitforexRestApi(ApiKey.builder()
    .key("<YOUR-API-KEY>")
    .secret("<YOUR-API-SECRET>")
.build());

final BalanceResponse balance = api.getBalance();
    if (balance.getSuccess()){
        for (BalanceEntry balanceEntry : balance.getData()) {
            if (balanceEntry.getTotal().compareTo(BigDecimal.ZERO) > 0){
                System.out.println(balanceEntry.getCurrency() + ": " + balanceEntry.getTotal());
            }
        }
    } else {
    System.out.println("error: " + balance.getMessage());
}
```

### createOrder
```
final TradeResponse trade = api.trade(TradeRequest.builder()
    .base("ETH")
    .quote("BTC")
    .volume(new BigDecimal("0.001"))
    .price(new BigDecimal("0.01"))
    .isBuy(true)
.build());
if (!trade.isSuccess()){
    throw new RuntimeException("Create order failed: " + trade.getMessage());
}
System.out.println(trade.getData().getOrderId());
```
