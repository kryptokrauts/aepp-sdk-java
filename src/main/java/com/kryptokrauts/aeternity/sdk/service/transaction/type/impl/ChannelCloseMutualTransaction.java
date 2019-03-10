package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ChannelApi;
import com.kryptokrauts.aeternity.generated.model.ChannelCloseMutualTx;
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
public class ChannelCloseMutualTransaction extends AbstractTransaction<ChannelCloseMutualTx> {

  @NonNull String channelId;
  @NonNull String fromId;
  @NonNull BigInteger initiatorAmountFinal;
  @NonNull BigInteger responderAmountFinal;
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
    return channelApi.rxPostChannelCloseMutual(toModel());
  }

  @Override
  protected ChannelCloseMutualTx toModel() {
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
}
