package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.ChannelCloseMutualTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ChannelCloseMutualTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ChannelCloseMutualTransactionModel
    extends AbstractTransactionModel<ChannelCloseMutualTx> {

  @NonNull private String channelId;
  @NonNull private String fromId;
  @NonNull private BigInteger initiatorAmountFinal;
  @NonNull private BigInteger responderAmountFinal;
  @NonNull private BigInteger ttl;
  @NonNull private BigInteger nonce;

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
  public void validateInput() {}

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
    return ChannelCloseMutualTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
