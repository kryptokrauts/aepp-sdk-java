package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.ChannelDepositTx;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ChannelDepositTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
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
  public Function<GenericTx, ChannelDepositTransactionModel> getApiToModelFunction() {
    return (tx) -> {
      ChannelDepositTx castedTx = (ChannelDepositTx) tx;
      return this.toBuilder()
          .channelId(castedTx.getChannelId())
          .fromId(castedTx.getFromId())
          .amount(castedTx.getAmount())
          .stateHash(castedTx.getStateHash())
          .round(castedTx.getRound())
          .fee(castedTx.getFee())
          .ttl(castedTx.getTtl())
          .nonce(castedTx.getNonce())
          .build();
    };
  }

  @Override
  public void validateInput() {}

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
    return ChannelDepositTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
