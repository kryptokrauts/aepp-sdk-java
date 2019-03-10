package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ChannelApi;
import com.kryptokrauts.aeternity.generated.model.ChannelSettleTx;
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
public class ChannelSettleTransaction extends AbstractTransaction<ChannelSettleTx> {

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
    return channelApi.rxPostChannelSettle(toModel());
  }

  @Override
  protected ChannelSettleTx toModel() {
    ChannelSettleTx channelSettleTx = new ChannelSettleTx();
    channelSettleTx.setChannelId(channelId);
    channelSettleTx.setFromId(fromId);
    channelSettleTx.setInitiatorAmountFinal(initiatorAmountFinal);
    channelSettleTx.setResponderAmountFinal(responderAmountFinal);
    channelSettleTx.setFee(fee);
    channelSettleTx.setTtl(ttl);
    channelSettleTx.setNonce(nonce);
    return channelSettleTx;
  }
}
