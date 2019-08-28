package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.ChannelCreateTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ChannelCreateTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ChannelCreateTransactionModel extends AbstractTransactionModel<ChannelCreateTx> {
  @NonNull private String initiator;
  @NonNull private BigInteger initiatorAmount;
  @NonNull private String responder;
  @NonNull private BigInteger responderAmount;
  @NonNull private BigInteger channelReserve;
  @NonNull private BigInteger lockPeriod;
  @NonNull private BigInteger ttl;
  @NonNull private String stateHash;
  @NonNull private BigInteger nonce;

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
  public void validateInput() {}

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
    return ChannelCreateTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
