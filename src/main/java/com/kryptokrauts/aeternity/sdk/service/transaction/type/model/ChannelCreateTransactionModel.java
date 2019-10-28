package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.ChannelCreateTx;
import com.kryptokrauts.aeternity.generated.model.GenericTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ChannelCreateTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import java.util.function.Function;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class ChannelCreateTransactionModel extends AbstractTransactionModel<ChannelCreateTx> {

  private String initiator;

  private BigInteger initiatorAmount;

  private String responder;

  private BigInteger responderAmount;

  private BigInteger channelReserve;

  private BigInteger lockPeriod;

  private BigInteger ttl;

  private String stateHash;

  private BigInteger nonce;

  @Override
  public ChannelCreateTx toApiModel() {
    ChannelCreateTx channelCreateTx = new ChannelCreateTx();
    channelCreateTx.setInitiatorId(initiator);
    channelCreateTx.setInitiatorAmount(initiatorAmount);
    channelCreateTx.setResponderId(responder);
    channelCreateTx.setResponderAmount(responderAmount);
    channelCreateTx.setChannelReserve(channelReserve);
    channelCreateTx.setLockPeriod(lockPeriod);
    channelCreateTx.setFee(fee);
    channelCreateTx.setTtl(ttl);
    channelCreateTx.setStateHash(stateHash);
    channelCreateTx.setNonce(nonce);
    return channelCreateTx;
  }

  @Override
  public Function<GenericTx, ChannelCreateTransactionModel> getApiToModelFunction() {
    return (tx) -> {
      ChannelCreateTx castedTx = (ChannelCreateTx) tx;
      return this.toBuilder()
          .initiator(castedTx.getInitiatorId())
          .initiatorAmount(castedTx.getInitiatorAmount())
          .responder(castedTx.getResponderId())
          .responderAmount(castedTx.getResponderAmount())
          .channelReserve(castedTx.getChannelReserve())
          .lockPeriod(castedTx.getLockPeriod())
          .stateHash(castedTx.getStateHash())
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
    return ChannelCreateTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
