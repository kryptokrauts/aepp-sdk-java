package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ChannelApi;
import com.kryptokrauts.aeternity.generated.model.ChannelSlashTx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import io.reactivex.Single;
import java.math.BigInteger;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;
import net.consensys.cava.bytes.Bytes;
import net.consensys.cava.rlp.RLP;

@Getter
@SuperBuilder
public class ChannelSlashTransaction extends AbstractTransaction<ChannelSlashTx> {

  @NonNull String channelId;
  @NonNull String fromId;
  @NonNull String payload;
  @NonNull String poi;
  @NonNull BigInteger ttl;
  @NonNull BigInteger nonce;
  @NonNull private ChannelApi channelApi;

  @Override
  protected Bytes createRLPEncodedList() {
    // TODO
    Bytes encodedRlp = RLP.encodeList(rlpWriter -> {});
    throw new UnsupportedOperationException();
  }

  @Override
  protected Single<UnsignedTx> createInternal() {
    return channelApi.rxPostChannelSlash(toModel());
  }

  @Override
  protected ChannelSlashTx toModel() {
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
}
