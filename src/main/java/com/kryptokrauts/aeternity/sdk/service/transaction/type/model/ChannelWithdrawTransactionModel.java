package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.api.rxjava.InternalApi;
import com.kryptokrauts.aeternity.generated.model.ChannelWithdrawTx;
import com.kryptokrauts.aeternity.generated.model.Tx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ChannelWithdrawTransaction;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class ChannelWithdrawTransactionModel extends AbstractTransactionModel<ChannelWithdrawTx> {

  private String channelId;
  private String toId;
  private BigInteger amount;
  private BigInteger ttl;
  private String stateHash;
  private BigInteger round;
  private BigInteger nonce;

  @Override
  public ChannelWithdrawTx toApiModel() {
    ChannelWithdrawTx channelWithdrawTx = new ChannelWithdrawTx();
    channelWithdrawTx.setChannelId(channelId);
    channelWithdrawTx.setToId(toId);
    channelWithdrawTx.setAmount(amount);
    channelWithdrawTx.setFee(fee);
    channelWithdrawTx.setTtl(ttl);
    channelWithdrawTx.setStateHash(stateHash);
    channelWithdrawTx.setRound(round);
    channelWithdrawTx.setNonce(nonce);
    return channelWithdrawTx;
  }

  @Override
  public Function<Tx, ChannelWithdrawTransactionModel> getApiToModelFunction() {
    return (tx) ->
        this.toBuilder()
            .channelId(tx.getChannelId())
            .toId(tx.getToId())
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
    return ChannelWithdrawTransaction.builder()
        .externalApi(externalApi)
        .internalApi(internalApi)
        .model(this)
        .build();
  }
}
