package com.kryptokrauts.aeternity.sdk.service.transaction.domain;

import com.kryptokrauts.aeternity.generated.model.PostTxResponse;
import com.kryptokrauts.aeternity.sdk.domain.GenericResultObject;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder(toBuilder = true)
@ToString
public class PostTransactionResult
    extends GenericResultObject<PostTxResponse, PostTransactionResult> {

  private String txHash;

  @Override
  protected PostTransactionResult map(PostTxResponse generatedResultObject) {
    if (generatedResultObject != null)
      return this.toBuilder().txHash(generatedResultObject.getTxHash()).build();
    else return this.toBuilder().build();
  }

  @Override
  protected String getResultObjectClassName() {
    return PostTransactionResult.class.getName();
  }
}
