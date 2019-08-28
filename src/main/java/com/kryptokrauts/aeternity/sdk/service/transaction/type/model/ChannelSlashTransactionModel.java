package com.kryptokrauts.aeternity.sdk.service.transaction.type.model;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.ChannelSlashTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.impl.ChannelSlashTransaction;
import com.kryptokrauts.sophia.compiler.generated.api.rxjava.DefaultApi;
import java.math.BigInteger;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ChannelSlashTransactionModel extends AbstractTransactionModel<ChannelSlashTx> {

  @NonNull private String channelId;
  @NonNull private String fromId;
  @NonNull private String payload;
  @NonNull private String poi;
  @NonNull private BigInteger ttl;
  @NonNull private BigInteger nonce;

  @Override
  public ChannelSlashTx toApiModel() {
    ChannelSlashTx channelSlashTx = new ChannelSlashTx();
    channelSlashTx.setChannelId(channelId);
    channelSlashTx.setFromId(fromId);
    channelSlashTx.setPayload(payload);
    channelSlashTx.setPoi(poi);
    channelSlashTx.setFee(fee);
    channelSlashTx.setTtl(ttl);
    channelSlashTx.setNonce(nonce);
    return channelSlashTx;
  }

  @Override
  public void validateInput() {}

  @Override
  public AbstractTransaction<?> buildTransaction(ExternalApi externalApi, DefaultApi compilerApi) {
    return ChannelSlashTransaction.builder().externalApi(externalApi).model(this).build();
  }
}
