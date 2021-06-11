package com.kryptokrauts.aeternity.sdk.service.info.domain;

import com.kryptokrauts.aeternity.generated.model.GenericSignedTx;
import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;
import com.kryptokrauts.aeternity.sdk.service.transaction.type.model.AbstractTransactionModel;
import java.math.BigInteger;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class TransactionResult extends GenericResultObject<GenericSignedTx, TransactionResult> {

  private BigInteger txVersion;

  private String txType;

  private BigInteger blockHeight;

  private String blockHash;

  private String hash;

  private List<String> signatures;

  private AbstractTransactionModel<?> resultTransactionModel;

  @Override
  protected TransactionResult map(GenericSignedTx generatedResultObject) {
    if (generatedResultObject != null)
      return this.toBuilder()
          .txType(generatedResultObject.getTx().getType())
          .txVersion(generatedResultObject.getTx().getVersion())
          .blockHash(generatedResultObject.getBlockHash())
          .blockHeight(generatedResultObject.getBlockHeight())
          .hash(generatedResultObject.getHash())
          .signatures(generatedResultObject.getSignatures())
          .resultTransactionModel(
              ApiModelMapper.mapToTransactionModel(generatedResultObject.getTx()))
          .build();
    else return this.toBuilder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return this.getClass().getName();
  }
}
