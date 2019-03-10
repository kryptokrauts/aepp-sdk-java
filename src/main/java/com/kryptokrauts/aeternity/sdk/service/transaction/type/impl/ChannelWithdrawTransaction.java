package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ChannelApi;
import com.kryptokrauts.aeternity.generated.model.ChannelWithdrawTx;
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
public class ChannelWithdrawTransaction extends AbstractTransaction<ChannelWithdrawTx> {

  @NonNull String channelId;
  @NonNull String toId;
  @NonNull BigInteger amount;
  @NonNull BigInteger ttl;
  @NonNull String stateHash;
  @NonNull BigInteger round;
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
    return channelApi.rxPostChannelWithdraw(toModel());
  }

  @Override
  protected ChannelWithdrawTx toModel() {
    ChannelWithdrawTx channelWithdrawTx = new ChannelWithdrawTx();
    channelWithdrawTx.setChannelId(channelId);
    channelWithdrawTx.setToId(toId);
    channelWithdrawTx.setAmount(amount);
    channelWithdrawTx.setFee(fee);
    channelWithdrawTx.setTtl(ttl);
    channelWithdrawTx.setStateHash(stateHash);
    channelWithdrawTx.setRound(round);
    channelWithdrawTx.setNonce(nonce);
    return channelWithdrawTx;
  }
}
