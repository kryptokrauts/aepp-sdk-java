package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.InternalApi;
import com.kryptokrauts.aeternity.generated.model.ChannelDepositTx;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ChannelDepositTransaction;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class ChannelDepositTransactionModel extends AbstractTransactionModel<ChannelDepositTx> {

  private String channelId;

  private String fromId;

  private BigInteger amount;

  private BigInteger ttl;

  private String stateHash;

  private BigInteger round;

  private BigInteger nonce;

  @Override
  public ChannelDepositTx toApiModel() {
    ChannelDepositTx channelDepositTx = new ChannelDepositTx();
    channelDepositTx.setChannelId(channelId);
    channelDepositTx.setFromId(fromId);
    channelDepositTx.setAmount(amount);
    channelDepositTx.fee(fee);
    channelDepositTx.setTtl(ttl);
    channelDepositTx.setStateHash(stateHash);
    channelDepositTx.setRound(round);
    channelDepositTx.setNonce(nonce);
    return channelDepositTx;
  }

  @Override
  public Function<Tx, ChannelDepositTransactionModel> getApiToModelFunction() {
    return (tx) ->
        this.toBuilder()
            .channelId(tx.getChannelId())
            .fromId(tx.getFromId())
            .amount(tx.getAmount())
            .stateHash(tx.getStateHash())
            .round(tx.getRound())
            .fee(tx.getFee())
            .ttl(tx.getTtl())
            .nonce(tx.getNonce())
            .build();
  }

  @Override
  public void validateInput() {}

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, InternalApi internalApi) {
    return ChannelDepositTransaction.builder()
        .externalApi(externalApi)
        .internalApi(internalApi)
        .model(this)
        .build();
  }
}
