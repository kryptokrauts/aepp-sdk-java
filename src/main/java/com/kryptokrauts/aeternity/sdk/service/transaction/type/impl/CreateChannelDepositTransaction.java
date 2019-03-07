package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ChannelApi;
import com.kryptokrauts.aeternity.generated.model.ChannelDepositTx;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.FeeCalculationModel;
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
public class CreateChannelDepositTransaction extends AbstractTransaction<ChannelDepositTx> {

  @NonNull String channelId;
  @NonNull String fromId;
  @NonNull BigInteger amount;
  @NonNull BigInteger ttl;
  @NonNull String stateHash;
  @NonNull BigInteger round;
  @NonNull BigInteger nonce;
  @NonNull private FeeCalculationModel feeCalculationModel;
  @NonNull private ChannelApi channelApi;

  @Override
  protected Single<UnsignedTx> createInternal() {
    return channelApi.rxPostChannelDeposit(toModel());
  }

  @Override
  protected ChannelDepositTx toModel() {
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
  protected Bytes createRLPEncodedList() {
    Bytes encodedRlp = RLP.encodeList(rlpWriter -> {});

    throw new UnsupportedOperationException();
    //		return encodedRlp;
  }

  @Override
  protected FeeCalculationModel getFeeCalculationModel() {
    return feeCalculationModel;
  }
}
