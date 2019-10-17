package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.ChannelCloseMutualTx;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ChannelCloseMutualTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class ChannelCloseMutualTransactionModel
    extends AbstractTransactionModel<ChannelCloseMutualTx> {

  private String channelId;

  private String fromId;

  private BigInteger initiatorAmountFinal;

  private BigInteger responderAmountFinal;

  private BigInteger ttl;

  private BigInteger nonce;

  @Override
  public ChannelCloseMutualTx toApiModel() {
    ChannelCloseMutualTx channelCloseMutualTx = new ChannelCloseMutualTx();
    channelCloseMutualTx.setChannelId(channelId);
    channelCloseMutualTx.setFromId(fromId);
    channelCloseMutualTx.setInitiatorAmountFinal(initiatorAmountFinal);
    channelCloseMutualTx.setResponderAmountFinal(responderAmountFinal);
    channelCloseMutualTx.setFee(fee);
    channelCloseMutualTx.setTtl(ttl);
    channelCloseMutualTx.setNonce(nonce);
    return channelCloseMutualTx;
  }

  @Override
  public Function<GenericTx, ChannelCloseMutualTransactionModel> getApiToModelFunction() {
    return (tx) -> {
      ChannelCloseMutualTx castedTx = (ChannelCloseMutualTx) tx;
      return this.toBuilder()
          .channelId(castedTx.getChannelId())
          .fromId(castedTx.getFromId())
          .initiatorAmountFinal(castedTx.getInitiatorAmountFinal())
          .responderAmountFinal(castedTx.getResponderAmountFinal())
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
    return ChannelCloseMutualTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
