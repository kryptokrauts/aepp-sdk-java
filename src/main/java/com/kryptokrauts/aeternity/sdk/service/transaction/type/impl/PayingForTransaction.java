package com.kryptokrauts.aeternity.sdk.service.transaction.type.impl;

import com.kryptokrauts.aeternity.generated.api.rxjava.ExternalApi;
import com.kryptokrauts.aeternity.generated.model.UnsignedTx;
import com.kryptokrauts.aeternity.sdk.constants.ApiIdentifiers;
import com.kryptokrauts.aeternity.sdk.constants.SerializationTags;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.FeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.fee.impl.PayingForFeeCalculationModel;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.AbstractTransaction;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.PayingForTransactionModel;
import com.kryptokrauts.aeternity.sdk.util.EncodingUtils;
import io.reactivex.Single;
import java.util.Arrays;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.tuweni.bytes.Bytes;
import org.apache.tuweni.rlp.RLP;

@SuperBuilder
@ToString
public class PayingForTransaction extends AbstractTransaction<PayingForTransactionModel> {

  @NonNull private ExternalApi externalApi;

  @Override
  public Bytes createRLPEncodedList() {
    Bytes encodedRlp =
        RLP.encodeList(
            rlpWriter -> {
              rlpWriter.writeInt(SerializationTags.OBJECT_TAG_PAYING_FOR_TRANSACTION);
              rlpWriter.writeInt(SerializationTags.VSN_1);
              byte[] payerIdWithTag =
                  EncodingUtils.decodeCheckAndTag(
                      model.getPayerId(), Arrays.asList(ApiIdentifiers.ACCOUNT_PUBKEY));
              rlpWriter.writeByteArray(payerIdWithTag);
              this.checkZeroAndWriteValue(rlpWriter, model.getNonce());
              this.checkZeroAndWriteValue(rlpWriter, model.getFee());
              rlpWriter.writeByteArray(EncodingUtils.decodeCheckWithIdentifier(model.getInnerTx()));
            });
    return encodedRlp;
  }

  @Override
  protected <T extends UnsignedTx> Single<T> createInternal() {
    throw new UnsupportedOperationException("Not possible");
  }

  @Override
  protected FeeCalculationModel getFeeCalculationModel() {
    return new PayingForFeeCalculationModel();
  }
}
