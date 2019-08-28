package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.ChannelDepositTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ChannelDepositTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ChannelDepositTransactionModel extends AbstractTransactionModel<ChannelDepositTx> {

  @NonNull private String channelId;
  @NonNull private String fromId;
  @NonNull private BigInteger amount;
  @NonNull private BigInteger ttl;
  @NonNull private String stateHash;
  @NonNull private BigInteger round;
  @NonNull private BigInteger nonce;

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
  public void validateInput() {}

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
    return ChannelDepositTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
